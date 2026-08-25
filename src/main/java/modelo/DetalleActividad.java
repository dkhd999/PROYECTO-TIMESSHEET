package modelo;

import controlador.ConexionBDD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class DetalleActividad {

    private int id;
    private String fecha;        // formato yyyy-MM-dd
    private String descripcion;
    private double horas;
    private String modulo;
    private int hojaTiempoId;

    public DetalleActividad() {}

    public DetalleActividad(int id, String fecha, String descripcion,
                             double horas, String modulo, int hojaTiempoId) {
        this.id = id;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.horas = horas;
        this.modulo = modulo;
        this.hojaTiempoId = hojaTiempoId;
    }

    // ─────── Validaciones (RF-04.5, RF-07.1, RF-07.3) ───────
    private void validar() throws Exception {
        if (horas <= 0 || horas > 24)
            throw new Exception("Las horas deben ser mayores a 0 y no exceder 24 por día.");
        if (descripcion == null || descripcion.trim().isEmpty())
            throw new Exception("La descripción de la actividad es obligatoria.");
        if (existeDuplicado())
            throw new Exception("Ya existe un registro para esta fecha y módulo en la hoja de tiempo (RF-07.1).");
        try {
            LocalDate fechaActividad = LocalDate.parse(fecha);
            HojaTiempo hoja = cargarHoja();
            hoja.validarParaDetalle();
            LocalDate[] rango = hoja.rangoPeriodo();
            if (fechaActividad.isBefore(rango[0]) || fechaActividad.isAfter(rango[1]))
                throw new Exception("La fecha de la actividad debe estar dentro del periodo de la hoja.");
            if (hoja.calcularTotalHoras() + horas > 60.0)
                throw new Exception("El total semanal no puede superar 60 horas.");
        } catch (DateTimeParseException e) {
            throw new Exception("La fecha debe usar el formato yyyy-MM-dd.");
        }
    }

    private HojaTiempo cargarHoja() throws Exception {
        String sql = "SELECT id, proyecto_id, recurso_id, periodo, estado FROM hoja_tiempo WHERE id=?";
        try (Connection conn = new ConexionBDD().conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hojaTiempoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) throw new Exception("No existe la hoja de tiempo indicada.");
                return new HojaTiempo(rs.getInt("id"), rs.getInt("proyecto_id"), rs.getInt("recurso_id"), rs.getString("periodo"), rs.getString("estado"));
            }
        }
    }

    private boolean existeDuplicado() throws Exception {
        String sql = "SELECT COUNT(*) FROM detalle_actividad WHERE fecha=? AND modulo=? AND hoja_tiempo_id=? AND id!=?";
        try (Connection conn = new ConexionBDD().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fecha);
            stmt.setString(2, modulo);
            stmt.setInt(3, hojaTiempoId);
            stmt.setInt(4, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // ─────── CRUD SQL ───────
    public void guardar() throws Exception {
        validar();
        String sql = "INSERT INTO detalle_actividad (fecha, descripcion, horas, modulo, hoja_tiempo_id) VALUES (?,?,?,?,?)";
        try (Connection conn = new ConexionBDD().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, fecha);
            stmt.setString(2, descripcion);
            stmt.setDouble(3, horas);
            stmt.setString(4, modulo);
            stmt.setInt(5, hojaTiempoId);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) this.id = rs.getInt(1);
            }
        }
    }

    public void actualizar() throws Exception {
        validar();
        String sql = "UPDATE detalle_actividad SET fecha=?, descripcion=?, horas=?, modulo=? WHERE id=?";
        try (Connection conn = new ConexionBDD().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fecha);
            stmt.setString(2, descripcion);
            stmt.setDouble(3, horas);
            stmt.setString(4, modulo);
            stmt.setInt(5, id);
            stmt.executeUpdate();
        }
    }

    public void eliminar() throws Exception {
        HojaTiempo hoja = cargarHoja();
        hoja.validarParaDetalle();
        String sql = "DELETE FROM detalle_actividad WHERE id=?";
        try (Connection conn = new ConexionBDD().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public static List<DetalleActividad> listarPorHoja(int idHoja) throws Exception {
        List<DetalleActividad> lista = new ArrayList<>();
        String sql = "SELECT * FROM detalle_actividad WHERE hoja_tiempo_id=? ORDER BY fecha";
        try (Connection conn = new ConexionBDD().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idHoja);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new DetalleActividad(
                        rs.getInt("id"),
                        rs.getString("fecha"),
                        rs.getString("descripcion"),
                        rs.getDouble("horas"),
                        rs.getString("modulo"),
                        rs.getInt("hoja_tiempo_id")
                    ));
                }
            }
        }
        return lista;
    }

    // ─────── Getters & Setters ───────
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public double getHoras() { return horas; }
    public void setHoras(double horas) { this.horas = horas; }
    public String getModulo() { return modulo; }
    public void setModulo(String modulo) { this.modulo = modulo; }
    public int getHojaTiempoId() { return hojaTiempoId; }
    public void setHojaTiempoId(int hojaTiempoId) { this.hojaTiempoId = hojaTiempoId; }
}
