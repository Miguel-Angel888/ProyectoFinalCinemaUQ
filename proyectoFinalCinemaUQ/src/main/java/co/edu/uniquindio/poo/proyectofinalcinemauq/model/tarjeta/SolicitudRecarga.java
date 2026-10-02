package co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Cliente;

import java.time.LocalDateTime;

public class SolicitudRecarga {

    //Atributos
    private String referencia;
    private Cliente cliente;
    private double cantidad;
    private LocalDateTime fechaSolicitud;
    private EstadoSolicitud estadoSolicitud;

    //Constructor


    public SolicitudRecarga(String referencia, Cliente cliente, double cantidad,
                            LocalDateTime fechaSolicitud, EstadoSolicitud estadoSolicitud) {
        this.referencia = referencia;
        this.cliente = cliente;
        this.cantidad = cantidad;
        this.fechaSolicitud = fechaSolicitud;
        this.estadoSolicitud = estadoSolicitud;
    }

    //Getters y setters


    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public EstadoSolicitud getEstadoSolicitud() {
        return estadoSolicitud;
    }

    public void setEstadoSolicitud(EstadoSolicitud estadoSolicitud) {
        this.estadoSolicitud = estadoSolicitud;
    }
}
