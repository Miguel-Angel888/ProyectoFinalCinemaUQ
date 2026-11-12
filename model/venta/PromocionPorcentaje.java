package co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta;

import java.time.LocalDate;

// Descuenta un porcentaje del subtotal. Ej: 15 % de descuento
public class PromocionPorcentaje extends PromocionBase {

    private final double porcentaje;

    public PromocionPorcentaje(String nombre, LocalDate desde, LocalDate hasta, double porcentaje) {
        super(nombre, desde, hasta);
        if (porcentaje <= 0 || porcentaje > 100) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 100");
        }
        this.porcentaje = porcentaje;
    }

    @Override
    public double aplicar(double subtotal) {
        return subtotal * porcentaje / 100;
    }

    public double getPorcentaje() { return porcentaje; }
}
