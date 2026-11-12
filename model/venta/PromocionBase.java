package co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta;

import java.time.LocalDate;

// Datos y vigencia comunes a las promociones concretas
public abstract class PromocionBase implements Promocion {

    private final String nombre;
    private final LocalDate desde;
    private final LocalDate hasta;

    protected PromocionBase(String nombre, LocalDate desde, LocalDate hasta) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("La promoción necesita un nombre");
        }
        if (desde == null || hasta == null || hasta.isBefore(desde)) {
            throw new IllegalArgumentException("La promoción necesita un periodo de vigencia válido");
        }
        this.nombre = nombre;
        this.desde = desde;
        this.hasta = hasta;
    }

    @Override
    public String getNombre() { return nombre; }

    @Override
    public boolean estaVigente(LocalDate fecha) {
        return !fecha.isBefore(desde) && !fecha.isAfter(hasta);
    }

    public LocalDate getDesde() { return desde; }
    public LocalDate getHasta() { return hasta; }
}
