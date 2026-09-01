
package controlador;
import modelo.HojaTiempo;
import modelo.DetalleActividad;
import modelo.Recurso;
import modelo.Proyecto;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import vista.HojaTiempoVista;

/**
 * Controlador de Hoja de Tiempo y Detalle de Actividad.
 * La Vista llama a estos métodos directamente; no tiene lógica propia.
 */
public class HojaTiempoControlador {

    private final HojaTiempoVista vista;
    private final String rolUsuario;
    private final Integer recursoUsuarioId;

    public HojaTiempoControlador() {
        this.vista = null;
        this.rolUsuario = "Desarrollador";
        this.recursoUsuarioId = null;
    }

    public HojaTiempoControlador(HojaTiempoVista vista) {
        this(vista, "Desarrollador", null);
    }

    public HojaTiempoControlador(HojaTiempoVista vista, String rolUsuario) {
        this(vista, rolUsuario, null);
    }

    public HojaTiempoControlador(HojaTiempoVista vista, String rolUsuario, Integer recursoUsuarioId) {
        this.vista = vista;
        this.rolUsuario = rolUsuario;
        this.recursoUsuarioId = recursoUsuarioId;
        if (recursoUsuarioId != null) {
            vista.getTxtRecursoId().setText(String.valueOf(recursoUsuarioId));
            vista.getTxtRecursoId().setEditable(false);
        }
        configurarPeriodoActual();
        configurarProyectosAsignados();
        vista.getBtnCrear().addActionListener(event -> guardarHojaDesdeVista());
        vista.getBtnActualizarHoja().addActionListener(event -> actualizarHojaDesdeVista());
        vista.getBtnAgregarDetalle().addActionListener(event -> guardarDetalleDesdeVista());
        vista.getBtnCambiarEstado().addActionListener(event -> cambiarEstadoDesdeVista());
        vista.getBtnEliminarHoja().addActionListener(event -> eliminarHojaDesdeVista());
        vista.getBtnActualizarDetalle().addActionListener(event -> actualizarDetalleDesdeVista());
        vista.getBtnActualizarDetalle1().addActionListener(event -> eliminarDetalleDesdeVista());
        vista.getTblHojas().getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                javax.swing.SwingUtilities.invokeLater(this::cargarSeleccionDesdeVista);
            }
        });
        vista.getTblDetalles().getSelectionModel().addListSelectionListener(event -> cargarDetalleSeleccionado());
        try {
            cargarTablaEnVista(null);
            actualizarPermisos(null);
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void configurarPeriodoActual() {
        LocalDate lunes = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate viernes = lunes.plusDays(4);
        vista.getTxtPeriodo().setText(lunes + " / " + viernes);
        vista.getTxtPeriodo().setEditable(false);
        vista.getTxtHojaId().setEditable(false);
    }

    private void configurarProyectosAsignados() {
        try {
            javax.swing.DefaultComboBoxModel<Proyecto> modelo = new javax.swing.DefaultComboBoxModel<>();
            if (recursoUsuarioId != null) {
                for (Proyecto proyecto : Proyecto.listarPorRecurso(recursoUsuarioId)) modelo.addElement(proyecto);
            } else {
                for (Proyecto proyecto : Proyecto.listarTodos()) {
                    if ("Activo".equalsIgnoreCase(proyecto.getEstado())) modelo.addElement(proyecto);
                }
            }
            vista.getCmbProyectoAsignado().setModel(modelo);
            vista.getCmbProyectoAsignado().addActionListener(event -> seleccionarProyecto());
            seleccionarProyecto();
        } catch (Exception ex) { mostrarError(ex); }
    }

    private void seleccionarProyecto() {
        Proyecto proyecto = (Proyecto) vista.getCmbProyectoAsignado().getSelectedItem();
        vista.getTxtProyectoId().setText(proyecto == null ? "" : String.valueOf(proyecto.getId()));
        vista.getTxtProyectoId().setEditable(false);
    }

    private void guardarHojaDesdeVista() {
        try {
                int recursoId = Integer.parseInt(vista.getTxtRecursoId().getText().trim());
                validarRecursoUsuario(recursoId);
                guardarHoja(Integer.parseInt(vista.getTxtProyectoId().getText().trim()), recursoId, vista.getTxtPeriodo().getText());
            mostrar("Hoja de Tiempo creada en estado Borrador.");
            cargarTablaEnVista(null);
            if (vista.getTblHojas().getRowCount() > 0) {
                vista.getTblHojas().setRowSelectionInterval(0, 0);
            }
            limpiarCamposDetalle();
        } catch (Exception ex) { mostrarError(ex); }
    }
private void guardarDetalleDesdeVista() {
        try {
            // 1. Obtiene el ID de la hoja seleccionada
            int hojaId = Integer.parseInt(vista.getTxtHojaId().getText().trim());
            
            // 2. Llama al método que guarda en la base de datos
            guardarDetalle(
                vista.getTxtFechaDetalle().getText(), 
                vista.getTxtDescripcion().getText(),
                vista.getTxtHoras().getText(), 
                vista.getTxtModulo().getText(), 
                hojaId
            );
            
            // 3. Refresca la tabla y los campos de la vista
            cargarSeleccionDesdeVista();
            
        } catch (Exception ex) { 
            mostrarError(ex); 
        }
    }
    private void actualizarHojaDesdeVista() {
        try {
            int fila = filaSeleccionada();
            int hojaId = (int) vista.getTblHojas().getValueAt(fila, 0);
            int recursoId = (int) vista.getTblHojas().getValueAt(fila, 3);
            validarRecursoUsuario(recursoId);
            actualizarHoja(hojaId, Integer.parseInt(vista.getTxtProyectoId().getText().trim()),
                    recursoId, vista.getTxtPeriodo().getText().trim(),
                    vista.getTblHojas().getValueAt(fila, 5).toString());
            cargarTablaEnVista(null);
            seleccionarHojaPorId(hojaId);
            mostrar("Hoja actualizada correctamente.");
        } catch (Exception ex) { mostrarError(ex); }
    }

    private void cambiarEstadoDesdeVista() {
        try {
            if (!"Desarrollador".equalsIgnoreCase(rolUsuario))
                throw new Exception("Solo el desarrollador puede enviar la hoja.");
            int fila = filaSeleccionada();
            int hojaId = (int) vista.getTblHojas().getValueAt(fila, 0);
            cambiarEstado(hojaId, (int) vista.getTblHojas().getValueAt(fila, 1),
                    (int) vista.getTblHojas().getValueAt(fila, 3), vista.getTblHojas().getValueAt(fila, 4).toString(),
                    "Enviada", rolUsuario);
            cargarTablaEnVista(null);
            seleccionarHojaPorId(hojaId);
            mostrar("Hoja enviada correctamente. Total horas: " + calcularTotalHoras(hojaId)
                    + " | Costo total: $" + String.format("%.2f", calcularCostoTotal(hojaId,
                    (int) vista.getTblHojas().getValueAt(vista.getTblHojas().getSelectedRow(), 3))));
        } catch (Exception ex) { mostrarError(ex); }
    }

    private void seleccionarHojaPorId(int hojaId) {
        for (int fila = 0; fila < vista.getTblHojas().getRowCount(); fila++) {
            if ((int) vista.getTblHojas().getValueAt(fila, 0) == hojaId) {
                vista.getTblHojas().setRowSelectionInterval(fila, fila);
                return;
            }
        }
    }

    private void limpiarCamposDetalle() {
        vista.getTxtFechaDetalle().setText("");
        vista.getTxtDescripcion().setText("");
        vista.getTxtHoras().setText("");
        vista.getTxtModulo().setText("");
    }

    private void eliminarHojaDesdeVista() {
        try {
            int fila = filaSeleccionada();
                eliminarHoja((int) vista.getTblHojas().getValueAt(fila, 0), (int) vista.getTblHojas().getValueAt(fila, 1),
                    (int) vista.getTblHojas().getValueAt(fila, 3), vista.getTblHojas().getValueAt(fila, 4).toString(),
                    vista.getTblHojas().getValueAt(fila, 5).toString());
            mostrar("Hoja de tiempo inactivada correctamente.");
            cargarTablaEnVista(null);
        } catch (Exception ex) { mostrarError(ex); }
    }

    private void actualizarDetalleDesdeVista() {
        try {
            int fila = vista.getTblDetalles().getSelectedRow();
            if (fila < 0) throw new Exception("Selecciona un detalle de actividad.");
                actualizarDetalle((int) vista.getTblDetalles().getValueAt(fila, 0), vista.getTxtFechaDetalle().getText(),
                    vista.getTxtDescripcion().getText(), Double.parseDouble(vista.getTxtHoras().getText().trim().replace(',', '.')),
                    vista.getTxtModulo().getText(), Integer.parseInt(vista.getTxtHojaId().getText().trim()));
            cargarSeleccionDesdeVista();
        } catch (Exception ex) { mostrarError(ex); }
    }

    private void eliminarDetalleDesdeVista() {
        try {
            int fila = vista.getTblDetalles().getSelectedRow();
            if (fila < 0) throw new Exception("Selecciona un detalle de actividad.");
            eliminarDetalle((int) vista.getTblDetalles().getValueAt(fila, 0));
            cargarSeleccionDesdeVista();
        } catch (Exception ex) { mostrarError(ex); }
    }

    private void cargarSeleccionDesdeVista() {
        int fila = vista.getTblHojas().getSelectedRow();
        if (fila < 0) {
            vista.getTblDetalles().setModel(new DefaultTableModel());
            return;
        }
        try {
            int hojaId = (int) vista.getTblHojas().getValueAt(fila, 0);
            int recursoId = (int) vista.getTblHojas().getValueAt(fila, 3);
            vista.getTxtHojaId().setText(String.valueOf(hojaId));
            vista.getTxtProyectoId().setText(String.valueOf(vista.getTblHojas().getValueAt(fila, 1)));
            vista.getTxtRecursoId().setText(String.valueOf(recursoId));
            vista.getTxtPeriodo().setText(vista.getTblHojas().getValueAt(fila, 4).toString());
            String estado = vista.getTblHojas().getValueAt(fila, 5).toString();
            vista.getTblDetalles().setModel(obtenerTablaDetalles(hojaId));
            if (vista.getTblDetalles().getRowCount() > 0) {
                vista.getTblDetalles().setRowSelectionInterval(0, 0);
                cargarDetalleSeleccionado();
            } else {
                limpiarCamposDetalle();
            }
            vista.getLblTotalHoras().setText("Total horas: " + calcularTotalHoras(hojaId));
            vista.getLblCostoTotal().setText("Costo total: $" + String.format("%.2f", calcularCostoTotal(hojaId, recursoId)));
            actualizarPermisos(estado);
        } catch (Exception ex) { mostrarError(ex); }
    }

    private void cargarDetalleSeleccionado() {
        int fila = vista.getTblDetalles().getSelectedRow();
        if (fila < 0) {
            return;
        }
        vista.getTxtFechaDetalle().setText(String.valueOf(vista.getTblDetalles().getValueAt(fila, 1)));
        vista.getTxtDescripcion().setText(String.valueOf(vista.getTblDetalles().getValueAt(fila, 2)));
        vista.getTxtHoras().setText(String.valueOf(vista.getTblDetalles().getValueAt(fila, 3)));
        vista.getTxtModulo().setText(String.valueOf(vista.getTblDetalles().getValueAt(fila, 4)));
    }

    private void cargarTablaEnVista(String periodo) throws Exception {
        vista.getTblHojas().setModel(cargarHojas(periodo, recursoUsuarioId));
        vista.getTblDetalles().setModel(new DefaultTableModel());
        vista.getLblTotalHoras().setText("Total horas: 0");
        vista.getLblCostoTotal().setText("Costo total: $0.00");
    }

    private void actualizarPermisos(String estado) {
        boolean puedeEditar = estado != null && ("Borrador".equalsIgnoreCase(estado) || "Rechazada".equalsIgnoreCase(estado));
        vista.getBtnAgregarDetalle().setEnabled(puedeEditar);
        vista.getBtnActualizarDetalle().setEnabled(puedeEditar);
        vista.getBtnActualizarDetalle1().setEnabled(puedeEditar);
        vista.getBtnEliminarHoja().setEnabled("Borrador".equalsIgnoreCase(estado));
        vista.getBtnCambiarEstado().setEnabled("Desarrollador".equalsIgnoreCase(rolUsuario) && puedeEditar);
    }

    private int filaSeleccionada() throws Exception {
        int fila = vista.getTblHojas().getSelectedRow();
        if (fila < 0) throw new Exception("Selecciona una hoja de tiempo.");
        return fila;
    }

    private void validarRecursoUsuario(int recursoId) throws Exception {
        if (recursoUsuarioId != null && recursoUsuarioId.intValue() != recursoId)
            throw new Exception("No puedes crear una hoja para otro recurso.");
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

    public void actualizarHoja(int id, int proyectoId, int recursoId,
                               String periodo, String estado) throws Exception {
        HojaTiempo h = new HojaTiempo(id, proyectoId, recursoId, periodo, estado);
        h.actualizar();
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
        String[] cols = {"ID", "Proyecto ID", "Proyecto", "Recurso ID", "Periodo", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (HojaTiempo h : HojaTiempo.listarTodas()) {
            modelo.addRow(new Object[]{
                h.getId(), h.getProyectoId(), h.getProyectoNombre(), h.getRecursoId(),
                h.getPeriodo(), h.getEstado()
            });
        }
        return modelo;
    }

    public DefaultTableModel cargarHojas(String periodo) throws Exception {
        return cargarHojas(periodo, null);
    }

    public DefaultTableModel cargarHojas(String periodo, Integer recursoId) throws Exception {
        List<HojaTiempo> hojas = listarPorFiltro(null, recursoId,
                periodo == null || periodo.trim().isEmpty() ? null : periodo.trim());
        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{"ID", "Proyecto ID", "Proyecto", "Recurso ID", "Periodo", "Estado"}, 0) {
            @Override public boolean isCellEditable(int fila, int columna) { return false; }
        };
        for (HojaTiempo hoja : hojas) {
            modelo.addRow(new Object[]{hoja.getId(), hoja.getProyectoId(), hoja.getProyectoNombre(),
                hoja.getRecursoId(), hoja.getPeriodo(), hoja.getEstado()});
        }
        return modelo;
    }

    public List<HojaTiempo> listarPorFiltro(Integer proyectoId, Integer recursoId, String periodo) throws Exception {
        return HojaTiempo.listarPorFiltro(proyectoId, recursoId, periodo);
    }

    
    public void guardarDetalle(String fecha, String descripcion, String horasStr, String modulo, int hojaTiempoId) throws Exception {
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
