package modelo;

import controlador.ConexionBDD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Proyecto {

    private int id;
    private String codigo;
    private String nombre;
    private String cliente;
    private String fechaInicio;   // formato yyyy-MM-dd
    private String fechaFin;      // formato yyyy-MM-dd
    private String estado;        // "Activo" | "Inactivo"

    public Proyecto() { this.estado = "Activo"; }

    public Proyecto(int id, String codigo, String nombre, String cliente,
                    String fechaInicio, String fechaFin, String estado) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.cliente = cliente;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
    }

    // ─────── Validaciones (RF-01.6, RF-07.3) ───────
    private void validar() throws Exception {
        if (codigo == null || codigo.trim().isEmpty())
            throw new Exception("El código del proyecto es obligatorio.");
        if (nombre == null || nombre.trim().isEmpty())
            throw new Exception("El nombre del proyecto es obligatorio.");
        if (fechaInicio != null && fechaFin != null && fechaInicio.compareTo(fechaFin) > 0)
            throw new Exception("La fecha de inicio no puede ser posterior a la fecha de fin.");
    }

    // ─────── CRUD SQL ───────
    public void guardar() throws Exception {
        validar();
        // RF-01.6: Código único
        if (existeCodigo(this.codigo, 0))
            throw new Exception("Ya existe un proyecto con el código: " + codigo);

        String sql = "INSERT INTO Proyecto (codigo, nombre, cliente, fecha_inicio, fecha_fin_estimada, estado) VALUES (?,?,?,?,?,?)";
        try (Connection conn = new ConexionBDD().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, codigo);
            stmt.setString(2, nombre);
            stmt.setString(3, cliente);
            stmt.setString(4, fechaInicio);
            stmt.setString(5, fechaFin);
            stmt.setString(6, estado);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) this.id = rs.getInt(1);
            }
        }
    }

    public void actualizar() throws Exception {
        validar();
        if (existeCodigo(this.codigo, this.id))
            throw new Exception("Ya existe otro proyecto con el código: " + codigo);

        String sql = "UPDATE Proyecto SET codigo=?, nombre=?, cliente=?, fecha_inicio=?, fecha_fin_estimada=?, estado=? WHERE id=?";
        try (Connection conn = new ConexionBDD().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codigo);
            stmt.setString(2, nombre);
            stmt.setString(3, cliente);
            stmt.setString(4, fechaInicio);
            stmt.setString(5, fechaFin);
            stmt.setString(6, estado);
            stmt.setInt(7, id);
            stmt.executeUpdate();
        }
    }

    // RF-01.5: Solo inactiva si no tiene hojas de tiempo activas
    public void eliminar() throws Exception {
        String checkSql = "SELECT COUNT(*) FROM HojaTiempo WHERE proyecto_id=? AND estado NOT IN ('Rechazada')";
        try (Connection conn = new ConexionBDD().conectar();
             PreparedStatement stmt = conn.prepareStatement(checkSql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0)
                    throw new Exception("No se puede inactivar: el proyecto tiene hojas de tiempo activas.");
            }
        }
        this.estado = "Inactivo";
        actualizar();
    }

    private boolean existeCodigo(String codigo, int ignorarId) throws Exception {
        String sql = "SELECT COUNT(*) FROM Proyecto WHERE codigo=? AND id!=?";
        try (Connection conn = new ConexionBDD().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codigo);
            stmt.setInt(2, ignorarId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public static List<Proyecto> listarTodos() throws Exception {
        List<Proyecto> lista = new ArrayList<>();
        String sql = "SELECT * FROM Proyecto ORDER BY nombre";
        try (Connection conn = new ConexionBDD().conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Proyecto(
                    rs.getInt("id"),
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getString("cliente"),
                    rs.getString("fecha_inicio"),
                    rs.getString("fecha_fin_estimada"),
                    rs.getString("estado")
                ));
            }
        }
        return lista;
    }

    // ─────── Getters & Setters ───────

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    

    @Override
    public String toString() { return nombre + " [" + codigo + "]"; }
}
