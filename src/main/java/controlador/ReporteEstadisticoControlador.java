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
        // Reporte por desarrollador + rango de fechas; el combo de proyecto
        // se mantiene visible por compatibilidad pero no condiciona el reporte.
        try {
            Recurso desarrollador = (Recurso) vista.getCmbxNombreDesarrollador().getSelectedItem();
            if (desarrollador == null) {
                vista.getCmbxProyectos().setModel(new javax.swing.DefaultComboBoxModel<>());
                return;
            }
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

    private void previsualizar() {
        try {
            Recurso dev = (Recurso) vista.getCmbxNombreDesarrollador().getSelectedItem();
            if (dev == null || dev.getId() <= 0)
                throw new Exception("Selecciona un desarrollador en particular (no 'Todos los desarrolladores').");

            String fechaInicio = vista.getTxtFechaInicio().getText().trim();
            String fechaFin = vista.getTxtFechaFin().getText().trim();
            if (fechaInicio.isEmpty() || fechaFin.isEmpty())
                throw new Exception("La fecha de inicio y la fecha de fin son obligatorias.");

            // Mostrar el proximo numero de reporte que se crearia
            vista.getTxtIDReporteE().setText(String.valueOf(ReporteEstadistico.proximoId()));

            List<DatoEstadistica> datos = ReporteEstadistico.horasPorProyectoDelDesarrollador(
                    dev.getId(), fechaInicio, fechaFin);

            System.out.println("PREVISUALIZAR: desarrollador=" + dev.getNombre()
                    + " proyectos=" + datos.size() + " rango=" + fechaInicio + " a " + fechaFin);

            if (datos.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(vista,
                        "El desarrollador \"" + dev.getNombre()
                        + "\" no tiene horas registradas entre "
                        + fechaInicio + " y " + fechaFin + ".\n"
                        + "No hay proyectos con actividad en ese lapso.",
                        "Sin actividad registrada",
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JFreeChart chart = crearGraficoBarrasProyectos(datos, dev.getNombre());
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

    private JFreeChart crearGraficoBarrasProyectos(List<DatoEstadistica> datos, String desarrollador) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
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
            "Horas trabajadas de " + desarrollador,
            "Proyecto", "Horas", dataset,
            org.jfree.chart.plot.PlotOrientation.VERTICAL,
            false, true, false);

        // Colores distintos por barra (una serie, una categoria por proyecto)
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
            Recurso dev = (Recurso) vista.getCmbxNombreDesarrollador().getSelectedItem();
            if (dev == null || dev.getId() <= 0)
                throw new Exception("Selecciona un desarrollador en particular (no 'Todos los desarrolladores').");

            String fechaInicio = vista.getTxtFechaInicio().getText().trim();
            String fechaFin = vista.getTxtFechaFin().getText().trim();
            if (fechaInicio.isEmpty() || fechaFin.isEmpty())
                throw new Exception("La fecha de inicio y la fecha de fin son obligatorias.");

            List<DatoEstadistica> datos = ReporteEstadistico.horasPorProyectoDelDesarrollador(
                    dev.getId(), fechaInicio, fechaFin);

            if (datos.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(vista,
                        "El desarrollador \"" + dev.getNombre()
                        + "\" no tiene horas registradas entre "
                        + fechaInicio + " y " + fechaFin + ".\n"
                        + "No se generará el PDF. No hay proyectos con actividad en ese lapso.",
                        "Sin actividad registrada",
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Archivar el reporte en el historial (un registro por proyecto)
            int ultimoId = 0;
            for (DatoEstadistica d : datos) {
                ReporteEstadistico.ResultadoGuardado g = ReporteEstadistico.guardarPorProyecto(
                        d.getId(), dev.getNombre(), fechaInicio, fechaFin, d.getTotalHoras());
                if (g.getId() > ultimoId) ultimoId = g.getId();
            }
            if (ultimoId > 0)
                vista.getTxtIDReporteE().setText(String.valueOf(ultimoId));

            JFileChooser selector = new JFileChooser();
            selector.setFileFilter(new FileNameExtensionFilter("PDF", "pdf"));
            if (selector.showSaveDialog(vista) != JFileChooser.APPROVE_OPTION) return;
            String ruta = selector.getSelectedFile().getAbsolutePath();
            if (!ruta.toLowerCase().endsWith(".pdf")) ruta += ".pdf";

            // Generar imagen del grafico y renderizarla en el PDF
            JFreeChart chart = crearGraficoBarrasProyectos(datos, dev.getNombre());
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
            doc.add(new com.itextpdf.text.Paragraph("Desarrollador: " + dev.getNombre()));
            doc.add(new com.itextpdf.text.Paragraph("Periodo: " + fechaInicio + " a " + fechaFin));
            doc.add(com.itextpdf.text.Chunk.NEWLINE);
            doc.add(imgPdf);
            doc.add(com.itextpdf.text.Chunk.NEWLINE);

            double totalGeneral = 0;
            com.itextpdf.text.pdf.PdfPTable tabla = new com.itextpdf.text.pdf.PdfPTable(2);
            tabla.addCell("Proyecto");
            tabla.addCell("Horas");
            for (DatoEstadistica d : datos) {
                tabla.addCell(d.getDesarrollador());
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