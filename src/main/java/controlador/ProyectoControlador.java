package controlador;

import modelo.Proyecto;
import vista.ProyectoVista;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;

/**
 * Controlador único de Proyectos.
 * Gestiona los eventos de la vista y la preparación de datos para la interfaz y el modelo.
 */
public class ProyectoControlador {

    private ProyectoVista vista;

    // Constructor por defecto (para llamadas de utilidades o combos desde otros controladores)
    public ProyectoControlador() {}

    // Constructor con Vista (vinculará los eventos e inicializará la tabla)
    public ProyectoControlador(ProyectoVista vista) {
        this.vista = vista;
        inicializarEventos();
        cargarTabla();
    }

    private void inicializarEventos() {
        if (vista == null) return;
        vista.getBtnGuardar().addActionListener(event -> guardarDesdeVista());
        vista.getBtnEliminar().addActionListener(event -> eliminarDesdeVista());
        vista.getBtnHabilitar().addActionListener(event -> habilitarDesdeVista());
    }

    // ─────── ACCIONES DESDE LA VISTA ───────
    public void cargarTabla() {
        if (vista == null) return;
        try {
            vista.getTblProyectos().setModel(obtenerTablaProyectos());
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void guardarDesdeVista() {
        try {
            guardar(vista.getTxtCodigo().getText(), vista.getTxtNombre().getText(),
                    vista.getTxtCliente().getText(), vista.getTxtFechaInicio().getText(), 
                    vista.getTxtFechaFin().getText());
            mostrar("Proyecto guardado con éxito.");
            limpiarCampos();
            cargarTabla();
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void eliminarDesdeVista() {
        try {
            Proyecto proyecto = proyectoSeleccionado();
            proyecto.eliminar();
            mostrar("Proyecto inactivado.");
            cargarTabla();
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void habilitarDesdeVista() {
        try {
            Proyecto proyecto = proyectoSeleccionado();
            proyecto.activar();
            mostrar("Proyecto habilitado correctamente.");
            cargarTabla();
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    // ─────── MÉTODOS CRUD DIRECTOS ───────
    public void guardar(String codigo, String nombre, String cliente, String fechaInicio, String fechaFin) throws Exception {
        Proyecto p = new Proyecto();
        p.setCodigo(codigo.trim());
        p.setNombre(nombre.trim());
        p.setCliente(cliente.trim());
        p.setFechaInicio(fechaInicio.trim());
        p.setFechaFin(fechaFin.trim());
        p.guardar();
    }

    public void actualizar(int id, String codigo, String nombre, String cliente, String fechaInicio, String fechaFin, String estado) throws Exception {
        Proyecto p = new Proyecto(id, codigo.trim(), nombre.trim(), cliente.trim(), fechaInicio.trim(), fechaFin.trim(), estado);
        p.actualizar();
    }

    public void eliminar(int id, String codigo, String nombre, String cliente, String fechaInicio, String fechaFin, String estado) throws Exception {
        Proyecto p = new Proyecto(id, codigo, nombre, cliente, fechaInicio, fechaFin, estado);
        p.eliminar();
    }

    public void activar(int id, String codigo, String nombre, String cliente, String fechaInicio, String fechaFin) throws Exception {
        Proyecto p = new Proyecto(id, codigo, nombre, cliente, fechaInicio, fechaFin, "Inactivo");
        p.activar();
    }

    // ─────── GENERADORES DE MODELOS PARA SWING ───────
    public DefaultTableModel obtenerTablaProyectos() throws Exception {
        String[] columnas = {"ID", "Código", "Nombre", "Cliente", "Fecha Inicio", "Fecha Fin", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        for (Proyecto p : Proyecto.listarTodos()) {
            modelo.addRow(new Object[]{
                p.getId(), p.getCodigo(), p.getNombre(), p.getCliente(),
                p.getFechaInicio(), p.getFechaFin(), p.getEstado()
            });
        }
        return modelo;
    }

    public DefaultComboBoxModel<Proyecto> obtenerComboProyectos() throws Exception {
        DefaultComboBoxModel<Proyecto> combo = new DefaultComboBoxModel<>();
        for (Proyecto p : Proyecto.listarTodos()) {
            if ("Activo".equalsIgnoreCase(p.getEstado())) combo.addElement(p);
        }
        return combo;
    }

    public Proyecto consultar(int id) throws Exception { 
        return Proyecto.consultar(id); 
    }

    public void asignarRecurso(int proyectoId, int recursoId) throws Exception {
        Proyecto.asignarRecurso(proyectoId, recursoId);
    }

    public DefaultTableModel obtenerTablaAsignaciones() throws Exception {
        DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"Proyecto ID", "Código", "Proyecto", "Recurso ID", "Recurso", "Tipo"}, 0) {
                @Override public boolean isCellEditable(int fila, int columna) { return false; }
            };
        for (Object[] fila : Proyecto.listarAsignaciones()) modelo.addRow(fila);
        return modelo;
    }

    // ─────── AUXILIARES DE INTERFAZ ───────
    private Proyecto proyectoSeleccionado() throws Exception {
        int fila = vista.getTblProyectos().getSelectedRow();
        if (fila < 0) throw new Exception("Selecciona un proyecto de la tabla.");
        return new Proyecto((int) vista.getTblProyectos().getValueAt(fila, 0),
            vista.getTblProyectos().getValueAt(fila, 1).toString(),
            vista.getTblProyectos().getValueAt(fila, 2).toString(),
            vista.getTblProyectos().getValueAt(fila, 3).toString(),
            vista.getTblProyectos().getValueAt(fila, 4).toString(),
            vista.getTblProyectos().getValueAt(fila, 5).toString(),
            vista.getTblProyectos().getValueAt(fila, 6).toString());
    }

    private void limpiarCampos() {
        vista.getTxtCodigo().setText("");
        vista.getTxtNombre().setText("");
        vista.getTxtCliente().setText("");
        vista.getTxtFechaInicio().setText("");
        vista.getTxtFechaFin().setText("");
        vista.getTblProyectos().clearSelection();
    }

    private void mostrar(String mensaje) { javax.swing.JOptionPane.showMessageDialog(vista, mensaje); }
    private void mostrarError(Exception ex) { javax.swing.JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage()); }
}