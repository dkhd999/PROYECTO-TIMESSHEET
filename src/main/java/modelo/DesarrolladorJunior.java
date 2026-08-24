package modelo;

public class DesarrolladorJunior extends Recurso {

    public DesarrolladorJunior() {
        super();
        this.tipo = "Junior";
        this.rol = "Desarrollador";
    }

    public DesarrolladorJunior(int id, String nombre, String correo, double tarifaBase, String estado) {
        super(id, nombre, correo, "Desarrollador", tarifaBase, "Junior", estado);
    }

    // RF-02.2: Tarifa fija sin bono
    @Override
    public double calcularTarifaHora() {
        return getTarifaBase();
    }
}
