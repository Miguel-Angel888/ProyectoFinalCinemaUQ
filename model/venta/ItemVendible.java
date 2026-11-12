package co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta;

// Component del patrón Composite: tanto un Producto (hoja) como un Combo (compuesto)
// se pueden comprar y redimir con puntos de la misma forma
public interface ItemVendible {

    String getReferencia();

    String getNombre();

    double getPrecio();

    int getPuntosRedencion();

    // Unidades que se pueden vender con el inventario actual
    int getStockDisponible();
}
