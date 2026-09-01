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

    // RF-02.2: Tarifa horaria fija para desarrolladores Senior
    @Override
    public double calcularTarifaHora() {
        return getTarifaBase();
    }
}
