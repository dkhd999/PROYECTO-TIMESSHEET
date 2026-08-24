package modelo;

import controlador.ConexionBDD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HojaTiempo {

    private static final double MAX_HORAS_SEMANA = 60.0;

    private int id;
    private int proyectoId;
    private int recursoId;
    private String periodo;
    private String estado; // Borrador | Enviada | Aprobada | Rechazada
    private List<DetalleActividad> detalles;

    public HojaTiempo() {
        this.estado = "Borrador";
        this.detalles = new ArrayList<>();
    }

    public HojaTiempo(int id, int proyectoId, int recursoId, String periodo, String estado) {
        this.id = id;
        this.proyectoId = proyectoId;
        this.recursoId = recursoId;
        this.periodo = periodo;
        this.estado = estado;
        this.detalles = new ArrayList<>();
    }

    // ─────── Lógica de negocio (RF-03.2, RF-03.7, RF-07.3) ───────
    public void cargarDetalles() throws Exception {
        this.detalles = DetalleActividad.listarPorHoja(this.id);
    }

    public double calcularTotalHoras() {
        double total = 0;
        for (DetalleActividad d : detalles) total += d.getHoras();
        return total;
    }

    public double calcularCostoTotal(Recurso recurso) {
        return recurso.calcularCostoHoras(calcularTotalHoras());
    }

    private void validarEstadoModificable() throws Exception {
        // RF-07.2
        if ("Aprobada".equalsIgnoreCase(this.estado))
            throw new Exception("No se puede modificar una hoja de tiempo ya Aprobada.");
    }

    private void validarHorasMaximas() throws Exception {
        // RF-03.7
        if (calcularTotalHoras() > MAX_HORAS_SEMANA)
            throw new Exception("El total de horas (" + calcularTotalHoras() + ") supera el máximo de " + MAX_HORAS_SEMANA + " horas semanales.");
    }

    // ─────── CRUD SQL ───────
    public void guardar() throws Exception {
        String sql = "INSERT INTO HojaTiempo (proyecto_id, recurso_id, periodo, estado) VALUES (?,?,?,?)";
        try (Connection conn = new ConexionBDD().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, proyectoId);
            stmt.setInt(2, recursoId);
            stmt.setString(3, periodo);
            stmt.setString(4, estado);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) this.id = rs.getInt(1);
            }
        }
    }

    public void actualizar() throws Exception {
        validarEstadoModificable();
        String sql = "UPDATE HojaTiempo SET proyecto_id=?, recurso_id=?, periodo=?, estado=? WHERE id=?";
        try (Connection conn = new ConexionBDD().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, proyectoId);
            stmt.setInt(2, recursoId);
            stmt.setString(3, periodo);
            stmt.setString(4, estado);
            stmt.setInt(5, id);
            stmt.executeUpdate();
        }
    }

    // RF-03.5: Cambio de estado
    public void cambiarEstado(String nuevoEstado) throws Exception {
        validarEstadoModificable();
        this.estado = nuevoEstado;
        String sql = "UPDATE HojaTiempo SET estado=? WHERE id=?";
        try (Connection conn = new ConexionBDD().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, estado);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    // RF-03.6: Solo eliminar si está en Borrador
    public void eliminar() throws Exception {
        if (!"Borrador".equalsIgnoreCase(this.estado))
            throw new Exception("Solo se puede eliminar una hoja de tiempo en estado 'Borrador'.");
        String sql = "DELETE FROM HojaTiempo WHERE id=?";
        try (Connection conn = new ConexionBDD().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public static List<HojaTiempo> listarTodas() throws Exception {
        List<HojaTiempo> lista = new ArrayList<>();
        String sql = "SELECT * FROM HojaTiempo ORDER BY id DESC";
        try (Connection conn = new ConexionBDD().conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new HojaTiempo(
                    rs.getInt("id"),
                    rs.getInt("proyecto_id"),
                    rs.getInt("recurso_id"),
                    rs.getString("periodo"),
                    rs.getString("estado")
                ));
            }
        }
        return lista;
    }

    // ─────── Getters & Setters ───────
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getProyectoId() { return proyectoId; }
    public void setProyectoId(int proyectoId) { this.proyectoId = proyectoId; }
    public int getRecursoId() { return recursoId; }
    public void setRecursoId(int recursoId) { this.recursoId = recursoId; }
    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public List<DetalleActividad> getDetalles() { return detalles; }
}
