package controlador;

import modelo.Proyecto;
import modelo.Recurso;
import vista.AsignacionVista;

public class AsignacionControlador {

    private final AsignacionVista vista;
    private final ProyectoControlador proyectos = new ProyectoControlador();
    private final RecursoControlador recursos = new RecursoControlador();

    public AsignacionControlador(AsignacionVista vista) {
        this.vista = vista;
        vista.getBtnAsignar().addActionListener(event -> asignar());
        cargarDatos();
    }

    private void cargarDatos() {
        try {
            vista.getCmbProyectos().setModel(proyectos.obtenerComboProyectos());
            vista.getCmbRecursos().setModel(recursos.cargarRecursos());
            vista.getTblAsignaciones().setModel(proyectos.obtenerTablaAsignaciones());
            if (vista.getCmbProyectos().getItemCount() == 0
                    || vista.getCmbRecursos().getItemCount() == 0) {
                javax.swing.JOptionPane.showMessageDialog(vista,
                        "Debes tener al menos un proyecto y un recurso activo.");
            }
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void asignar() {
        try {
            Proyecto proyecto = (Proyecto) vista.getCmbProyectos().getSelectedItem();
            Recurso recurso = (Recurso) vista.getCmbRecursos().getSelectedItem();
            if (proyecto == null || recurso == null) {
                javax.swing.JOptionPane.showMessageDialog(vista,
                        "Debes tener al menos un proyecto y un recurso activo.");
                return;
            }
            proyectos.asignarRecurso(proyecto.getId(), recurso.getId());
            javax.swing.JOptionPane.showMessageDialog(vista,
                    "Recurso asignado correctamente.");
            vista.getTblAsignaciones().setModel(proyectos.obtenerTablaAsignaciones());
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void mostrarError(Exception ex) {
        javax.swing.JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage());
    }
}
