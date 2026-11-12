package co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta;

import java.time.LocalDate;

// Descuenta un valor fijo. Ej: $5.000 menos en la compra
public class PromocionMontoFijo extends PromocionBase {

    private final double monto;

    public PromocionMontoFijo(String nombre, LocalDate desde, LocalDate hasta, double monto) {
        super(nombre, desde, hasta);
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto del descuento debe ser mayor a cero");
        }
        this.monto = monto;
    }

    // nunca descuenta más de lo que vale la compra
    @Override
    public double aplicar(double subtotal) {
        return Math.min(monto, subtotal);
    }

    public double getMonto() { return monto; }
}
