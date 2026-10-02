package co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta;

public class LineaCompra {

    private final Producto producto;
    private final int cantidad;
    private final double precioUnitario;   // precio al momento de la compra

    public LineaCompra(Producto producto, int cantidad) {
        if (producto == null || cantidad <= 0) {
            throw new IllegalArgumentException("La línea requiere un producto y una cantidad mayor a cero");
        }
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecio();
    }

    public double getSubtotal() { return precioUnitario * cantidad; }

    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
}