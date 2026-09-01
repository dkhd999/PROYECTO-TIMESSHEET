package controlador;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import controlador.ConexionBDD;
import modelo.DetalleActividad;
import modelo.HojaTiempo;
import modelo.Recurso;
import java.io.FileOutputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;


public class ReportePDF {

    public DefaultComboBoxModel<HojaTiempo> cargarHojas() throws Exception {
        return cargarHojas(null);
    }

    public DefaultComboBoxModel<HojaTiempo> cargarHojas(Integer recursoId) throws Exception {
        DefaultComboBoxModel<HojaTiempo> combo = new DefaultComboBoxModel<>();
        for (HojaTiempo hoja : HojaTiempo.listarPorFiltro(null, recursoId, null)) combo.addElement(hoja);
        return combo;
    }

    public String generarReportePDF(int hojaTiempoId, int recursoId, String rutaArchivo) throws Exception {
        // 1. Obtener datos del modelo
        HojaTiempo hoja = HojaTiempo.consultar(hojaTiempoId);
        if (hoja.getRecursoId() != recursoId)
            throw new Exception("La hoja de tiempo no corresponde al recurso seleccionado.");
        hoja.cargarDetalles();

        Recurso recurso = null;
        for (Recurso r : Recurso.listarTodos()) {
            if (r.getId() == recursoId) { recurso = r; break; }
        }
        if (recurso == null) throw new Exception("No se encontro el recurso con ID: " + recursoId);

        double totalHorasHoja = hoja.calcularTotalHoras();
        double costoTotalHoja = hoja.calcularCostoTotal(recurso);

        // 2. Obtener datos mensuales desde el stored procedure sp_reporte_mensual_recurso
        String mesAnio = hoja.getPeriodo().substring(0, 7); // "2026-08"
        double[] totalMensual = new double[2]; // [0]=totalHorasMes, [1]=totalCostoMes
        List<HojaTiempo> hojasMes = listarMensualPorSP(recursoId, mesAnio, totalMensual);
        double totalHorasMes = totalMensual[0];
        double totalCostoMes = totalMensual[1];

        // 3. Generar PDF con iTextPDF
        Document doc = new Document();
        PdfWriter.getInstance(doc, new FileOutputStream(rutaArchivo));
        doc.open();

        Font fuenteTitulo = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Font fuenteSubtitulo = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
        Font fuenteNormal = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
        Font fuenteResumen = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.DARK_GRAY);

        // ── Encabezado ──
        doc.add(new Paragraph("REPORTE DE HOJA DE TIEMPO", fuenteTitulo));
        doc.add(Chunk.NEWLINE);
        doc.add(new Paragraph("Recurso: " + recurso.getNombre() + " (" + recurso.getTipo() + ")", fuenteSubtitulo));
        doc.add(new Paragraph("Cedula: " + recurso.getCedula(), fuenteNormal));
        doc.add(new Paragraph("Periodo de la hoja: " + hoja.getPeriodo(), fuenteNormal));
        doc.add(new Paragraph("Estado: " + hoja.getEstado(), fuenteNormal));
        doc.add(new Paragraph("Tarifa/hora: $" + String.format("%.2f", recurso.calcularTarifaHora()), fuenteNormal));
        doc.add(Chunk.NEWLINE);

        // ── Tabla de detalles de la hoja seleccionada ──
        doc.add(new Paragraph("DETALLE DE ACTIVIDADES - Hoja #" + hoja.getId(), fuenteSubtitulo));
        doc.add(Chunk.NEWLINE);

        PdfPTable tabla = new PdfPTable(5);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{15, 20, 30, 15, 20});
        String[] cabeceras = {"Fecha", "Modulo", "Descripcion", "Horas", "Costo"};
        for (String cab : cabeceras) {
            PdfPCell celda = new PdfPCell(new Phrase(cab, fuenteSubtitulo));
            celda.setBackgroundColor(BaseColor.LIGHT_GRAY);
            tabla.addCell(celda);
        }
        for (DetalleActividad d : hoja.getDetalles()) {
            double costoDet = recurso.calcularCostoHoras(d.getHoras());
            tabla.addCell(new Phrase(d.getFecha(), fuenteNormal));
            tabla.addCell(new Phrase(d.getModulo(), fuenteNormal));
            tabla.addCell(new Phrase(d.getDescripcion(), fuenteNormal));
            tabla.addCell(new Phrase(String.format("%.2f", d.getHoras()), fuenteNormal));
            tabla.addCell(new Phrase("$" + String.format("%.2f", costoDet), fuenteNormal));
        }
        doc.add(tabla);
        doc.add(Chunk.NEWLINE);

        doc.add(new Paragraph("Total Horas (Hoja): " + String.format("%.2f", totalHorasHoja), fuenteSubtitulo));
        doc.add(new Paragraph("Costo Total (Hoja): $" + String.format("%.2f", costoTotalHoja), fuenteSubtitulo));

        // ── Resumen mensual ──
        doc.add(Chunk.NEWLINE);
        doc.add(new Paragraph("─".repeat(50), fuenteNormal));
        doc.add(Chunk.NEWLINE);
        doc.add(new Paragraph("RESUMEN MENSUAL - " + mesAnio + " - " + recurso.getNombre(), fuenteResumen));
        doc.add(Chunk.NEWLINE);

        PdfPTable tablaMensual = new PdfPTable(4);
        tablaMensual.setWidthPercentage(100);
        tablaMensual.setWidths(new float[]{15, 25, 30, 30});
        String[] cabMensual = {"Hoja", "Periodo", "Estado", "Horas"};
        for (String cab : cabMensual) {
            PdfPCell celda = new PdfPCell(new Phrase(cab, fuenteSubtitulo));
            celda.setBackgroundColor(BaseColor.LIGHT_GRAY);
            tablaMensual.addCell(celda);
        }
        for (HojaTiempo h : hojasMes) {
            h.cargarDetalles();
            tablaMensual.addCell(new Phrase("#" + h.getId(), fuenteNormal));
            tablaMensual.addCell(new Phrase(h.getPeriodo(), fuenteNormal));
            tablaMensual.addCell(new Phrase(h.getEstado(), fuenteNormal));
            tablaMensual.addCell(new Phrase(String.format("%.2f", h.calcularTotalHoras()), fuenteNormal));
        }
        doc.add(tablaMensual);
        doc.add(Chunk.NEWLINE);

        doc.add(new Paragraph("Total Horas Mensuales: " + String.format("%.2f", totalHorasMes), fuenteResumen));
        doc.add(new Paragraph("Costo Total Mensual: $" + String.format("%.2f", totalCostoMes), fuenteResumen));

        doc.close();
        return rutaArchivo;
    }

  
    private List<HojaTiempo> listarMensualPorSP(int recursoId, String mesAnio,
                                               double[] totalMensual) throws Exception {
        List<HojaTiempo> hojas = new ArrayList<>();
        String sql = "{CALL sp_reporte_mensual_recurso(?, ?)}";
        try (Connection conn = new ConexionBDD().conectar();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, recursoId);
            stmt.setString(2, mesAnio);
            boolean hayResultados = stmt.execute();

            // Primer result set: detalle por hoja
            if (hayResultados) {
                try (ResultSet rs = stmt.getResultSet()) {
                    while (rs.next()) {
                        HojaTiempo h = new HojaTiempo(rs.getInt("hoja_id"),
                                recursoId, 0,
                                rs.getString("periodo"), rs.getString("estado"));
                        h.cargarDetalles();
                        hojas.add(h);
                    }
                }
            }

            // Segundo result set: totales
            if (stmt.getMoreResults()) {
                try (ResultSet rs = stmt.getResultSet()) {
                    if (rs.next()) {
                        totalMensual[0] = rs.getDouble("total_horas_mes");
                        totalMensual[1] = rs.getDouble("costo_total_mes");
                    }
                }
            }
        }
        return hojas;
    }
}
