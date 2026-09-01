package controlador;

import modelo.DesarrolladorJunior;
import modelo.DesarrolladorSenior;
import modelo.Recurso;
import modelo.ValidarCedula;
import vista.RecursoVista;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class RecursoControlador {

    private RecursoVista vista;

    // Constructor sin vista 
    public RecursoControlador() {}

    // Constructor vinculante con la vista
    public RecursoControlador(RecursoVista vista) {
        this.vista = vista;
        inicializarVistaYEventos();
    }

    private void inicializarVistaYEventos() {
        if (vista == null) return;

        vista.getTxtRol().setText("Desarrollador");
        vista.getTxtRol().setEditable(false);
        
        vista.getCmbTipo().addActionListener(event -> actualizarTarifa());
        actualizarTarifa();
        configurarTarifaNumerica();

        vista.getBtnGuardar().addActionListener(event -> guardarDesdeVista());
        vista.getBtnEliminar().addActionListener(event -> eliminarDesdeVista());
        vista.getBtnHabilitar().addActionListener(event -> habilitarDesdeVista());

        vista.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent event) {
                cargarTabla();
            }
        });

        cargarTabla();
    }

    // EVENTOS Y MANEJO DE VISTA
    private void actualizarTarifa() {
        String tipo = String.valueOf(vista.getCmbTipo().getSelectedItem());
        vista.getTxtTarifaBase().setText("Senior".equalsIgnoreCase(tipo) ? "60.00" : "25.00");
        vista.getTxtTarifaBase().setEditable(false);
    }

    private void configurarTarifaNumerica() {
        ((AbstractDocument) vista.getTxtTarifaBase().getDocument()).setDocumentFilter(
            new DocumentFilter() {
                @Override
                public void insertString(FilterBypass fb, int offset, String texto, AttributeSet attrs) throws BadLocationException {
                    replace(fb, offset, 0, texto, attrs);
                }
                @Override
                public void replace(FilterBypass fb, int offset, int length, String texto, AttributeSet attrs) throws BadLocationException {
                    String actual = fb.getDocument().getText(0, fb.getDocument().getLength());
                    String resultado = actual.substring(0, offset) + (texto == null ? "" : texto) + actual.substring(offset + length);
                    if (resultado.matches("\\d*(?:[.,]\\d{0,2})?")) {
                        fb.replace(offset, length, texto, attrs);
                    }
                }
            }
        );
    }

    public void cargarTabla() {
        if (vista == null) return;
        try {
            vista.getTblRecursos().setModel(obtenerTablaRecursos());
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void guardarDesdeVista() {
        try {
            String cedula = vista.getTxtNumeroCedula().getText().trim();
            if (!ValidarCedula.validarCedulaEcuatoriana(cedula)) {
                mostrarError(new Exception("La cédula ingresada no es válida. Verifique e intente de nuevo."));
                return;
            }
            guardar(
                vista.getTxtNombre().getText(),
                cedula,
                vista.getTxtCorreo().getText(),
                vista.getTxtRol().getText(),
                Double.parseDouble(vista.getTxtTarifaBase().getText().trim().replace(',', '.')),
                vista.getCmbTipo().getSelectedItem().toString(),
                vista.getTxtUsuarioCreado().getText(),
                vista.getTxtContrasenaDev().getText()
            );
            mostrar("Recurso guardado correctamente.");
            limpiarCampos();
            cargarTabla();
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void eliminarDesdeVista() {
        try {
            Recurso recurso = seleccionado();
            eliminar(recurso.getId(), recurso.getNombre(), recurso.getCedula(), recurso.getCorreo(), recurso.getRol(), recurso.getTarifaBase(), recurso.getTipo());
            cargarTabla();
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private void habilitarDesdeVista() {
        try {
            Recurso recurso = seleccionado();
            if ("Activo".equalsIgnoreCase(recurso.getEstado())) {
                mostrar("El recurso ya está activo.");
                return;
            }
            activar(recurso.getId(), recurso.getNombre(), recurso.getCedula(), recurso.getCorreo(), recurso.getRol(), recurso.getTarifaBase(), recurso.getTipo());
            mostrar("Recurso habilitado correctamente.");
            cargarTabla();
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    private Recurso seleccionado() throws Exception {
        int fila = vista.getTblRecursos().getSelectedRow();
        if (fila < 0) throw new Exception("Selecciona un recurso.");

        int id = (int) vista.getTblRecursos().getValueAt(fila, 0);
        String nombre = vista.getTblRecursos().getValueAt(fila, 1).toString();
        String cedula = vista.getTblRecursos().getValueAt(fila, 2).toString();
        String correo = vista.getTblRecursos().getValueAt(fila, 3).toString();
        String rol = vista.getTblRecursos().getValueAt(fila, 4).toString();
        double tarifa = Double.parseDouble(vista.getTblRecursos().getValueAt(fila, 5).toString());
        String tipo = vista.getTblRecursos().getValueAt(fila, 6).toString();
        String estado = vista.getTblRecursos().getValueAt(fila, 7).toString();

        if ("Senior".equalsIgnoreCase(tipo)) {
            return new DesarrolladorSenior(id, nombre, cedula, correo, tarifa, estado);
        }
        return new DesarrolladorJunior(id, nombre, cedula, correo, tarifa, estado);
    }

    private void limpiarCampos() {
        vista.getTxtNombre().setText("");
        vista.getTxtNumeroCedula().setText("");
        vista.getTxtCorreo().setText("");
        vista.getTxtUsuarioCreado().setText("");
        vista.getTxtContrasenaDev().setText("");
        vista.getTxtRol().setText("Desarrollador");
        actualizarTarifa();
        vista.getCmbTipo().setSelectedIndex(0);
        vista.getTblRecursos().clearSelection();
    }

    // ─────── OPERACIONES CRUD Y LÓGICA DE NEGOCIO ───────
    public void guardar(String nombre, String cedula, String correo, String rol, double tarifaBase, String tipo, String usuario, String contrasena) throws Exception {
        Recurso recurso = crear(0, nombre, cedula, correo, rol, tarifaBase, tipo, "Activo");
        recurso.guardar(usuario, contrasena);
    }

    public void actualizar(int id, String nombre, String cedula, String correo, String rol, double tarifaBase, String tipo, String estado) throws Exception {
        crear(id, nombre, cedula, correo, rol, tarifaBase, tipo, estado).actualizar();
    }

    public void eliminar(int id, String nombre, String cedula, String correo, String rol, double tarifaBase, String tipo) throws Exception {
        crear(id, nombre, cedula, correo, rol, tarifaBase, tipo, "Activo").eliminar();
    }

    public void activar(int id, String nombre, String cedula, String correo, String rol, double tarifaBase, String tipo) throws Exception {
        crear(id, nombre, cedula, correo, rol, tarifaBase, tipo, "Inactivo").activar();
    }

    public DefaultTableModel obtenerTablaRecursos() throws Exception {
        DefaultTableModel tabla = new DefaultTableModel(
            new String[]{"ID", "Nombre", "Cédula", "Correo", "Rol", "Tarifa", "Tipo", "Estado", "Proyecto asignado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) { return false; }
        };

        String sql = "SELECT r.id, GROUP_CONCAT(p.nombre ORDER BY p.nombre SEPARATOR ', ') AS proyectos "
                   + "FROM recurso r LEFT JOIN proyecto_recurso pr ON pr.recurso_id=r.id "
                   + "LEFT JOIN proyecto p ON p.id=pr.proyecto_id GROUP BY r.id";

        Map<Integer, String> proyectosPorRecurso = new HashMap<>();
        try (Connection conn = new ConexionBDD().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                proyectosPorRecurso.put(rs.getInt("id"), rs.getString("proyectos"));
            }
        }

        for (Recurso recurso : Recurso.listarTodos()) {
            String proyectos = proyectosPorRecurso.get(recurso.getId());
            tabla.addRow(new Object[]{
                recurso.getId(), recurso.getNombre(), recurso.getCedula(), recurso.getCorreo(), recurso.getRol(),
                recurso.getTarifaBase(), recurso.getTipo(), recurso.getEstado(),
                proyectos == null ? "Sin asignar" : proyectos
            });
        }
        return tabla;
    }

    public DefaultComboBoxModel<Recurso> cargarRecursos() throws Exception {
        DefaultComboBoxModel<Recurso> combo = new DefaultComboBoxModel<>();
        for (Recurso recurso : Recurso.listarActivos()) {
            combo.addElement(recurso);
        }
        return combo;
    }

    private Recurso crear(int id, String nombre, String cedula, String correo, String rol, double tarifaBase, String tipo, String estado) throws Exception {
        if ("Senior".equalsIgnoreCase(tipo)) return new DesarrolladorSenior(id, nombre.trim(), cedula.trim(), correo.trim(), tarifaBase, estado);
        if ("Junior".equalsIgnoreCase(tipo)) return new DesarrolladorJunior(id, nombre.trim(), cedula.trim(), correo.trim(), tarifaBase, estado);
        throw new Exception("El tipo debe ser Junior o Senior.");
    }

    private void mostrar(String mensaje) { javax.swing.JOptionPane.showMessageDialog(vista, mensaje); }
    private void mostrarError(Exception ex) { javax.swing.JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage()); }
}