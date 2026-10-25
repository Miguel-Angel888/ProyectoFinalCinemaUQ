package co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException.StockInsuficienteException;

// PATRÓN COMPOSITE - Componente
// Un producto suelto (hoja) y un combo (compuesto) se tratan igual:
// la compra, el carrito y la interfaz solo conocen esta interfaz.
public interface ItemConfiteria {

    String getReferencia();

    String getNombre();

    double getPrecio();

    int getPuntosRedencion();

    // Texto que describe el ítem (en un combo lista lo que contiene)
    String getDescripcion();

    boolean hayStock(int cantidad);

    void descontarStock(int cantidad) throws StockInsuficienteException;
}
