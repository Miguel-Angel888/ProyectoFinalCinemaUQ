package co.edu.uniquindio.poo.proyectofinalcinemauq.model.promocion;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Entrada;

import java.util.List;

// Promoción registrada en el cine. Sabe cuándo aplica y con qué decorador
// envolver la cotización de la compra.
public interface Promocion {

    String getNombre();

    boolean esAplicable(List<Entrada> entradas, double subtotal);

    Cotizacion aplicar(Cotizacion cotizacion);
}
