package controlador; 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBDD {
    // ATRIBUTO
    Connection conexion;

    public Connection conectar() throws SQLException {
        // LANZAR CÓDIGO DE PRUEBA 
        try {
            // Driver actualizado de MySQL para evitar la advertencia de deprecación
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Parámetros de conexión url/usuario/clave en mysql
            conexion = DriverManager.getConnection(
                "jdbc:mysql://localhost/proyecto_2?autoReconnect=true&useSSL=false&serverTimezone=UTC",
                "root",
                "clientmod23"
            );

            System.out.println("CONECTADO"); 
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el controlador de MySQL.", e);
        }
        return conexion;
    }
}