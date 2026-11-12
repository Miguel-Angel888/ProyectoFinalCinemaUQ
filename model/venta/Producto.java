package co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta;

// Leaf del patrón Composite
public class Producto implements ItemVendible {

    //Atributos
    private String referencia;
    private String nombre;
    private CategoriaProducto categoriaProducto;
    private double precio;
    private int stock;
    private int puntosRedencion;

    //Constructor


    public Producto(String referencia, String nombre,
                    CategoriaProducto categoriaProducto, double precio, int stock, int puntosRedencion) {
        this.referencia = referencia;
        this.nombre = nombre;
        this.categoriaProducto = categoriaProducto;
        this.precio = precio;
        this.stock = stock;
        this.puntosRedencion = puntosRedencion;
    }

    //Getters y setters

    @Override
    public int getStockDisponible() {
        return stock;
    }

    @Override
    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public CategoriaProducto getCategoriaProducto() {
        return categoriaProducto;
    }

    public void setCategoriaProducto(CategoriaProducto categoriaProducto) {
        this.categoriaProducto = categoriaProducto;
    }

    @Override
    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public int getPuntosRedencion() {
        return puntosRedencion;
    }

    public void setPuntosRedencion(int puntosRedencion) {
        this.puntosRedencion = puntosRedencion;
    }
}
