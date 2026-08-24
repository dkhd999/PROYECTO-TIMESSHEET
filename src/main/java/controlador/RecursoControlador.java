package controlador;

import modelo.DesarrolladorJunior;
import modelo.DesarrolladorSenior;
import modelo.Recurso;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class RecursoControlador {

    public void guardar(String nombre, String correo, String rol, double tarifaBase, String tipo) throws Exception {
        Recurso recurso = crear(0, nombre, correo, rol, tarifaBase, tipo, "Activo");
        recurso.guardar();
    }

    public void actualizar(int id, String nombre, String correo, String rol, double tarifaBase, String tipo, String estado) throws Exception {
        crear(id, nombre, correo, rol, tarifaBase, tipo, estado).actualizar();
    }

    public void eliminar(int id, String nombre, String correo, String rol, double tarifaBase, String tipo) throws Exception {
        crear(id, nombre, correo, rol, tarifaBase, tipo, "Activo").eliminar();
    }

    public DefaultTableModel obtenerTablaRecursos() throws Exception {
        DefaultTableModel tabla = new DefaultTableModel(new String[]{"ID", "Nombre", "Correo", "Rol", "Tarifa", "Tipo", "Estado"}, 0) {
            @Override public boolean isCellEditable(int fila, int columna) { return false; }
        };
        for (Recurso recurso : Recurso.listarTodos()) tabla.addRow(new Object[]{recurso.getId(), recurso.getNombre(), recurso.getCorreo(), recurso.getRol(), recurso.calcularTarifaHora(), recurso.getTipo(), recurso.getEstado()});
        return tabla;
    }

    public List<Recurso> listar() throws Exception { return Recurso.listarTodos(); }

    private Recurso crear(int id, String nombre, String correo, String rol, double tarifaBase, String tipo, String estado) throws Exception {
        if ("Senior".equalsIgnoreCase(tipo)) return new DesarrolladorSenior(id, nombre.trim(), correo.trim(), tarifaBase, estado);
        if ("Junior".equalsIgnoreCase(tipo)) return new DesarrolladorJunior(id, nombre.trim(), correo.trim(), tarifaBase, estado);
        throw new Exception("El tipo debe ser Junior o Senior.");
    }
}
