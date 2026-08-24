package controlador;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import modelo.DetalleActividad;
import modelo.HojaTiempo;
import modelo.Recurso;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Controlador de generación de reportes PDF.
 * Obtiene los datos del modelo y los renderiza con iTextPDF. (RF-06.4)
 */
public class ReporteControlador {

    public String generarReportePDF(int hojaTiempoId, int recursoId, String rutaArchivo) throws Exception {
        // 1. Obtener datos del modelo (RF-06.4: el modelo entrega, no genera)
        HojaTiempo h = new HojaTiempo();
        h.setId(hojaTiempoId);
        h.cargarDetalles();

        Recurso recurso = null;
        List<Recurso> recursos = Recurso.listarTodos();
        for (Recurso r : recursos) {
            if (r.getId() == recursoId) { recurso = r; break; }
        }
        if (recurso == null) throw new Exception("No se encontró el recurso con ID: " + recursoId);

        double totalHoras = h.calcularTotalHoras();
        double costoTotal = h.calcularCostoTotal(recurso);

        // 2. Generar PDF con iTextPDF
        Document doc = new Document();
        PdfWriter.getInstance(doc, new FileOutputStream(rutaArchivo));
        doc.open();

        Font fuenteTitulo = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Font fuenteSubtitulo = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
        Font fuenteNormal = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

        doc.add(new Paragraph("REPORTE DE HOJA DE TIEMPO", fuenteTitulo));
        doc.add(Chunk.NEWLINE);
        doc.add(new Paragraph("Recurso: " + recurso.getNombre() + " (" + recurso.getTipo() + ")", fuenteSubtitulo));
        doc.add(new Paragraph("Periodo: " + h.getPeriodo(), fuenteNormal));
        doc.add(new Paragraph("Estado: " + h.getEstado(), fuenteNormal));
        doc.add(new Paragraph("Tarifa/hora: $" + String.format("%.2f", recurso.calcularTarifaHora()), fuenteNormal));
        doc.add(Chunk.NEWLINE);

        // Tabla de detalles
        PdfPTable tabla = new PdfPTable(5);
        tabla.setWidthPercentage(100);
        String[] cabeceras = {"Fecha", "Módulo", "Descripción", "Horas", "Costo"};
        for (String cab : cabeceras) {
            PdfPCell celda = new PdfPCell(new Phrase(cab, fuenteSubtitulo));
            celda.setBackgroundColor(BaseColor.LIGHT_GRAY);
            tabla.addCell(celda);
        }
        for (DetalleActividad d : h.getDetalles()) {
            double costoDet = recurso.calcularCostoHoras(d.getHoras());
            tabla.addCell(new Phrase(d.getFecha(), fuenteNormal));
            tabla.addCell(new Phrase(d.getModulo(), fuenteNormal));
            tabla.addCell(new Phrase(d.getDescripcion(), fuenteNormal));
            tabla.addCell(new Phrase(String.valueOf(d.getHoras()), fuenteNormal));
            tabla.addCell(new Phrase("$" + String.format("%.2f", costoDet), fuenteNormal));
        }
        doc.add(tabla);
        doc.add(Chunk.NEWLINE);

        doc.add(new Paragraph("Total de Horas: " + totalHoras, fuenteSubtitulo));
        doc.add(new Paragraph("Costo Total: $" + String.format("%.2f", costoTotal), fuenteSubtitulo));

        // Espacio de firma (RF-06.3)
        doc.add(Chunk.NEWLINE);
        doc.add(Chunk.NEWLINE);
        doc.add(Chunk.NEWLINE);
        doc.add(new Paragraph("_________________________________", fuenteNormal));
        doc.add(new Paragraph("Firma del Cliente", fuenteNormal));
        doc.add(new Paragraph("Nombre: ___________________________", fuenteNormal));
        doc.add(new Paragraph("Fecha:  ___________________________", fuenteNormal));

        doc.close();
        return rutaArchivo;
    }
}
