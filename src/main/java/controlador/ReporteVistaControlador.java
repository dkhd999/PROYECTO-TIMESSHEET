package controlador;

import modelo.HojaTiempo;
import vista.ReporteVista;

/** Coordina ReporteVista y delega la generación del documento al controlador de reportes. */
public class ReporteVistaControlador {

    private final ReporteVista vista;
    private final ReporteControlador reportes = new ReporteControlador();

    public ReporteVistaControlador(ReporteVista vista) {
        this.vista = vista;
        vista.cmbHojas.addActionListener(event -> cargarHojaSeleccionada());
        vista.btnSeleccionarRuta.addActionListener(event -> seleccionarRuta());
        vista.btnGenerarPDF.addActionListener(event -> generarPDF());
        cargarHojas();
    }

    private void cargarHojas() {
        try {
            vista.cmbHojas.setModel(reportes.cargarHojas());
            if (vista.cmbHojas.getItemCount() == 0) mostrar("No hay hojas de tiempo disponibles.");
        } catch (Exception ex) { mostrarError(ex); }
    }

    private void cargarHojaSeleccionada() {
        HojaTiempo hoja = (HojaTiempo) vista.cmbHojas.getSelectedItem();
        if (hoja != null) {
            vista.txtHojaId.setText(String.valueOf(hoja.getId()));
            vista.txtRecursoId.setText(String.valueOf(hoja.getRecursoId()));
        }
    }

    private void seleccionarRuta() {
        javax.swing.JFileChooser selector = new javax.swing.JFileChooser();
        if (selector.showSaveDialog(vista) == javax.swing.JFileChooser.APPROVE_OPTION) {
            vista.txtRutaArchivo.setText(selector.getSelectedFile().getAbsolutePath());
        }
    }

    private void generarPDF() {
        try {
            if (vista.txtRutaArchivo.getText().trim().isEmpty()) {
                mostrar("Selecciona una ubicación para guardar el PDF.");
                return;
            }
            int hojaId = Integer.parseInt(vista.txtHojaId.getText().trim());
            int recursoId = Integer.parseInt(vista.txtRecursoId.getText().trim());
            reportes.generarReportePDF(hojaId, recursoId, vista.txtRutaArchivo.getText().trim());
            mostrar("Reporte PDF generado correctamente.");
        } catch (NumberFormatException ex) {
            mostrar("Los IDs deben ser números válidos.");
        } catch (Exception ex) { mostrarError(ex); }
    }

    private void mostrar(String mensaje) { javax.swing.JOptionPane.showMessageDialog(vista, mensaje); }
    private void mostrarError(Exception ex) { mostrar("Error: " + ex.getMessage()); }
}
