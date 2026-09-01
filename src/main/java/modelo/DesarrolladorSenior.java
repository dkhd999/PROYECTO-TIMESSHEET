package modelo;

public class DesarrolladorSenior extends Recurso {

    public DesarrolladorSenior() {
        super();
        this.tipo = "Senior";
        this.rol = "Desarrollador";
    }

    public DesarrolladorSenior(int id, String nombre, String cedula, String correo, double tarifaBase, String estado) {
        super(id, nombre, cedula, correo, "Desarrollador", tarifaBase, "Senior", estado);
    }

    @Override
    public double calcularTarifaHora() {
        return getTarifaBase();
    }
}
