package co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Compra;

import java.time.LocalDateTime;

public class Reembolso {

    //Atributos
    private String referencia;
    private Compra compra;
    private double porcentaje;
    private double cantidad;
    private LocalDateTime fechaReembolso;
    private String descripcion;
    private MotivoReembolso motivo;

    //Constructor


    public Reembolso(Compra compra, String referencia, double porcentaje, double cantidad,
                     LocalDateTime fechaReembolso, String descripcion, MotivoReembolso motivo) {
        this.compra = compra;
        this.referencia = referencia;
        this.porcentaje = porcentaje;
        this.cantidad = cantidad;
        this.fechaReembolso = fechaReembolso;
        this.descripcion = descripcion;
        this.motivo = motivo;
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

    public double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(double porcentaje) {
        this.porcentaje = porcentaje;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getFechaReembolso() {
        return fechaReembolso;
    }

    public void setFechaReembolso(LocalDateTime fechaReembolso) {
        this.fechaReembolso = fechaReembolso;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public MotivoReembolso getMotivo() {
        return motivo;
    }

    public void setMotivo(MotivoReembolso motivo) {
        this.motivo = motivo;
    }
}
