package modelo;

public class DesarrolladorSenior extends Recurso {

    private static final double BONO_EXPERIENCIA = 1.5;

    public DesarrolladorSenior() {
        super();
        this.tipo = "Senior";
        this.rol = "Desarrollador";
    }

    public DesarrolladorSenior(int id, String nombre, String correo, double tarifaBase, String estado) {
        super(id, nombre, correo, "Desarrollador", tarifaBase, "Senior", estado);
    }

    // RF-02.2: Tarifa base + bono por experiencia del 50%
    @Override
    public double calcularTarifaHora() {
        return getTarifaBase() * BONO_EXPERIENCIA;
    }
}
