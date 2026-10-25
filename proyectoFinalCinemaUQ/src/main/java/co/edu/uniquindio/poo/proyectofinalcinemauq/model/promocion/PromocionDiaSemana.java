package co.edu.uniquindio.poo.proyectofinalcinemauq.model.promocion;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Entrada;

import java.time.DayOfWeek;
import java.util.List;

// Ej: "Martes de cine": 20 % de descuento si la función es un martes
public class PromocionDiaSemana implements Promocion {

    private final String nombre;
    private final DayOfWeek dia;
    private final double porcentaje;

    public PromocionDiaSemana(String nombre, DayOfWeek dia, double porcentaje) {
        this.nombre = nombre;
        this.dia = dia;
        this.porcentaje = porcentaje;
    }

    @Override
    public boolean esAplicable(List<Entrada> entradas, double subtotal) {
        return !entradas.isEmpty()
                && entradas.get(0).getFuncion().getFechaFuncion().getDayOfWeek() == dia;
    }

    @Override
    public Cotizacion aplicar(Cotizacion cotizacion) {
        return new DescuentoPorcentaje(cotizacion, nombre, porcentaje);
    }

    @Override
    public String getNombre() { return nombre; }
}
