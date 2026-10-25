package co.edu.uniquindio.poo.proyectofinalcinemauq.model.promocion;

// PATRÓN DECORATOR - Decorador concreto: descuenta un porcentaje del total
public class DescuentoPorcentaje extends PromocionDecorator {

    private final double porcentaje;

    public DescuentoPorcentaje(Cotizacion envuelta, String nombre, double porcentaje) {
        super(envuelta, nombre);
        this.porcentaje = porcentaje;
    }

    @Override
    protected double calcularDescuento(double totalActual) {
        return totalActual * porcentaje / 100;
    }
}
