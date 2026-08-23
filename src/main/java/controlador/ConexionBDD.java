package controlador; 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBDD {
    // ATRIBUTO
    Connection conexion;

    public Connection conectar() {
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
        } catch (ClassNotFoundException | SQLException e) { // CAPTURAR ERRORES 
            System.out.println("ERROR DE CONEXION A LA BASE DE DATOS: " + e.getMessage());
        }
        return conexion;
    }
}