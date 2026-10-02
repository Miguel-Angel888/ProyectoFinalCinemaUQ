package co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Cliente;

import java.time.LocalDateTime;

public class Notificacion {

    //Atributos
    private String referencia;
    private Cliente cliente;
    private String mensaje;
    private LocalDateTime fecha;
    private boolean leida;

    //Constructor


    public Notificacion(String referencia, Cliente cliente, String mensaje, LocalDateTime fecha, boolean leida) {
        this.referencia = referencia;
        this.cliente = cliente;
        this.mensaje = mensaje;
        this.fecha = fecha;
        this.leida = leida;
    }

    //Getters y setters

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public boolean isLeida() {
        return leida;
    }

    public void setLeida(boolean leida) {
        this.leida = leida;
    }
}

