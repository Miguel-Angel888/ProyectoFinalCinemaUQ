package co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException.StockInsuficienteException;

// PATRÓN COMPOSITE - Hoja
public class Producto implements ItemConfiteria {

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

    //Composite

    @Override
    public String getDescripcion() {
        return categoriaProducto.name().charAt(0) + categoriaProducto.name().substring(1).toLowerCase();
    }

    @Override
    public boolean hayStock(int cantidad) {
        return stock >= cantidad;
    }

    @Override
    public void descontarStock(int cantidad) throws StockInsuficienteException {
        if (!hayStock(cantidad)) {
            throw new StockInsuficienteException("No hay suficiente stock de " + nombre + ". Disponible: " + stock);
        }
        stock -= cantidad;
    }

    //Getters y setters

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

    @Override
    public String toString() {
        return nombre;
    }
}
