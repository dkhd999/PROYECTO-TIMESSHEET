package modelo;

import controlador.ConexionBDD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class HojaTiempo {

    private static double maxHorasSemana = 60.0;

    private int id;
    private int proyectoId;
    private String proyectoNombre;
    private int recursoId;
    private String periodo;
    private String estado; 
    private List<DetalleActividad> detalles;

    public HojaTiempo() {
        this.estado = "Borrador";
        this.detalles = new ArrayList<>();
    }

    public HojaTiempo(int id, int proyectoId, int recursoId, String periodo, String estado) {
        this(id, proyectoId, null, recursoId, periodo, estado);
    }

    public HojaTiempo(int id, int proyectoId, String proyectoNombre, int recursoId, String periodo, String estado) {
        this.id = id;
        this.proyectoId = proyectoId;
        this.proyectoNombre = proyectoNombre;
        this.recursoId = recursoId;
        this.periodo = periodo;
        this.estado = estado;
        this.detalles = new ArrayList<>();
    }

    // ─────── Lógica de negocio 
    public void cargarDetalles() throws Exception {
        this.detalles = DetalleActividad.listarPorHoja(this.id);
    }

    public void validarParaDetalle() throws Exception {
        validarEstadoModificable();
        cargarDetalles();
        validarHorasMaximas();
    }

    public static void configurarMaxHorasSemana(double maximo) {
        if (maximo <= 0) throw new IllegalArgumentException("El máximo semanal debe ser positivo.");
        maxHorasSemana = maximo;
    }

    public LocalDate[] rangoPeriodo() throws Exception {
        if (periodo == null || !periodo.matches("\\d{4}-\\d{2}-\\d{2}\\s*/\\s*\\d{4}-\\d{2}-\\d{2}"))
            throw new Exception("El periodo debe usar el formato yyyy-MM-dd / yyyy-MM-dd.");
        String[] fechas = periodo.split("\\s*/\\s*");
        try { return new LocalDate[]{LocalDate.parse(fechas[0]), LocalDate.parse(fechas[1])}; }
        catch (DateTimeParseException e) { throw new Exception("El periodo contiene fechas inválidas."); }
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
       
        if (!"Borrador".equalsIgnoreCase(this.estado)
            && !"Rechazada".equalsIgnoreCase(this.estado))
            throw new Exception("Solo se pueden modificar hojas en estado Borrador");
    }

    private void validarHorasMaximas() throws Exception {
       
        if (calcularTotalHoras() > maxHorasSemana)
            throw new Exception("El total de horas (" + calcularTotalHoras() + ") supera el máximo de " + maxHorasSemana + " horas semanales.");
    }

    //CRUD SQL 
    public void guardar() throws Exception {
        rangoPeriodo();
        validarAsignacion();
        String sql = "INSERT INTO hoja_tiempo (proyecto_id, recurso_id, periodo, estado) VALUES (?,?,?,?)";
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

    private void validarAsignacion() throws Exception {
        String sql = "SELECT COUNT(*) FROM proyecto_recurso WHERE proyecto_id=? AND recurso_id=?";
        try (Connection conn = new ConexionBDD().conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, proyectoId); stmt.setInt(2, recursoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next() || rs.getInt(1) == 0)
                    throw new Exception("El recurso debe estar asignado al proyecto antes de crear la hoja.");
            }
        }
    }

    public void actualizar() throws Exception {
        validarEstadoModificable();
        rangoPeriodo();
        validarAsignacion();
        validarHorasMaximas();
        String sql = "UPDATE hoja_tiempo SET proyecto_id=?, recurso_id=?, periodo=?, estado=? WHERE id=?";
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

  
    public void cambiarEstado(String nuevoEstado, String rol) throws Exception {
        validarEstadoModificable();
        if (rol == null || !"Desarrollador".equalsIgnoreCase(rol))
            throw new Exception("Rol no permitido. Solo el desarrollador puede enviar la hoja.");
        if (!"Enviada".equalsIgnoreCase(nuevoEstado))
            throw new Exception("El desarrollador solo puede enviar la hoja (estado 'Enviada').");

        String sql = "{CALL sp_enviar_hoja(?, ?, ?, ?)}";
        try (Connection conn = new ConexionBDD().conectar();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, id);
            stmt.setDouble(2, maxHorasSemana);
            stmt.registerOutParameter(3, java.sql.Types.INTEGER);
            stmt.registerOutParameter(4, java.sql.Types.VARCHAR);
            stmt.execute();
            int ok = stmt.getInt(3);
            String msg = stmt.getString(4);
            if (ok != 1) throw new Exception(msg);
        }
        this.estado = "Enviada";
    }

 
    public void eliminar() throws Exception {
        if (!"Borrador".equalsIgnoreCase(this.estado))
            throw new Exception("Solo se puede eliminar una hoja de tiempo en estado 'Borrador'.");
        String sql = "UPDATE hoja_tiempo SET estado='Inactiva' WHERE id=?";
        try (Connection conn = new ConexionBDD().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public static List<HojaTiempo> listarTodas() throws Exception {
        List<HojaTiempo> lista = new ArrayList<>();
        String sql = "SELECT h.*, p.nombre AS proyecto_nombre FROM hoja_tiempo h "
               + "JOIN proyecto p ON p.id=h.proyecto_id WHERE h.estado <> 'Inactiva' ORDER BY h.id DESC";
        try (Connection conn = new ConexionBDD().conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new HojaTiempo(
                    rs.getInt("id"),
                    rs.getInt("proyecto_id"),
                    rs.getString("proyecto_nombre"), rs.getInt("recurso_id"),
                    rs.getString("periodo"),
                    rs.getString("estado")
                ));
            }
        }
        return lista;
    }

    public static HojaTiempo consultar(int id) throws Exception {
        String sql = "SELECT id, proyecto_id, recurso_id, periodo, estado FROM hoja_tiempo WHERE id=? AND estado <> 'Inactiva'";
        try (Connection conn = new ConexionBDD().conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) throw new Exception("No se encontró la hoja de tiempo.");
                return new HojaTiempo(rs.getInt("id"), rs.getInt("proyecto_id"), rs.getInt("recurso_id"),
                    rs.getString("periodo"), rs.getString("estado"));
            }
        }
    }

    public static double costoAcumuladoProyecto(int proyectoId) throws Exception {
        String sql = "SELECT COALESCE(SUM(d.horas * r.tarifa_base), 0) "
                   + "FROM detalle_actividad d JOIN hoja_tiempo h ON h.id=d.hoja_tiempo_id "
                   + "JOIN recurso r ON r.id=h.recurso_id WHERE h.proyecto_id=? AND h.estado <> 'Inactiva'";
        try (Connection conn = new ConexionBDD().conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, proyectoId);
            try (ResultSet rs = stmt.executeQuery()) { return rs.next() ? rs.getDouble(1) : 0; }
        }
    }

    public static List<HojaTiempo> listarPorFiltro(Integer proyectoId, Integer recursoId, String periodo) throws Exception {
        List<HojaTiempo> lista = new ArrayList<>();
        String sql = "SELECT h.*, p.nombre AS proyecto_nombre FROM hoja_tiempo h "
               + "JOIN proyecto p ON p.id=h.proyecto_id WHERE h.estado <> 'Inactiva' "
               + "AND (? IS NULL OR h.proyecto_id=?) AND (? IS NULL OR h.recurso_id=?) "
               + "AND (? IS NULL OR h.periodo=?) ORDER BY h.id DESC";
        try (Connection conn = new ConexionBDD().conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, proyectoId); stmt.setObject(2, proyectoId);
            stmt.setObject(3, recursoId); stmt.setObject(4, recursoId);
            stmt.setString(5, periodo); stmt.setString(6, periodo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(new HojaTiempo(rs.getInt("id"), rs.getInt("proyecto_id"),
                    rs.getString("proyecto_nombre"), rs.getInt("recurso_id"), rs.getString("periodo"), rs.getString("estado")));
            }
        }
        return lista;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProyectoId() {
        return proyectoId;
    }

    public String getProyectoNombre() {
        return proyectoNombre;
    }

    public void setProyectoId(int proyectoId) {
        this.proyectoId = proyectoId;
    }

    public int getRecursoId() {
        return recursoId;
    }

    public void setRecursoId(int recursoId) {
        this.recursoId = recursoId;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<DetalleActividad> getDetalles() {
        return detalles;
    }

   
    public static List<HojaTiempo> listarPorRecursoYMes(int recursoId, String mesAnio) throws Exception {
        List<HojaTiempo> lista = new ArrayList<>();
        String sql = "SELECT h.*, p.nombre AS proyecto_nombre FROM hoja_tiempo h "
                   + "JOIN proyecto p ON p.id = h.proyecto_id "
                   + "WHERE h.recurso_id = ? AND h.estado <> 'Inactiva' "
                   + "AND LEFT(h.periodo, 7) = ? ORDER BY h.periodo";
        try (Connection conn = new ConexionBDD().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recursoId);
            stmt.setString(2, mesAnio);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new HojaTiempo(
                        rs.getInt("id"),
                        rs.getInt("proyecto_id"),
                        rs.getString("proyecto_nombre"),
                        rs.getInt("recurso_id"),
                        rs.getString("periodo"),
                        rs.getString("estado")
                    ));
                }
            }
        }
        return lista;
    }

    @Override
    public String toString() {
        return "Hoja " + id + " | Proyecto " + proyectoId + " | Recurso " + recursoId + " | " + periodo + " | " + estado;
    }
}
