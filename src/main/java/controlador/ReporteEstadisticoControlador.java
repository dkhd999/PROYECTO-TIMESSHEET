package controlador;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import modelo.DesarrolladorJunior;
import modelo.Proyecto;
import modelo.Recurso;
import modelo.ReporteEstadistico;
import modelo.ReporteEstadistico.DatoEstadistica;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import vista.ReporteEstadisticoVista;


public class ReporteEstadisticoControlador {

    private final ReporteEstadisticoVista vista;

    public ReporteEstadisticoControlador(ReporteEstadisticoVista vista) {
        this.vista = vista;
        vista.getBtnPrevisualizar().addActionListener(event -> previsualizar());
        vista.getBtnGenerarPDF().addActionListener(event -> exportarPDF());
        cargarDesarrolladores();
        cargarEstados();
        vista.getCmbxNombreDesarrollador().addActionListener(event -> cargarProyectos());
        cargarProyectos();
    }

    private void cargarProyectos() {
        // Reporte por PROYECTO + desarrollador + rango de fechas.
        // El combo de proyecto filtra los proyectos segun el estado elegido.
        try {
            String estadoFiltro = vista.getCmbxEstadoProyecto().getSelectedItem() == null
                    ? "Activo" : vista.getCmbxEstadoProyecto().getSelectedItem().toString();
            javax.swing.DefaultComboBoxModel<Proyecto> modelo = new javax.swing.DefaultComboBoxModel<>();
            for (Proyecto p : Proyecto.listarTodos()) {
                if (estadoFiltro.equalsIgnoreCase(p.getEstado()))
                    modelo.addElement(p);
            }
            vista.getCmbxProyectos().setModel(modelo);
        } catch (Exception ex) { mostrarError(ex); }
    }

    private void cargarDesarrolladores() {
        try {
            javax.swing.DefaultComboBoxModel<Recurso> modelo = new javax.swing.DefaultComboBoxModel<>();
            DesarrolladorJunior todos = new DesarrolladorJunior();
            todos.setId(0);
            todos.setNombre("Todos los desarrolladores");
            modelo.addElement(todos);
            DesarrolladorJunior senior = new DesarrolladorJunior() {
                @Override
                public String toString() {
                    return "Todos los desarrolladores Senior";
                }
            };
            senior.setId(-1);
            modelo.addElement(senior);
            for (Recurso r : Recurso.listarActivos()) modelo.addElement(r);
            vista.getCmbxNombreDesarrollador().setModel(modelo);
        } catch (Exception ex) { mostrarError(ex); }
    }

    private void cargarEstados() {
        vista.getCmbxEstadoProyecto().setModel(
            new javax.swing.DefaultComboBoxModel<>(new String[]{"Activo", "Finalizado"}));
        vista.getCmbxEstadoProyecto().addActionListener(event -> cargarProyectos());
    }

    private Proyecto proyectoSeleccionado() {
        return (Proyecto) vista.getCmbxProyectos().getSelectedItem();
    }

    private int devolverDesarrolladorSeleccionado() {
        Recurso r = (Recurso) vista.getCmbxNombreDesarrollador().getSelectedItem();
        return r == null ? 0 : r.getId();
    }

    private String devolverNombreDesarrolladorSeleccionado() {
        Recurso r = (Recurso) vista.getCmbxNombreDesarrollador().getSelectedItem();
        return r == null ? "" : r.getNombre();
    }

    private void filtrarDatosModo(List<DatoEstadistica> datos) {
        int id = devolverDesarrolladorSeleccionado();
        if (id == -1) {            // Todos los desarrolladores Senior
            datos.removeIf(d -> !"Senior".equalsIgnoreCase(d.getTipo()));
        } else if (id > 0) {       // Un desarrollador especifico
            String nombre = devolverNombreDesarrolladorSeleccionado().trim();
            datos.removeIf(d -> !d.getDesarrollador().trim().equalsIgnoreCase(nombre));
        }
    }

    private void previsualizar() {
        try {
            Proyecto proyecto = proyectoSeleccionado();
            if (proyecto == null) throw new Exception("Selecciona un proyecto.");

            String fechaInicio = vista.getTxtFechaInicio().getText().trim();
            String fechaFin = vista.getTxtFechaFin().getText().trim();
            // El rango de fechas es solo informativo: se muestran las horas
            // totales registradas del proyecto seleccionado (sin filtro por fecha).
            if (fechaInicio.isEmpty()) fechaInicio = "---";
            if (fechaFin.isEmpty()) fechaFin = "---";

            // Mostrar el proximo numero de reporte que se crearia
            vista.getTxtIDReporteE().setText(String.valueOf(ReporteEstadistico.proximoId()));

            List<DatoEstadistica> datos = ReporteEstadistico.horasPorDesarrollador(
                    proyecto.getId(), fechaInicio, fechaFin);
            filtrarDatosModo(datos);

            System.out.println("PREVISUALIZAR: proyecto=" + proyecto.getNombre()
                    + " desarrolladores=" + datos.size() + " rango(referencia)=" + fechaInicio + " a " + fechaFin);

            if (datos.isEmpty()) {
                String desarrollo = devolverNombreDesarrolladorSeleccionado();
                String mensaje;
                if (desarrollo == null || desarrollo.trim().isEmpty()) {
                    mensaje = "No hay horas registradas para el proyecto \"" + proyecto.getNombre()
                            + "\".\nSeleccione otro desarrollador o proyecto para generar la estadística.";
                } else {
                    mensaje = "El desarrollador \"" + desarrollo
                            + "\" no tiene horas registradas en el proyecto \"" + proyecto.getNombre()
                            + "\".\nVerifique que el desarrollador esté asignado al proyecto "
                            + "y que haya registrado horas de actividad.";
                }
                javax.swing.JOptionPane.showMessageDialog(vista, mensaje,
                        "Sin actividad registrada",
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JFreeChart chart = crearGraficoBarras(datos, proyecto.getNombre());
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new java.awt.Dimension(560, 260));
            chartPanel.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

            // Forzar un layout y tamano visible en el panel (robusto ante regeneraciones)
            javax.swing.JPanel panel = vista.getPanelGrafico();
            panel.removeAll();
            panel.setLayout(new java.awt.BorderLayout());
            panel.setMinimumSize(new java.awt.Dimension(560, 260));
            panel.setPreferredSize(new java.awt.Dimension(560, 260));
            panel.add(chartPanel, java.awt.BorderLayout.CENTER);
            panel.revalidate();
            panel.repaint();
            chartPanel.setVisible(true);
            panel.setVisible(true);
            System.out.println("PREVISUALIZAR: chartPanel añadido, visible=" + chartPanel.isVisible()
                    + " panelSize=" + panel.getSize());
        } catch (Exception ex) { mostrarError(ex); }
    }

    private JFreeChart crearGraficoBarras(List<DatoEstadistica> datos, String tituloProyecto) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        // Una sola serie "Horas" con cada desarrollo como categoria en el eje X:
        // el nombre de cada desarrollo sale debajo de su barra.
        int n = datos.size();
        java.awt.Color[] colores = {
            new java.awt.Color(31, 119, 180),
            new java.awt.Color(255, 127, 14),
            new java.awt.Color(44, 160, 44),
            new java.awt.Color(214, 39, 40),
            new java.awt.Color(148, 103, 189),
            new java.awt.Color(140, 86, 75),
            new java.awt.Color(227, 119, 194),
            new java.awt.Color(127, 127, 127)
        };
        for (int i = 0; i < n; i++) {
            DatoEstadistica d = datos.get(i);
            dataset.addValue(d.getTotalHoras(), "Horas", d.getDesarrollador());
        }

        JFreeChart chart = ChartFactory.createBarChart(
            "Horas trabajadas - " + tituloProyecto,
            "Desarrollador", "Horas", dataset,
            org.jfree.chart.plot.PlotOrientation.VERTICAL,
            false, true, false);

        final java.awt.Color[] paletaColores = colores;
        final int total = n;
        BarRenderer renderer = new BarRenderer() {
            @Override
            public java.awt.Paint getItemPaint(int row, int column) {
                return paletaColores[column % paletaColores.length];
            }
        };
        renderer.setDrawBarOutline(false);
        chart.getCategoryPlot().setRenderer(renderer);
        return chart;
    }

    private void exportarPDF() {
        try {
            Proyecto proyecto = proyectoSeleccionado();
            if (proyecto == null) throw new Exception("Selecciona un proyecto.");

            String fechaInicio = vista.getTxtFechaInicio().getText().trim();
            String fechaFin = vista.getTxtFechaFin().getText().trim();
            // El rango de fechas es solo informativo: se archivan y muestran las
            // horas totales del proyecto. Si el usuario no indica fechas, se usan
            // las del periodo del proyecto para el historial.
            if (fechaInicio.isEmpty()) fechaInicio = "---";
            if (fechaFin.isEmpty()) fechaFin = "---";

            // Archivar el reporte en el historial (usa fechas validas si estan vacias)
            String fechaInicioBD = ("---".equals(fechaInicio)) ? proyecto.getFechaInicio() : fechaInicio;
            String fechaFinBD = ("---".equals(fechaFin)) ? proyecto.getFechaFin() : fechaFin;
            ReporteEstadistico.ResultadoGuardado guardado =
                    ReporteEstadistico.guardar(proyecto.getId(), fechaInicioBD, fechaFinBD);
            if (guardado.getId() > 0)
                vista.getTxtIDReporteE().setText(String.valueOf(guardado.getId()));

            List<DatoEstadistica> datos = ReporteEstadistico.horasPorDesarrollador(
                    proyecto.getId(), fechaInicio, fechaFin);
            filtrarDatosModo(datos);

            if (datos.isEmpty()) {
                String desarrollo = devolverNombreDesarrolladorSeleccionado();
                String mensaje;
                if (desarrollo == null || desarrollo.trim().isEmpty()) {
                    mensaje = "No hay horas registradas para el proyecto \"" + proyecto.getNombre()
                            + "\".\nNo se generará el PDF. Seleccione otro proyecto.";
                } else {
                    mensaje = "El desarrollador \"" + desarrollo
                            + "\" no tiene horas registradas en el proyecto \"" + proyecto.getNombre()
                            + "\".\nNo se generará el PDF. Verifique la asignación y el registro de horas.";
                }
                javax.swing.JOptionPane.showMessageDialog(vista, mensaje,
                        "Sin actividad registrada",
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JFileChooser selector = new JFileChooser();
            selector.setFileFilter(new FileNameExtensionFilter("PDF", "pdf"));
            if (selector.showSaveDialog(vista) != JFileChooser.APPROVE_OPTION) return;
            String ruta = selector.getSelectedFile().getAbsolutePath();
            if (!ruta.toLowerCase().endsWith(".pdf")) ruta += ".pdf";

            // Generar imagen del grafico y renderizarla en el PDF
            JFreeChart chart = crearGraficoBarras(datos, proyecto.getNombre());
            File imagenTmp = File.createTempFile("grafico_", ".png");
            ChartUtils.saveChartAsPNG(imagenTmp, chart, 800, 400);

            BufferedImage imagen = ImageIO.read(imagenTmp);
            com.itextpdf.text.Image imgPdf = com.itextpdf.text.Image.getInstance(imagen, null);
            imgPdf.scaleToFit(500, 300);

            com.itextpdf.text.Document doc = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(doc, new FileOutputStream(ruta));
            doc.open();
            doc.add(new com.itextpdf.text.Paragraph("REPORTE ESTADISTICO DE HORAS", new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 16, com.itextpdf.text.Font.BOLD)));
            doc.add(new com.itextpdf.text.Paragraph("Proyecto: " + proyecto.getNombre()));
            doc.add(new com.itextpdf.text.Paragraph("Periodo: " + fechaInicio + " a " + fechaFin));
            doc.add(com.itextpdf.text.Chunk.NEWLINE);
            doc.add(imgPdf);
            doc.add(com.itextpdf.text.Chunk.NEWLINE);

            double totalGeneral = 0;
            com.itextpdf.text.pdf.PdfPTable tabla = new com.itextpdf.text.pdf.PdfPTable(3);
            tabla.addCell("Desarrollador");
            tabla.addCell("Tipo");
            tabla.addCell("Horas");
            for (DatoEstadistica d : datos) {
                tabla.addCell(d.getDesarrollador());
                tabla.addCell(d.getTipo());
                tabla.addCell(String.format("%.2f", d.getTotalHoras()));
                totalGeneral += d.getTotalHoras();
            }
            doc.add(tabla);
            doc.add(com.itextpdf.text.Chunk.NEWLINE);
            doc.add(new com.itextpdf.text.Paragraph("Total horas en el periodo: " + String.format("%.2f", totalGeneral), new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD)));
            doc.close();
            imagenTmp.deleteOnExit();

            mostrar("Reporte estadistico PDF generado correctamente.");
        } catch (Exception ex) { mostrarError(ex); }
    }

    private void mostrar(String mensaje) { JOptionPane.showMessageDialog(vista, mensaje); }
    private void mostrarError(Exception ex) {
        ex.printStackTrace(System.err);
        JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage());
    }
}