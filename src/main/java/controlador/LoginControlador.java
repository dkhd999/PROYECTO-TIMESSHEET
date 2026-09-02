package controlador;

import modelo.Login;
import vista.HojaTiempoVista;
import vista.LoginVista;
import vista.MenuVista;
import vista.ReporteVista;

/**
 * Coordina la autenticacion y la navegacion segun el tipo de usuario.
 * Los botones "Siguiente"/"Regresar" estan integrados en cada vista y se
 * cablean aqui con la logica de flujo.
 */


public class LoginControlador {

    private final LoginVista vista;

    public LoginControlador(LoginVista vista) {
        this.vista = vista;
        vista.getBtnIngresar().addActionListener(event -> ingresar());
        vista.getPassword().addActionListener(event -> ingresar());
    }

    private void ingresar() {
        try {
                Login usuario = Login.autenticar(vista.getTxtUsuario().getText(),
                    new String(vista.getPassword().getPassword()));
            vista.dispose();
            abrirVistas(usuario);
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(vista, ex.getMessage(),
                    "Inicio de sesion", javax.swing.JOptionPane.WARNING_MESSAGE);
            vista.getPassword().setText("");
        }
    }

    private void abrirVistas(Login usuario) {
        if ("Gestor".equalsIgnoreCase(usuario.getTipo())) {
            MenuVista menu = new MenuVista();
            new MenuControlador(menu);
            menu.setVisible(true);
        } else {
            HojaTiempoVista hojas = new HojaTiempoVista();
            new HojaTiempoControlador(hojas, usuario.getRol(), usuario.getRecursoId());
            hojas.getBtnSiguiente().addActionListener(event -> abrirReporte(hojas, null, usuario.getRecursoId()));
            hojas.getBtnRegresar().addActionListener(event -> cerrarSesion(hojas));
            hojas.setVisible(true);
        }
    }

    private void abrirReporte(java.awt.Window anterior, java.awt.Window volver, Integer recursoUsuarioId) {
        anterior.setVisible(false);
        ReporteVista reportes = new ReporteVista();
        new ReporteControlador(reportes, recursoUsuarioId);
        reportes.getBtnSiguiente().setEnabled(false);
        reportes.getBtnRegresar().addActionListener(event -> {
            reportes.dispose();
            if (volver == null) mostrarAnterior(reportes, anterior);
            else volver.setVisible(true);
        });
        reportes.setVisible(true);
    }

    private void cerrarSesion(java.awt.Window actual) {
        actual.dispose();
        LoginVista login = new LoginVista();
        new LoginControlador(login);
        login.setVisible(true);
    }

    private void mostrarAnterior(java.awt.Window actual, java.awt.Window anterior) {
        actual.dispose();
        if (anterior != null) anterior.setVisible(true);
    }
}
