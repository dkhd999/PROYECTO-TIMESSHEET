package controlador;

import modelo.Proyecto;
import vista.ProyectoVista;

/** Coordina ProyectoVista y delega las reglas al modelo Proyecto. */
public class ProyectoVistaControlador {

    private final ProyectoVista vista;
    private final ProyectoControlador proyectos = new ProyectoControlador();

    public ProyectoVistaControlador(ProyectoVista vista) {
        this.vista = vista;
        vista.btnGuardar.addActionListener(event -> guardar());
        vista.btnEliminar.addActionListener(event -> eliminar());
        vista.btnHabilitar.addActionListener(event -> habilitar());
        cargarTabla();
    }

    private void cargarTabla() {
        try {
            vista.tblProyectos.setModel(proyectos.obtenerTablaProyectos());
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void guardar() {
        try {
            proyectos.guardar(vista.txtCodigo.getText(), vista.txtNombre.getText(),
                    vista.txtCliente.getText(), vista.txtFechaInicio.getText(), vista.txtFechaFin.getText());
            mostrar("Proyecto guardado con éxito.");
            limpiarCampos();
            cargarTabla();
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void eliminar() {
        try {
            Proyecto proyecto = proyectoSeleccionado();
            proyectos.eliminar(proyecto.getId(), proyecto.getCodigo(), proyecto.getNombre(),
                    proyecto.getCliente(), proyecto.getFechaInicio(), proyecto.getFechaFin(), proyecto.getEstado());
            mostrar("Proyecto inactivado.");
            cargarTabla();
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void habilitar() {
        try {
            Proyecto proyecto = proyectoSeleccionado();
            proyectos.activar(proyecto.getId(), proyecto.getCodigo(), proyecto.getNombre(),
                    proyecto.getCliente(), proyecto.getFechaInicio(), proyecto.getFechaFin());
            mostrar("Proyecto habilitado correctamente.");
            cargarTabla();
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private Proyecto proyectoSeleccionado() throws Exception {
        int fila = vista.tblProyectos.getSelectedRow();
        if (fila < 0) throw new Exception("Selecciona un proyecto de la tabla.");
        return new Proyecto((int) vista.tblProyectos.getValueAt(fila, 0),
                vista.tblProyectos.getValueAt(fila, 1).toString(),
                vista.tblProyectos.getValueAt(fila, 2).toString(),
                vista.tblProyectos.getValueAt(fila, 3).toString(),
                vista.tblProyectos.getValueAt(fila, 4).toString(),
                vista.tblProyectos.getValueAt(fila, 5).toString(),
                vista.tblProyectos.getValueAt(fila, 6).toString());
    }

    private void limpiarCampos() {
        vista.txtCodigo.setText("");
        vista.txtNombre.setText("");
        vista.txtCliente.setText("");
        vista.txtFechaInicio.setText("");
        vista.txtFechaFin.setText("");
        vista.tblProyectos.clearSelection();
    }

    private void mostrar(String mensaje) { javax.swing.JOptionPane.showMessageDialog(vista, mensaje); }
    private void mostrarError(Exception ex) { javax.swing.JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage()); }
}
