package co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta;

import java.time.LocalDateTime;

public class Pago {

    //Atributos
    private String referencia;
    private Compra compra;
    private double cantidad;
    private LocalDateTime fechaPago;
    private boolean aprobado;

    //Constructor


    public Pago(String referencia, Compra compra, double cantidad, LocalDateTime fechaPago, boolean aprobado) {
        this.referencia = referencia;
        this.compra = compra;
        this.cantidad = cantidad;
        this.fechaPago = fechaPago;
        this.aprobado = aprobado;
    }

    //Getters y setters

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Compra getCompra() {
        return compra;
    }

    public void setCompra(Compra compra) {
        this.compra = compra;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public boolean isAprobado() {
        return aprobado;
    }

    public void setAprobado(boolean aprobado) {
        this.aprobado = aprobado;
    }
}
