package controlador;

import modelo.Proyecto;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;

/**
 * Controlador de Proyectos.
 * La Vista llama a estos métodos directamente; no tiene lógica propia.
 */
public class ProyectoControlador {

    // ─────── GUARDAR (nuevo) ───────
    public void guardar(String codigo, String nombre, String cliente,
                        String fechaInicio, String fechaFin) throws Exception {
        Proyecto p = new Proyecto();
        p.setCodigo(codigo.trim());
        p.setNombre(nombre.trim());
        p.setCliente(cliente.trim());
        p.setFechaInicio(fechaInicio.trim());
        p.setFechaFin(fechaFin.trim());
        p.guardar();
    }

    // ─────── ACTUALIZAR ───────
    public void actualizar(int id, String codigo, String nombre, String cliente,
                           String fechaInicio, String fechaFin, String estado) throws Exception {
        Proyecto p = new Proyecto(id, codigo.trim(), nombre.trim(), cliente.trim(),
                                  fechaInicio.trim(), fechaFin.trim(), estado);
        p.actualizar();
    }

    // ─────── ELIMINAR (inactivar) ───────
    public void eliminar(int id, String codigo, String nombre, String cliente,
                         String fechaInicio, String fechaFin, String estado) throws Exception {
        Proyecto p = new Proyecto(id, codigo, nombre, cliente, fechaInicio, fechaFin, estado);
        p.eliminar();
    }

    // ─────── LLENAR TABLA ───────
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

    // ─────── LLENAR COMBOBOX (para otras vistas) ───────
    public DefaultComboBoxModel<Proyecto> obtenerComboProyectos() throws Exception {
        DefaultComboBoxModel<Proyecto> combo = new DefaultComboBoxModel<>();
        for (Proyecto p : Proyecto.listarTodos()) {
            if ("Activo".equalsIgnoreCase(p.getEstado())) combo.addElement(p);
        }
        return combo;
    }

    public Proyecto consultar(int id) throws Exception { return Proyecto.consultar(id); }

    public void asignarRecurso(int proyectoId, int recursoId) throws Exception {
        Proyecto.asignarRecurso(proyectoId, recursoId);
    }
    
    
}
