package controlador;

import modelo.HojaTiempo;
import modelo.Recurso;
import vista.ReporteVista;

public class ReporteControlador {

    private final ReporteVista vista;
    private final ReportePDF reportes = new ReportePDF();
    private final Integer recursoUsuarioId;
    private String rutaArchivo;

    public ReporteControlador(ReporteVista vista) {
        this(vista, null);
    }

    public ReporteControlador(ReporteVista vista, Integer recursoUsuarioId) {
        this.vista = vista;
        this.recursoUsuarioId = recursoUsuarioId;
        vista.getCmbHojas().addActionListener(event -> cargarHojaSeleccionada());
        vista.getCmbnNombreDev().addActionListener(event -> cargarDesarrolladorSeleccionado());
        vista.getBtnSeleccionarRuta().addActionListener(event -> seleccionarRuta());
        vista.getBtnGenerarPDF().addActionListener(event -> generarPDF());
        cargarHojas();
        cargarDesarrolladores();
    }

    private void cargarHojas() {
        try {
            vista.getCmbHojas().setModel(reportes.cargarHojas(recursoUsuarioId));
            if (vista.getCmbHojas().getItemCount() == 0) mostrar("No hay hojas de tiempo disponibles.");
        } catch (Exception ex) { mostrarError(ex); }
    }

    private void cargarHojaSeleccionada() {
        HojaTiempo hoja = (HojaTiempo) vista.getCmbHojas().getSelectedItem();
        if (hoja != null) {
            vista.getTxtHojaId().setText(String.valueOf(hoja.getId()));
            vista.getTxtRecursoId().setText(String.valueOf(hoja.getRecursoId()));
        }
    }

    private void cargarDesarrolladores() {
        try {
            javax.swing.DefaultComboBoxModel<Recurso> modelo = new javax.swing.DefaultComboBoxModel<>();
            for (Recurso recurso : Recurso.listarActivos()) {
                if (recursoUsuarioId == null || recurso.getId() == recursoUsuarioId) modelo.addElement(recurso);
            }
            vista.getCmbnNombreDev().setModel(modelo);
            cargarDesarrolladorSeleccionado();
        } catch (Exception ex) { mostrarError(ex); }
    }

    private void cargarDesarrolladorSeleccionado() {
        Recurso recurso = (Recurso) vista.getCmbnNombreDev().getSelectedItem();
        if (recurso != null) {
            vista.getTxtRecursoId().setText(String.valueOf(recurso.getId()));
            try {
                vista.getCmbHojas().setModel(reportes.cargarHojas(recurso.getId()));
                cargarHojaSeleccionada();
            } catch (Exception ex) { mostrarError(ex); }
        }
    }

    private void seleccionarRuta() {
        javax.swing.JFileChooser selector = new javax.swing.JFileChooser();
        if (selector.showSaveDialog(vista) == javax.swing.JFileChooser.APPROVE_OPTION) {
            rutaArchivo = selector.getSelectedFile().getAbsolutePath();
        }
    }

    private void generarPDF() {
        try {
            if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) {
                mostrar("Selecciona una ubicación para guardar el PDF.");
                return;
            }
            int hojaId = Integer.parseInt(vista.getTxtHojaId().getText().trim());
            int recursoId = Integer.parseInt(vista.getTxtRecursoId().getText().trim());
            if (recursoUsuarioId != null && recursoUsuarioId.intValue() != recursoId)
                throw new Exception("No puedes generar un reporte de otro desarrollador.");
            reportes.generarReportePDF(hojaId, recursoId, rutaArchivo.trim());
            mostrar("Reporte PDF generado correctamente.");
        } catch (NumberFormatException ex) {
            mostrar("Los IDs deben ser números válidos.");
        } catch (Exception ex) { mostrarError(ex); }
    }

    private void mostrar(String mensaje) { javax.swing.JOptionPane.showMessageDialog(vista, mensaje); }
    private void mostrarError(Exception ex) { mostrar("Error: " + ex.getMessage()); }
}
