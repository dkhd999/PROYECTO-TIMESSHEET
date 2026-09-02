package modelo;

import controlador.ConexionBDD;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;


public class ReporteEstadistico {

    /** Resultado individual: horas de un desarrollo en un proyecto y rango. */
    /** Resultado individual: horas de un desarrollo en un proyecto y rango. */
    public static class DatoEstadistica {
        private final String desarrollador;
        private final String tipo;
        private final double totalHoras;

        public DatoEstadistica(String desarrollador, String tipo, double totalHoras) {
            this.desarrollador = desarrollador;
            this.tipo = tipo;
            this.totalHoras = totalHoras;
        }

        public String getDesarrollador() { return desarrollador; }
        public String getTipo() { return tipo; }
        public double getTotalHoras() { return totalHoras; }
    }

   
    public static class ResultadoGuardado {
        private final int id;
        private final int filas;
        private final double totalHoras;

        public ResultadoGuardado(int id, int filas, double totalHoras) {
            this.id = id;
            this.filas = filas;
            this.totalHoras = totalHoras;
        }

        public int getId() { return id; }
        public int getFilas() { return filas; }
        public double getTotalHoras() { return totalHoras; }
    }

  // consulta
    public static List<DatoEstadistica> horasPorDesarrollador(
            int proyectoId, String fechaInicio, String fechaFin) throws Exception {
        if (fechaInicio == null || fechaInicio.trim().isEmpty() || fechaFin == null || fechaFin.trim().isEmpty())
            throw new Exception("La fecha de inicio y la fecha de fin son obligatorias.");
        if (fechaInicio.compareTo(fechaFin) > 0)
            throw new Exception("La fecha de inicio no puede ser posterior a la fecha de fin.");

        List<DatoEstadistica> lista = new ArrayList<>();
        String sql = "{CALL sp_reporte_estadistico_horas(?, ?, ?)}";
        try (Connection conn = new ConexionBDD().conectar();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, proyectoId);
            stmt.setString(2, fechaInicio.trim());
            stmt.setString(3, fechaFin.trim());
            boolean hayResultados = stmt.execute();
            if (hayResultados) {
                try (ResultSet rs = stmt.getResultSet()) {
                    while (rs.next()) {
                        lista.add(new DatoEstadistica(
                            rs.getString("desarrollador"),
                            rs.getString("tipo"),
                            rs.getDouble("total_horas")
                        ));
                    }
                }
            }
        } catch (java.sql.SQLException e) {
            String msg = e.getMessage();
            if (msg != null && msg.length() > 1) msg = msg.replaceFirst("^[^:]*:\\s*", "");
            throw new Exception(msg == null || msg.trim().isEmpty() ? "Error al consultar el reporte." : msg.trim());
        }
        return lista;
    }

   /** Consulta de horas de un desarrollador agrupadas por PROYECTO en un rango. */
    public static List<DatoEstadistica> horasPorProyectoDelDesarrollador(
            int recursoId, String fechaInicio, String fechaFin) throws Exception {
        if (fechaInicio == null || fechaInicio.trim().isEmpty() || fechaFin == null || fechaFin.trim().isEmpty())
            throw new Exception("La fecha de inicio y la fecha de fin son obligatorias.");
        if (recursoId <= 0)
            throw new Exception("Debe seleccionar un desarrollador.");

        List<DatoEstadistica> lista = new ArrayList<>();
        String sql = "{CALL sp_reporte_horas_por_proyecto(?, ?, ?)}";
        try (Connection conn = new ConexionBDD().conectar();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, recursoId);
            stmt.setString(2, fechaInicio.trim());
            stmt.setString(3, fechaFin.trim());
            boolean hayResultados = stmt.execute();
            if (hayResultados) {
                try (ResultSet rs = stmt.getResultSet()) {
                    while (rs.next()) {
                        lista.add(new DatoEstadistica(
                            rs.getString("proyecto"),
                            "Proyecto",
                            rs.getDouble("total_horas")
                        ));
                    }
                }
            }
        } catch (java.sql.SQLException e) {
            String msg = e.getMessage();
            if (msg != null && msg.length() > 1) msg = msg.replaceFirst("^[^:]*:\\s*", "");
            throw new Exception(msg == null || msg.trim().isEmpty() ? "Error al consultar el reporte." : msg.trim());
        }
        return lista;
    }

   // calcula
    public static ResultadoGuardado guardar(int proyectoId, String fechaInicio, String fechaFin)
            throws Exception {
        if (fechaInicio == null || fechaInicio.trim().isEmpty() || fechaFin == null || fechaFin.trim().isEmpty())
            throw new Exception("La fecha de inicio y la fecha de fin son obligatorias.");

        String sql = "{CALL sp_guardar_reporte_estadistico(?, ?, ?, ?, ?, ?)}";
        try (Connection conn = new ConexionBDD().conectar();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, proyectoId);
            stmt.setString(2, fechaInicio.trim());
            stmt.setString(3, fechaFin.trim());
            stmt.registerOutParameter(4, Types.INTEGER);
            stmt.registerOutParameter(5, Types.DECIMAL);
            stmt.registerOutParameter(6, Types.VARCHAR);
            stmt.execute();
            int filas = stmt.getInt(4);
            double total = stmt.getDouble(5);
            String msg = stmt.getString(6);
            if (msg != null && (msg.startsWith("Error") || msg.startsWith("El reporte solo")
                    || msg.startsWith("La fecha") || msg.startsWith("El proyecto")
                    || msg.startsWith("El rango") || msg.startsWith("No hay horas")))
                throw new Exception(msg);
            return new ResultadoGuardado(idUltimoInsertado(conn, proyectoId), filas, total);
        }
    }

    private static int idUltimoInsertado(Connection conn, int proyectoId) throws Exception {
        String sql = "SELECT MAX(id) AS id FROM reporte_estadistico WHERE proyecto_id=?";
        try (java.sql.PreparedStatement p = conn.prepareStatement(sql)) {
            p.setInt(1, proyectoId);
            try (ResultSet rs = p.executeQuery()) {
                return rs.next() && rs.getObject(1) != null ? rs.getInt(1) : 0;
            }
        }
    }

    public static int proximoId() throws Exception {
        String sql = "SELECT COALESCE(MAX(id),0)+1 AS prox FROM reporte_estadistico";
        try (Connection conn = new ConexionBDD().conectar();
             java.sql.Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt("prox") : 1;
        }
    }
}