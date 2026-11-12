package co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Composite del patrón Composite: agrupa ítems vendibles (productos u otros combos)
// y los trata como un solo ítem hacia afuera
public class Combo implements ItemVendible {

    private String referencia;
    private String nombre;
    private double porcentajeDescuento;   // entre 0 y 100, sobre la suma de los componentes
    private final List<ItemVendible> componentes;

    public Combo(String referencia, String nombre, double porcentajeDescuento) {
        if (porcentajeDescuento < 0 || porcentajeDescuento >= 100) {
            throw new IllegalArgumentException("El descuento del combo debe estar entre 0 y 100");
        }
        this.referencia = referencia;
        this.nombre = nombre;
        this.porcentajeDescuento = porcentajeDescuento;
        this.componentes = new ArrayList<>();
    }

    // operaciones

    public void agregarComponente(ItemVendible item, int cantidad) {
        if (item == null || cantidad <= 0) {
            throw new IllegalArgumentException("El componente requiere un ítem y una cantidad mayor a cero");
        }
        // un combo no puede contenerse a sí mismo, ni directa ni indirectamente
        if (item == this || (item instanceof Combo otro && otro.contiene(this))) {
            throw new IllegalArgumentException("Un combo no puede contenerse a sí mismo");
        }
        for (int i = 0; i < cantidad; i++) {
            componentes.add(item);
        }
    }

    public boolean contiene(ItemVendible item) {
        for (ItemVendible c : componentes) {
            if (c == item) return true;
            if (c instanceof Combo combo && combo.contiene(item)) return true;
        }
        return false;
    }

    private double sumaComponentes() {
        double suma = 0;
        for (ItemVendible c : componentes) suma += c.getPrecio();
        return suma;
    }

    // ItemVendible

    @Override
    public double getPrecio() {
        return sumaComponentes() * (1 - porcentajeDescuento / 100);
    }

    // los puntos del combo son los de sus componentes con el mismo descuento del combo
    @Override
    public int getPuntosRedencion() {
        int suma = 0;
        for (ItemVendible c : componentes) suma += c.getPuntosRedencion();
        return (int) Math.round(suma * (1 - porcentajeDescuento / 100));
    }

    // el combo se puede vender tantas veces como lo permita su componente más escaso
    @Override
    public int getStockDisponible() {
        if (componentes.isEmpty()) return 0;
        Map<ItemVendible, Integer> necesarios = new LinkedHashMap<>();
        for (ItemVendible c : componentes) necesarios.merge(c, 1, Integer::sum);

        int minimo = Integer.MAX_VALUE;
        for (Map.Entry<ItemVendible, Integer> e : necesarios.entrySet()) {
            minimo = Math.min(minimo, e.getKey().getStockDisponible() / e.getValue());
        }
        return minimo;
    }

    // Getters y setters

    @Override
    public String getReferencia() { return referencia; }

    public void setReferencia(String referencia) { this.referencia = referencia; }

    @Override
    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPorcentajeDescuento() { return porcentajeDescuento; }

    public void setPorcentajeDescuento(double porcentajeDescuento) {
        if (porcentajeDescuento < 0 || porcentajeDescuento >= 100) {
            throw new IllegalArgumentException("El descuento del combo debe estar entre 0 y 100");
        }
        this.porcentajeDescuento = porcentajeDescuento;
    }

    public List<ItemVendible> getComponentes() {
        return Collections.unmodifiableList(componentes);
    }
}
