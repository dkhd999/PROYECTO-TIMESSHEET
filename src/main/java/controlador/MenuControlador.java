package controlador;

import vista.AsignacionVista;
import vista.MenuVista;
import vista.ProyectoVista;
import vista.RecursoVista;
import vista.ReporteEstadisticoVista;
import vista.ReporteVista;

/**
 * Coordina la navegacion del menu administrativo (MenuVista). Cada boton del
 * menu abre su modulo; "Siguiente" encadena modulos, "Regresar" retrocede y
 * "Ir al Menu" en cualquier vista admin vuelve siempre al hub.
 */
public class MenuControlador {

    private final MenuVista vista;

    public MenuControlador(MenuVista vista) {
        this.vista = vista;
        vista.getBtnGestionProyecto().addActionListener(e -> abrirProyecto(vista));
        vista.getBtnRegistroDev().addActionListener(e -> abrirRecurso(vista));
        vista.getBtnAsignarDev().addActionListener(e -> abrirAsignacion(vista));
        vista.getBtnGenerarReporte().addActionListener(e -> abrirReporte(vista, vista));
        vista.getBtnGenerarReporteEsta().addActionListener(e -> abrirReporteEstadistico(vista, vista));
    }

    private void abrirProyecto(java.awt.Window anterior) {
        anterior.setVisible(false);
        ProyectoVista pv = new ProyectoVista();
        new ProyectoControlador(pv);
        pv.getBtnSiguiente().addActionListener(e -> abrirRecurso(pv));
        pv.getBtnRegresar().addActionListener(e -> irMenu(pv));
        pv.getBtnIrMenu().addActionListener(e -> irMenu(pv));
        pv.setVisible(true);
    }

    private void abrirRecurso(java.awt.Window anterior) {
        anterior.setVisible(false);
        RecursoVista rv = new RecursoVista();
        new RecursoControlador(rv);
        rv.getBtnSiguiente().addActionListener(e -> abrirAsignacion(rv));
        rv.getBtnRegresar().addActionListener(e -> mostrarAnterior(rv, anterior));
        rv.getBtnIrMenu().addActionListener(e -> irMenu(rv));
        rv.setVisible(true);
    }

    private void abrirAsignacion(java.awt.Window anterior) {
        anterior.setVisible(false);
        AsignacionVista av = new AsignacionVista();
        new AsignacionControlador(av);
        av.getBtnSiguiente().addActionListener(e -> abrirReporte(av, anterior));
        av.getBtnRegresar().addActionListener(e -> mostrarAnterior(av, anterior));
        av.getBtnIrMenu().addActionListener(e -> irMenu(av));
        av.setVisible(true);
    }

    private void abrirReporte(java.awt.Window anterior, java.awt.Window volver) {
        anterior.setVisible(false);
        ReporteVista rv = new ReporteVista();
        new ReporteControlador(rv, null);
        rv.getBtnSiguiente().addActionListener(e -> abrirReporteEstadistico(rv, anterior));
        rv.getBtnRegresar().addActionListener(e -> {
            rv.dispose();
            if (volver == null) mostrarAnterior(rv, anterior);
            else volver.setVisible(true);
        });
        rv.getBtnIrMenu().addActionListener(e -> irMenu(rv));
        rv.setVisible(true);
    }

    private void abrirReporteEstadistico(java.awt.Window anterior, java.awt.Window volver) {
        anterior.setVisible(false);
        ReporteEstadisticoVista ev = new ReporteEstadisticoVista();
        new ReporteEstadisticoControlador(ev);
        ev.getBtnRegresar().addActionListener(e -> {
            ev.dispose();
            if (volver == null) mostrarAnterior(ev, anterior);
            else volver.setVisible(true);
        });
        ev.getBtnIrMenu().addActionListener(e -> irMenu(ev));
        ev.setVisible(true);
    }

    private void irMenu(java.awt.Window actual) {
        actual.dispose();
        vista.setVisible(true);
        vista.toFront();
    }

    private void mostrarAnterior(java.awt.Window actual, java.awt.Window anterior) {
        actual.dispose();
        if (anterior != null) anterior.setVisible(true);
    }
}