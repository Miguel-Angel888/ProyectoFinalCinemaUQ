package co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException.StockInsuficienteException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// PATRÓN COMPOSITE - Compuesto
// Un combo contiene ítems (productos u otros combos) y delega en ellos
// el cálculo del precio, los puntos y el control de stock.
public class Combo implements ItemConfiteria {

    private final String referencia;
    private final String nombre;
    private final double porcentajeDescuento;   // ahorro del combo frente a comprar por separado
    private final List<ItemConfiteria> componentes;

    public Combo(String referencia, String nombre, double porcentajeDescuento) {
        if (porcentajeDescuento < 0 || porcentajeDescuento >= 100) {
            throw new IllegalArgumentException("El descuento del combo debe estar entre 0 y 100");
        }
        this.referencia = referencia;
        this.nombre = nombre;
        this.porcentajeDescuento = porcentajeDescuento;
        this.componentes = new ArrayList<>();
    }

    public Combo agregar(ItemConfiteria item) {
        if (item == this) {
            throw new IllegalArgumentException("Un combo no puede contenerse a sí mismo");
        }
        componentes.add(item);
        return this;
    }

    public void quitar(ItemConfiteria item) {
        componentes.remove(item);
    }

    @Override
    public double getPrecio() {
        double suma = 0;
        for (ItemConfiteria item : componentes) suma += item.getPrecio();
        return suma * (1 - porcentajeDescuento / 100);
    }

    @Override
    public int getPuntosRedencion() {
        int suma = 0;
        for (ItemConfiteria item : componentes) suma += item.getPuntosRedencion();
        return (int) (suma * (1 - porcentajeDescuento / 100));
    }

    @Override
    public String getDescripcion() {
        List<String> nombres = new ArrayList<>();
        for (ItemConfiteria item : componentes) nombres.add(item.getNombre());
        return "Incluye: " + String.join(", ", nombres);
    }

    // Hay stock del combo solo si hay stock de cada uno de sus componentes
    @Override
    public boolean hayStock(int cantidad) {
        for (ItemConfiteria item : componentes) {
            if (!item.hayStock(cantidad)) return false;
        }
        return true;
    }

    @Override
    public void descontarStock(int cantidad) throws StockInsuficienteException {
        if (!hayStock(cantidad)) {
            throw new StockInsuficienteException("No hay suficiente stock para el " + nombre);
        }
        for (ItemConfiteria item : componentes) item.descontarStock(cantidad);
    }

    @Override public String getReferencia() { return referencia; }
    @Override public String getNombre() { return nombre; }
    public double getPorcentajeDescuento() { return porcentajeDescuento; }
    public List<ItemConfiteria> getComponentes() { return Collections.unmodifiableList(componentes); }

    @Override
    public String toString() {
        return nombre;
    }
}
