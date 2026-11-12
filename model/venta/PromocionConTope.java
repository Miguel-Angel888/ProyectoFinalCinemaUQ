package co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta;

import java.time.LocalDate;

// Decorator del patrón Decorator: envuelve cualquier promoción y limita su descuento.
// Ej: 20 % de descuento con un máximo de $10.000
public class PromocionConTope implements Promocion {

    private final Promocion promocion;
    private final double tope;

    public PromocionConTope(Promocion promocion, double tope) {
        if (promocion == null) {
            throw new IllegalArgumentException("Se requiere la promoción a decorar");
        }
        if (tope <= 0) {
            throw new IllegalArgumentException("El tope debe ser mayor a cero");
        }
        this.promocion = promocion;
        this.tope = tope;
    }

    @Override
    public String getNombre() {
        return promocion.getNombre() + " (tope $" + tope + ")";
    }

    @Override
    public boolean estaVigente(LocalDate fecha) {
        return promocion.estaVigente(fecha);
    }

    @Override
    public double aplicar(double subtotal) {
        return Math.min(promocion.aplicar(subtotal), tope);
    }

    public double getTope() { return tope; }
}
