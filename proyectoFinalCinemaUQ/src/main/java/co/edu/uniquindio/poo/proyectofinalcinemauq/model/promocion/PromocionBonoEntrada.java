package co.edu.uniquindio.poo.proyectofinalcinemauq.model.promocion;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.recompensa.Canje;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Entrada;

import java.util.List;

// Un bono de entrada canjeado con puntos entra a la compra como una promoción más,
// así reutiliza el Decorator de descuentos que ya existe.
public class PromocionBonoEntrada implements Promocion {

    private final Canje bono;
    private final double descuento;

    // descuento = lo que cubre el bono, sin pasarse del precio de la entrada
    public PromocionBonoEntrada(Canje bono, double precioEntrada) {
        this.bono = bono;
        this.descuento = Math.min(bono.getRecompensa().getValorMaximo(), precioEntrada);
    }

    @Override
    public String getNombre() {
        return "Bono " + bono.getRecompensa().getNombre();
    }

    @Override
    public boolean esAplicable(List<Entrada> entradas, double subtotal) {
        return bono.estaDisponible() && !entradas.isEmpty();
    }

    @Override
    public Cotizacion aplicar(Cotizacion cotizacion) {
        return new DescuentoMontoFijo(cotizacion, getNombre(), descuento);
    }

    public Canje getBono() { return bono; }
}
