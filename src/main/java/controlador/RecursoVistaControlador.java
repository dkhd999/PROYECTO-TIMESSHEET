package controlador;

import modelo.Recurso;
import vista.RecursoVista;

/** Coordina RecursoVista y delega las reglas al modelo. */
public class RecursoVistaControlador {

    private final RecursoVista vista;
    private final RecursoControlador recursos = new RecursoControlador();

    public RecursoVistaControlador(RecursoVista vista) {
        this.vista = vista;
        configurarTarifaNumerica();
        vista.btnGuardar.addActionListener(event -> guardar());
        vista.btnEliminar.addActionListener(event -> eliminar());
        vista.btnHabilitar.addActionListener(event -> habilitar());
        cargarTabla();
    }

    private void configurarTarifaNumerica() {
        ((javax.swing.text.AbstractDocument) vista.txtTarifaBase.getDocument()).setDocumentFilter(
            new javax.swing.text.DocumentFilter() {
                @Override public void insertString(FilterBypass fb, int offset, String texto,
                        javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
                    replace(fb, offset, 0, texto, attrs);
                }
                @Override public void replace(FilterBypass fb, int offset, int length, String texto,
                        javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
                    String actual = fb.getDocument().getText(0, fb.getDocument().getLength());
                    String resultado = actual.substring(0, offset) + (texto == null ? "" : texto)
                            + actual.substring(offset + length);
                    if (resultado.matches("\\d*(?:[.,]\\d{0,2})?")) fb.replace(offset, length, texto, attrs);
                }
            });
    }

    private void cargarTabla() {
        try { vista.tblRecursos.setModel(recursos.obtenerTablaRecursos()); }
        catch (Exception ex) { mostrarError(ex); }
    }

    private void guardar() {
        try {
            recursos.guardar(vista.txtNombre.getText(), vista.txtCorreo.getText(), vista.txtRol.getText(),
                    Double.parseDouble(vista.txtTarifaBase.getText().trim().replace(',', '.')),
                    vista.cmbTipo.getSelectedItem().toString());
            mostrar("Recurso guardado correctamente.");
            limpiarCampos();
            cargarTabla();
        } catch (Exception ex) { mostrarError(ex); }
    }

    private void eliminar() {
        try {
            Recurso recurso = seleccionado();
            recursos.eliminar(recurso.getId(), recurso.getNombre(), recurso.getCorreo(), recurso.getRol(),
                    recurso.getTarifaBase(), recurso.getTipo());
            cargarTabla();
        } catch (Exception ex) { mostrarError(ex); }
    }

    private void habilitar() {
        try {
            Recurso recurso = seleccionado();
            if ("Activo".equalsIgnoreCase(recurso.getEstado())) {
                mostrar("El recurso ya está activo.");
                return;
            }
            recursos.activar(recurso.getId(), recurso.getNombre(), recurso.getCorreo(), recurso.getRol(),
                    recurso.getTarifaBase(), recurso.getTipo());
            mostrar("Recurso habilitado correctamente.");
            cargarTabla();
        } catch (Exception ex) { mostrarError(ex); }
    }

    private Recurso seleccionado() throws Exception {
        int fila = vista.tblRecursos.getSelectedRow();
        if (fila < 0) throw new Exception("Selecciona un recurso.");
        int id = (int) vista.tblRecursos.getValueAt(fila, 0);
        String nombre = vista.tblRecursos.getValueAt(fila, 1).toString();
        String correo = vista.tblRecursos.getValueAt(fila, 2).toString();
        String rol = vista.tblRecursos.getValueAt(fila, 3).toString();
        double tarifa = Double.parseDouble(vista.tblRecursos.getValueAt(fila, 4).toString());
        String tipo = vista.tblRecursos.getValueAt(fila, 5).toString();
        String estado = vista.tblRecursos.getValueAt(fila, 6).toString();
        if ("Senior".equalsIgnoreCase(tipo)) {
            return new modelo.DesarrolladorSenior(id, nombre, correo, tarifa, estado);
        }
        return new modelo.DesarrolladorJunior(id, nombre, correo, tarifa, estado);
    }

    private void limpiarCampos() {
        vista.txtNombre.setText("");
        vista.txtCorreo.setText("");
        vista.txtRol.setText("");
        vista.txtTarifaBase.setText("");
        vista.cmbTipo.setSelectedIndex(0);
        vista.tblRecursos.clearSelection();
    }

    private void mostrar(String mensaje) { javax.swing.JOptionPane.showMessageDialog(vista, mensaje); }
    private void mostrarError(Exception ex) { javax.swing.JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage()); }
}
