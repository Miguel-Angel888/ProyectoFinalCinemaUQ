package co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta;

public class LineaCompra {

    private final ItemConfiteria item;     // puede ser un producto o un combo (Composite)
    private final int cantidad;
    private final double precioUnitario;   // precio al momento de la compra

    public LineaCompra(ItemConfiteria item, int cantidad) {
        if (item == null || cantidad <= 0) {
            throw new IllegalArgumentException("La línea requiere un ítem y una cantidad mayor a cero");
        }
        this.item = item;
        this.cantidad = cantidad;
        this.precioUnitario = item.getPrecio();
    }

    public double getSubtotal() { return precioUnitario * cantidad; }

    public ItemConfiteria getItem() { return item; }
    public int getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
}
