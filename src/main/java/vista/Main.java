package vista;

/**
 * Punto de entrada del sistema.
 * Abre el inicio de sesion al iniciar.
 */
public class Main {

    public static void main(String[] args) {
        // Lanza la ventana principal del sistema
        java.awt.EventQueue.invokeLater(() -> {
            LoginVista vista = new LoginVista();
            new controlador.LoginControlador(vista);
            vista.setVisible(true);
        });
    }
}
