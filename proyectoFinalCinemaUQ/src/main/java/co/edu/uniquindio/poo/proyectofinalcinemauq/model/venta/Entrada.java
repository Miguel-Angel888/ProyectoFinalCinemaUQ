package co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta;

public class Entrada {

    //Atributos
    private String referencia;
    private Funcion funcion;
    private Asiento asiento;
    private TipoEntrada tipoEntrada;
    private double precio;

    public Entrada(String referencia, Funcion funcion, Asiento asiento, TipoEntrada tipoEntrada, double precio) {
        this.referencia = referencia;
        this.funcion = funcion;
        this.asiento = asiento;
        this.tipoEntrada = tipoEntrada;
        this.precio = precio;

    }


    //Getters y setters

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Funcion getFuncion() {
        return funcion;
    }

    public void setFuncion(Funcion funcion) {
        this.funcion = funcion;
    }

    public Asiento getAsiento() {
        return asiento;
    }

    public void setAsiento(Asiento asiento) {
        this.asiento = asiento;
    }

    public TipoEntrada getTipoEntrada() {
        return tipoEntrada;
    }

    public void setTipoEntrada(TipoEntrada tipoEntrada) {
        this.tipoEntrada = tipoEntrada;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}
