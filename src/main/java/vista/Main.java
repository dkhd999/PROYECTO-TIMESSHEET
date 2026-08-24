package vista;

/**
 * Punto de entrada del sistema.
 * Abre la vista ProyectoVista al iniciar.
 */
public class Main {

    public static void main(String[] args) {
        // Lanza la ventana principal del sistema
        java.awt.EventQueue.invokeLater(() -> {
            new ProyectoVista().setVisible(true);
        });
    }
}
