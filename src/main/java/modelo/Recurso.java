package modelo;

import controlador.ConexionBDD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Recurso {

    protected int id;
    protected String nombre;
    protected String cedula;
    protected String correo;
    protected String rol;
    protected double tarifaBase;
    protected String tipo;
    protected String estado;

    public Recurso() {
        this.estado = "Activo";
    }

    public Recurso(int id, String nombre, String cedula, String correo, String rol, double tarifaBase, String tipo, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.cedula = cedula;
        this.correo = correo;
        this.rol = rol;
        this.tarifaBase = tarifaBase;
        this.tipo = tipo;
        this.estado = estado;
    }

    // RF-02.1: Método polimórfico que cada subclase implementa con su propia lógica
    public abstract double calcularTarifaHora();

    // RF-02.5: Costo de las horas registradas según tarifa propia
    public double calcularCostoHoras(double horas) {
        return horas * calcularTarifaHora();
    }

    // ─────── CRUD SQL ───────
    public void guardar() throws Exception {
        guardar(null, null);
    }

    public void guardar(String usuario, String contrasena) throws Exception {
        validar();
        if (usuario == null || usuario.trim().isEmpty() || contrasena == null || contrasena.isEmpty())
            throw new Exception("El usuario y la contraseña del desarrollador son obligatorios.");
        String sql = "INSERT INTO recurso (nombre, cedula, correo, rol, tarifa_base, tipo, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = new ConexionBDD().conectar();
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            conn.setAutoCommit(false);
            stmt.setString(1, nombre);
            stmt.setString(2, cedula);
            stmt.setString(3, correo);
            stmt.setString(4, rol);
            stmt.setDouble(5, tarifaBase);
            stmt.setString(6, tipo);
            stmt.setString(7, estado);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) this.id = rs.getInt(1);
            }
            String sqlUsuario = "INSERT INTO usuario (usuario, contrasena, rol, tipo, recurso_id, estado) VALUES (?, ?, 'Desarrollador', 'Recurso', ?, 'Activo')";
            try (PreparedStatement usuarioStmt = conn.prepareStatement(sqlUsuario)) {
                usuarioStmt.setString(1, usuario.trim());
                usuarioStmt.setString(2, contrasena);
                usuarioStmt.setInt(3, this.id);
                usuarioStmt.executeUpdate();
            }
            conn.commit();
            }
        } catch (Exception ex) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ignored) { }
            throw ex;
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException ignored) { }
        }
    }

    public void actualizar() throws Exception {
        validar();
        String sql = "UPDATE recurso SET nombre=?, cedula=?, correo=?, rol=?, tarifa_base=?, tipo=?, estado=? WHERE id=?";
        try (Connection conn = new ConexionBDD().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setString(2, cedula);
            stmt.setString(3, correo);
            stmt.setString(4, rol);
            stmt.setDouble(5, tarifaBase);
            stmt.setString(6, tipo);
            stmt.setString(7, estado);
            stmt.setInt(8, id);
            stmt.executeUpdate();
        }
    }

    public void eliminar() throws Exception {
        // Soft delete
        this.estado = "Inactivo";
        actualizar();
    }

    public void activar() throws Exception {
        this.estado = "Activo";
        actualizar();
    }

    public static List<Recurso> listarTodos() throws Exception {
        return listar(false);
    }

    public static List<Recurso> listarActivos() throws Exception {
        return listar(true);
    }

    private static List<Recurso> listar(boolean soloActivos) throws Exception {
        List<Recurso> lista = new ArrayList<>();
        String sql = "SELECT * FROM recurso" + (soloActivos ? " WHERE estado='Activo'" : "") + " ORDER BY nombre";
        try (Connection conn = new ConexionBDD().conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Recurso r;
                if ("Senior".equalsIgnoreCase(rs.getString("tipo"))) {
                    r = new DesarrolladorSenior();
                } else {
                    r = new DesarrolladorJunior();
                }
                r.setId(rs.getInt("id"));
                r.setNombre(rs.getString("nombre"));
                r.setCedula(rs.getString("cedula"));
                r.setCorreo(rs.getString("correo"));
                r.setRol(rs.getString("rol"));
                r.setTarifaBase(rs.getDouble("tarifa_base"));
                r.setTipo(rs.getString("tipo"));
                r.setEstado(rs.getString("estado"));
                lista.add(r);
            }
        }
        return lista;
    }

    private void validar() throws Exception {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new Exception("El nombre del recurso es obligatorio.");
        }
        if (cedula == null || cedula.trim().isEmpty()) {
            throw new Exception("La cédula del recurso es obligatoria.");
        }
        if (!ValidarCedula.validarCedulaEcuatoriana(cedula.trim())) {
            throw new Exception("La cédula ingresada no es válida.");
        }
        if (correo == null || correo.trim().isEmpty()) {
            throw new Exception("El correo del recurso es obligatorio.");
        }
        if (tarifaBase < 0) {
            throw new Exception("La tarifa base no puede ser negativa.");
        }
        if (!"Junior".equalsIgnoreCase(tipo) && !"Senior".equalsIgnoreCase(tipo)) {
            throw new Exception("El tipo debe ser Junior o Senior.");
        }
    }

    // ─────── Getters & Setters ───────
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public double getTarifaBase() {
        return tarifaBase;
    }

    public void setTarifaBase(double tarifaBase) {
        this.tarifaBase = tarifaBase;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() { return nombre + " (" + tipo + ")"; }
}
