package controlador;

import modelo.HojaTiempo;
import modelo.DetalleActividad;
import modelo.Recurso;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import vista.HojaTiempoVista;

/**
 * Controlador de Hoja de Tiempo y Detalle de Actividad.
 * La Vista llama a estos métodos directamente; no tiene lógica propia.
 */
public class HojaTiempoControlador {

    private final HojaTiempoVista vista;
    private final String rolUsuario = "Desarrollador";

    public HojaTiempoControlador() {
        this.vista = null;
    }

    public HojaTiempoControlador(HojaTiempoVista vista) {
        this.vista = vista;
        vista.btnCrear.addActionListener(event -> guardarHojaDesdeVista());
        vista.btnAgregarDetalle.addActionListener(event -> guardarDetalleDesdeVista());
        vista.btnCambiarEstado.addActionListener(event -> cambiarEstadoDesdeVista());
        vista.btnEliminarHoja.addActionListener(event -> eliminarHojaDesdeVista());
        vista.btnFiltrar.addActionListener(event -> filtrarDesdeVista());
        vista.btnActualizarDetalle.addActionListener(event -> actualizarDetalleDesdeVista());
        vista.btnActualizarDetalle1.addActionListener(event -> eliminarDetalleDesdeVista());
        vista.tblHojas.getSelectionModel().addListSelectionListener(event -> cargarSeleccionDesdeVista());
        try {
            cargarTablaEnVista(null);
            actualizarPermisos(null);
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void guardarHojaDesdeVista() {
        try {
                guardarHoja(Integer.parseInt(vista.txtProyectoId.getText().trim()),
                    Integer.parseInt(vista.txtRecursoId.getText().trim()), vista.txtPeriodo.getText());
            mostrar("Hoja de Tiempo creada en estado Borrador.");
            cargarTablaEnVista(null);
        } catch (Exception ex) { mostrarError(ex); }
    }

    private void guardarDetalleDesdeVista() {
        try {
                int hojaId = Integer.parseInt(vista.txtHojaId.getText().trim());
                guardarDetalle(vista.txtFechaDetalle.getText(), vista.txtDescripcion.getText(),
                    vista.txtHoras.getText(), vista.txtModulo.getText(), hojaId);
            cargarSeleccionDesdeVista();
        } catch (Exception ex) { mostrarError(ex); }
    }

    private void cambiarEstadoDesdeVista() {
        try {
            int fila = filaSeleccionada();
                cambiarEstado((int) vista.tblHojas.getValueAt(fila, 0), (int) vista.tblHojas.getValueAt(fila, 1),
                    (int) vista.tblHojas.getValueAt(fila, 2), vista.tblHojas.getValueAt(fila, 3).toString(),
                    vista.cmbEstado.getSelectedItem().toString(), rolUsuario);
            mostrar("Estado actualizado correctamente.");
            cargarTablaEnVista(null);
        } catch (Exception ex) { mostrarError(ex); }
    }

    private void eliminarHojaDesdeVista() {
        try {
            int fila = filaSeleccionada();
                eliminarHoja((int) vista.tblHojas.getValueAt(fila, 0), (int) vista.tblHojas.getValueAt(fila, 1),
                    (int) vista.tblHojas.getValueAt(fila, 2), vista.tblHojas.getValueAt(fila, 3).toString(),
                    vista.tblHojas.getValueAt(fila, 4).toString());
            mostrar("Hoja de tiempo inactivada correctamente.");
            cargarTablaEnVista(null);
        } catch (Exception ex) { mostrarError(ex); }
    }

    private void filtrarDesdeVista() {
        try { cargarTablaEnVista(vista.txtFiltroPeriodo.getText()); }
        catch (Exception ex) { mostrarError(ex); }
    }

    private void actualizarDetalleDesdeVista() {
        try {
            int fila = vista.tblDetalles.getSelectedRow();
            if (fila < 0) throw new Exception("Selecciona un detalle de actividad.");
                actualizarDetalle((int) vista.tblDetalles.getValueAt(fila, 0), vista.txtFechaDetalle.getText(),
                    vista.txtDescripcion.getText(), Double.parseDouble(vista.txtHoras.getText().trim().replace(',', '.')),
                    vista.txtModulo.getText(), Integer.parseInt(vista.txtHojaId.getText().trim()));
            cargarSeleccionDesdeVista();
        } catch (Exception ex) { mostrarError(ex); }
    }

    private void eliminarDetalleDesdeVista() {
        try {
            int fila = vista.tblDetalles.getSelectedRow();
            if (fila < 0) throw new Exception("Selecciona un detalle de actividad.");
            eliminarDetalle((int) vista.tblDetalles.getValueAt(fila, 0));
            cargarSeleccionDesdeVista();
        } catch (Exception ex) { mostrarError(ex); }
    }

    private void cargarSeleccionDesdeVista() {
        int fila = vista.tblHojas.getSelectedRow();
        if (fila < 0) return;
        try {
            int hojaId = (int) vista.tblHojas.getValueAt(fila, 0);
            int recursoId = (int) vista.tblHojas.getValueAt(fila, 2);
            vista.txtHojaId.setText(String.valueOf(hojaId));
            vista.txtProyectoId.setText(String.valueOf(vista.tblHojas.getValueAt(fila, 1)));
            vista.txtRecursoId.setText(String.valueOf(recursoId));
            vista.txtPeriodo.setText(vista.tblHojas.getValueAt(fila, 3).toString());
            String estado = vista.tblHojas.getValueAt(fila, 4).toString();
            vista.cmbEstado.setSelectedItem(estado);
            vista.tblDetalles.setModel(obtenerTablaDetalles(hojaId));
            vista.lblTotalHoras.setText("Total horas: " + calcularTotalHoras(hojaId));
            vista.lblCostoTotal.setText("Costo total: $" + String.format("%.2f", calcularCostoTotal(hojaId, recursoId)));
            actualizarPermisos(estado);
        } catch (Exception ex) { mostrarError(ex); }
    }

    private void cargarTablaEnVista(String periodo) throws Exception {
        vista.tblHojas.setModel(cargarHojas(periodo));
        vista.tblDetalles.setModel(new DefaultTableModel());
        vista.lblTotalHoras.setText("Total horas: 0");
        vista.lblCostoTotal.setText("Costo total: $0.00");
    }

    private void actualizarPermisos(String estado) {
        boolean puedeEditar = estado != null && ("Borrador".equalsIgnoreCase(estado) || "Rechazada".equalsIgnoreCase(estado));
        vista.btnAgregarDetalle.setEnabled(puedeEditar);
        vista.btnActualizarDetalle.setEnabled(puedeEditar);
        vista.btnActualizarDetalle1.setEnabled(puedeEditar);
        vista.btnEliminarHoja.setEnabled("Borrador".equalsIgnoreCase(estado));
        vista.btnCambiarEstado.setEnabled(("Desarrollador".equalsIgnoreCase(rolUsuario) && puedeEditar)
            || (!"Desarrollador".equalsIgnoreCase(rolUsuario) && "Enviada".equalsIgnoreCase(estado)));
    }

    private int filaSeleccionada() throws Exception {
        int fila = vista.tblHojas.getSelectedRow();
        if (fila < 0) throw new Exception("Selecciona una hoja de tiempo.");
        return fila;
    }

    private void mostrar(String mensaje) { javax.swing.JOptionPane.showMessageDialog(vista, mensaje); }
    private void mostrarError(Exception ex) { javax.swing.JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage()); }

    // ─────── HOJA DE TIEMPO ───────
    public void guardarHoja(int proyectoId, int recursoId, String periodo) throws Exception {
        HojaTiempo h = new HojaTiempo();
        h.setProyectoId(proyectoId);
        h.setRecursoId(recursoId);
        h.setPeriodo(periodo.trim());
        h.guardar();
    }

    public void cambiarEstado(int id, int proyectoId, int recursoId,
                              String periodo, String nuevoEstado) throws Exception {
        cambiarEstado(id, proyectoId, recursoId, periodo, nuevoEstado, "Desarrollador");
    }

    public void cambiarEstado(int id, int proyectoId, int recursoId,
                              String periodo, String nuevoEstado, String rol) throws Exception {
        HojaTiempo h = new HojaTiempo(id, proyectoId, recursoId, periodo, obtenerEstado(id));
        h.cambiarEstado(nuevoEstado, rol);
    }

    private String obtenerEstado(int id) throws Exception {
        for (HojaTiempo hoja : HojaTiempo.listarTodas()) if (hoja.getId() == id) return hoja.getEstado();
        throw new Exception("No se encontró la hoja de tiempo.");
    }

    public void eliminarHoja(int id, int proyectoId, int recursoId,
                             String periodo, String estado) throws Exception {
        HojaTiempo h = new HojaTiempo(id, proyectoId, recursoId, periodo, estado);
        h.eliminar();
    }

    public DefaultTableModel obtenerTablaHojas() throws Exception {
        String[] cols = {"ID", "Proyecto ID", "Recurso ID", "Periodo", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (HojaTiempo h : HojaTiempo.listarTodas()) {
            modelo.addRow(new Object[]{
                h.getId(), h.getProyectoId(), h.getRecursoId(),
                h.getPeriodo(), h.getEstado()
            });
        }
        return modelo;
    }

    public DefaultTableModel cargarHojas(String periodo) throws Exception {
        List<HojaTiempo> hojas = listarPorFiltro(null, null,
                periodo == null || periodo.trim().isEmpty() ? null : periodo.trim());
        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{"ID", "Proyecto ID", "Recurso ID", "Periodo", "Estado"}, 0) {
            @Override public boolean isCellEditable(int fila, int columna) { return false; }
        };
        for (HojaTiempo hoja : hojas) {
            modelo.addRow(new Object[]{hoja.getId(), hoja.getProyectoId(),
                hoja.getRecursoId(), hoja.getPeriodo(), hoja.getEstado()});
        }
        return modelo;
    }

    public List<HojaTiempo> listarPorFiltro(Integer proyectoId, Integer recursoId, String periodo) throws Exception {
        return HojaTiempo.listarPorFiltro(proyectoId, recursoId, periodo);
    }

    // ─────── DETALLE DE ACTIVIDAD ───────
    public void guardarDetalle(String fecha, String descripcion,
                               String horasStr, String modulo, int hojaTiempoId) throws Exception {
        double horas;
        try {
            horas = Double.parseDouble(horasStr.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new Exception("Las horas deben ser un número válido (ej: 8 o 7.5).");
        }
        DetalleActividad d = new DetalleActividad();
        d.setFecha(fecha.trim());
        d.setDescripcion(descripcion.trim());
        d.setHoras(horas);
        d.setModulo(modulo.trim());
        d.setHojaTiempoId(hojaTiempoId);
        d.guardar();
    }

    public void eliminarDetalle(int idDetalle) throws Exception {
        DetalleActividad d = new DetalleActividad();
        d.setId(idDetalle);
        d.eliminar();
    }

    public void actualizarDetalle(int id, String fecha, String descripcion, double horas, String modulo, int hojaId) throws Exception {
        DetalleActividad d = new DetalleActividad(id, fecha.trim(), descripcion.trim(), horas, modulo.trim(), hojaId);
        d.actualizar();
    }

    public void asignarRecurso(int proyectoId, int recursoId) throws Exception {
        modelo.Proyecto.asignarRecurso(proyectoId, recursoId);
    }

    public double costoAcumuladoProyecto(int proyectoId) throws Exception {
        return modelo.HojaTiempo.costoAcumuladoProyecto(proyectoId);
    }

    public DefaultTableModel obtenerTablaDetalles(int hojaTiempoId) throws Exception {
        String[] cols = {"ID", "Fecha", "Descripción", "Horas", "Módulo"};
        DefaultTableModel modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (DetalleActividad d : DetalleActividad.listarPorHoja(hojaTiempoId)) {
            modelo.addRow(new Object[]{
                d.getId(), d.getFecha(), d.getDescripcion(),
                d.getHoras(), d.getModulo()
            });
        }
        return modelo;
    }

    // ─────── CALCULAR TOTALES ───────
    public double calcularTotalHoras(int hojaTiempoId) throws Exception {
        HojaTiempo h = new HojaTiempo();
        h.setId(hojaTiempoId);
        h.cargarDetalles();
        return h.calcularTotalHoras();
    }

    public double calcularCostoTotal(int hojaTiempoId, int recursoId) throws Exception {
        HojaTiempo h = new HojaTiempo();
        h.setId(hojaTiempoId);
        h.cargarDetalles();
        List<Recurso> recursos = Recurso.listarTodos();
        for (Recurso r : recursos) {
            if (r.getId() == recursoId) return h.calcularCostoTotal(r);
        }
        return 0;
    }
}
