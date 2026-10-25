package co.edu.uniquindio.poo.proyectofinalcinemauq.model.promocion;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Entrada;

import java.util.List;

// Ej: "Plan grupal": descuento fijo al comprar cierta cantidad de entradas o más
public class PromocionPorCantidad implements Promocion {

    private final String nombre;
    private final int minimoEntradas;
    private final double montoDescuento;

    public PromocionPorCantidad(String nombre, int minimoEntradas, double montoDescuento) {
        this.nombre = nombre;
        this.minimoEntradas = minimoEntradas;
        this.montoDescuento = montoDescuento;
    }

    @Override
    public boolean esAplicable(List<Entrada> entradas, double subtotal) {
        return entradas.size() >= minimoEntradas;
    }

    @Override
    public Cotizacion aplicar(Cotizacion cotizacion) {
        return new DescuentoMontoFijo(cotizacion, nombre, montoDescuento);
    }

    @Override
    public String getNombre() { return nombre; }
}
