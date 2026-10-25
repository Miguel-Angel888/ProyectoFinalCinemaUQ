package co.edu.uniquindio.poo.proyectofinalcinemauq.model.promocion;

import java.util.List;

// PATRÓN DECORATOR - Decorador base
// Envuelve otra cotización y le agrega un descuento sin modificarla.
public abstract class PromocionDecorator implements Cotizacion {

    protected final Cotizacion envuelta;
    private final String nombre;

    protected PromocionDecorator(Cotizacion envuelta, String nombre) {
        if (envuelta == null) {
            throw new IllegalArgumentException("El decorador necesita una cotización a envolver");
        }
        this.envuelta = envuelta;
        this.nombre = nombre;
    }

    // Cada decorador concreto decide cuánto descuenta sobre el total que recibe
    protected abstract double calcularDescuento(double totalActual);

    @Override
    public double calcularTotal() {
        double totalActual = envuelta.calcularTotal();
        return totalActual - descuentoReal(totalActual);
    }

    @Override
    public List<DescuentoAplicado> getDescuentosAplicados() {
        List<DescuentoAplicado> descuentos = envuelta.getDescuentosAplicados();
        descuentos.add(new DescuentoAplicado(nombre, descuentoReal(envuelta.calcularTotal())));
        return descuentos;
    }

    // El descuento nunca puede dejar el total en negativo
    private double descuentoReal(double totalActual) {
        return Math.max(0, Math.min(calcularDescuento(totalActual), totalActual));
    }
}
