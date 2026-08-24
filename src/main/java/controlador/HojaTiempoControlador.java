package controlador;

import modelo.HojaTiempo;
import modelo.DetalleActividad;
import modelo.Recurso;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 * Controlador de Hoja de Tiempo y Detalle de Actividad.
 * La Vista llama a estos métodos directamente; no tiene lógica propia.
 */
public class HojaTiempoControlador {

    // ─────── HOJA DE TIEMPO ───────
    public void guardarHoja(int proyectoId, int recursoId, String periodo) throws Exception {
        HojaTiempo h = new HojaTiempo();
        h.setProyectoId(proyectoId);
        h.setRecursoId(recursoId);
        h.setPeriodo(periodo.trim());
        h.guardar();
    }

    public void cambiarEstado(int id, int proyectoId, int recursoId,
                              String periodo, String nuevoEstado) throws Exception {
        HojaTiempo h = new HojaTiempo(id, proyectoId, recursoId, periodo, "Borrador");
        h.cambiarEstado(nuevoEstado);
    }

    public void eliminarHoja(int id, int proyectoId, int recursoId,
                             String periodo, String estado) throws Exception {
        HojaTiempo h = new HojaTiempo(id, proyectoId, recursoId, periodo, estado);
        h.eliminar();
    }

    public DefaultTableModel obtenerTablaHojas() throws Exception {
        String[] cols = {"ID", "Proyecto ID", "Recurso ID", "Periodo", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (HojaTiempo h : HojaTiempo.listarTodas()) {
            modelo.addRow(new Object[]{
                h.getId(), h.getProyectoId(), h.getRecursoId(),
                h.getPeriodo(), h.getEstado()
            });
        }
        return modelo;
    }

    // ─────── DETALLE DE ACTIVIDAD ───────
    public void guardarDetalle(String fecha, String descripcion,
                               String horasStr, String modulo, int hojaTiempoId) throws Exception {
        double horas;
        try {
            horas = Double.parseDouble(horasStr.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new Exception("Las horas deben ser un número válido (ej: 8 o 7.5).");
        }
        DetalleActividad d = new DetalleActividad();
        d.setFecha(fecha.trim());
        d.setDescripcion(descripcion.trim());
        d.setHoras(horas);
        d.setModulo(modulo.trim());
        d.setHojaTiempoId(hojaTiempoId);
        d.guardar();
    }

    public void eliminarDetalle(int idDetalle) throws Exception {
        DetalleActividad d = new DetalleActividad();
        d.setId(idDetalle);
        d.eliminar();
    }

    public DefaultTableModel obtenerTablaDetalles(int hojaTiempoId) throws Exception {
        String[] cols = {"ID", "Fecha", "Descripción", "Horas", "Módulo"};
        DefaultTableModel modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (DetalleActividad d : DetalleActividad.listarPorHoja(hojaTiempoId)) {
            modelo.addRow(new Object[]{
                d.getId(), d.getFecha(), d.getDescripcion(),
                d.getHoras(), d.getModulo()
            });
        }
        return modelo;
    }

    // ─────── CALCULAR TOTALES ───────
    public double calcularTotalHoras(int hojaTiempoId) throws Exception {
        HojaTiempo h = new HojaTiempo();
        h.setId(hojaTiempoId);
        h.cargarDetalles();
        return h.calcularTotalHoras();
    }

    public double calcularCostoTotal(int hojaTiempoId, int recursoId) throws Exception {
        HojaTiempo h = new HojaTiempo();
        h.setId(hojaTiempoId);
        h.cargarDetalles();
        List<Recurso> recursos = Recurso.listarTodos();
        for (Recurso r : recursos) {
            if (r.getId() == recursoId) return h.calcularCostoTotal(r);
        }
        return 0;
    }
}
