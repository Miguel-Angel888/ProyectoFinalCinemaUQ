package co.edu.uniquindio.poo.proyectofinalcinemauq.model.promocion;

// PATRÓN DECORATOR - Decorador concreto: descuenta un valor fijo del total
public class DescuentoMontoFijo extends PromocionDecorator {

    private final double monto;

    public DescuentoMontoFijo(Cotizacion envuelta, String nombre, double monto) {
        super(envuelta, nombre);
        this.monto = monto;
    }

    @Override
    protected double calcularDescuento(double totalActual) {
        return monto;
    }
}
