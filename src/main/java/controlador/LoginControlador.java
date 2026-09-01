package controlador;

import modelo.Login;
import vista.AsignacionVista;
import vista.HojaTiempoVista;
import vista.LoginVista;
import vista.ProyectoVista;
import vista.RecursoVista;
import vista.ReporteVista;

/** Coordina la autenticacion y la navegacion segun el tipo de usuario. */
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
            ProyectoVista proyectos = new ProyectoVista();
            new ProyectoControlador(proyectos);
            NavegacionControlador.configurar(proyectos,
                    () -> abrirRecursos(proyectos), () -> cerrarSesion(proyectos));
            proyectos.setVisible(true);
        } else {
            HojaTiempoVista hojas = new HojaTiempoVista();
            new HojaTiempoControlador(hojas, usuario.getRol(), usuario.getRecursoId());
                NavegacionControlador.configurar(hojas,
                    () -> abrirReporte(hojas, null, usuario.getRecursoId()), () -> cerrarSesion(hojas));
            hojas.setVisible(true);
        }
    }

    private void abrirRecursos(java.awt.Window anterior) {
        anterior.setVisible(false);
        RecursoVista recursos = new RecursoVista();
        new RecursoControlador(recursos);
        NavegacionControlador.configurar(recursos,
            () -> abrirAsignaciones(recursos), () -> mostrarAnterior(recursos, anterior));
        recursos.setVisible(true);
    }

    private void abrirAsignaciones(java.awt.Window anterior) {
        anterior.setVisible(false);
        AsignacionVista asignaciones = new AsignacionVista();
        NavegacionControlador.configurar(asignaciones,
            () -> abrirReporte(asignaciones, anterior, null), () -> mostrarAnterior(asignaciones, anterior));
        asignaciones.setVisible(true);
    }

    private void abrirReporte(java.awt.Window anterior, java.awt.Window volver, Integer recursoUsuarioId) {
        anterior.setVisible(false);
        ReporteVista reportes = new ReporteVista();
        new ReporteControlador(reportes, recursoUsuarioId);
        NavegacionControlador.configurar(reportes, () -> { },
                () -> {
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
