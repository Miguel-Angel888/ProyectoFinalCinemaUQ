package co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta;

public class Producto {

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

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

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

    public int getPuntosRedencion() {
        return puntosRedencion;
    }

    public void setPuntosRedencion(int puntosRedencion) {
        this.puntosRedencion = puntosRedencion;
    }
}
