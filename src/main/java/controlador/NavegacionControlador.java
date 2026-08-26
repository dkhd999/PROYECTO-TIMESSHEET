package controlador;

import java.awt.FlowLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

/** Añade los controles de navegación sin poner lógica de flujo en las vistas. */
public final class NavegacionControlador {

    private NavegacionControlador() { }

    public static void configurar(JFrame vista, Runnable siguiente, Runnable regresar) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        panel.setOpaque(false);

        JButton btnRegresar = new JButton("Regresar");
        JButton btnSiguiente = new JButton("Siguiente");
        btnRegresar.addActionListener(event -> regresar.run());
        btnSiguiente.addActionListener(event -> siguiente.run());
        panel.add(btnRegresar);
        panel.add(btnSiguiente);

        vista.getLayeredPane().add(panel, JLayeredPane.PALETTE_LAYER);
        Runnable ajustarPosicion = () -> panel.setBounds(
                0, vista.getRootPane().getHeight() - 48,
                vista.getRootPane().getWidth(), 40);
        vista.addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent event) {
                ajustarPosicion.run();
            }
        });
        ajustarPosicion.run();
    }
}
