package modelo;

public class DesarrolladorJunior extends Recurso {

    public DesarrolladorJunior() {
        super();
        this.tipo = "Junior";
        this.rol = "Desarrollador";
    }

    public DesarrolladorJunior(int id, String nombre, String cedula, String correo, double tarifaBase, String estado) {
        super(id, nombre, cedula, correo, "Desarrollador", tarifaBase, "Junior", estado);
    }

    
    @Override
    public double calcularTarifaHora() {
        return getTarifaBase();
    }
}
