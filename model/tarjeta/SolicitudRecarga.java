package co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Cliente;

import java.time.LocalDateTime;
import java.util.UUID;

public class SolicitudRecarga {

    private final String referencia;
    private final Cliente cliente;
    private final double cantidad;
    private final LocalDateTime fechaSolicitud;
    private EstadoSolicitud estadoSolicitud;

    public SolicitudRecarga(Cliente cliente, double cantidad) throws CinemaUQException.MontoInvalidoException {
        if (cliente == null) {
            throw new IllegalArgumentException("La solicitud debe tener un cliente");
        }
        if (cantidad <= 0) {
            throw new CinemaUQException.MontoInvalidoException("El monto de la recarga debe ser mayor a cero");
        }
        this.referencia = "SOL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.cliente = cliente;
        this.cantidad = cantidad;
        this.fechaSolicitud = LocalDateTime.now();
        this.estadoSolicitud = EstadoSolicitud.PENDIENTE;
    }

    public void aprobar() throws CinemaUQException.SolicitudInvalidaException {
        validarPendiente();
        this.estadoSolicitud = EstadoSolicitud.APROBADA;
    }

    public void rechazar() throws CinemaUQException.SolicitudInvalidaException {
        validarPendiente();
        this.estadoSolicitud = EstadoSolicitud.RECHAZADA;
    }

    private void validarPendiente() throws CinemaUQException.SolicitudInvalidaException {
        if (estadoSolicitud != EstadoSolicitud.PENDIENTE) {
            throw new CinemaUQException.SolicitudInvalidaException("La solicitud ya fue procesada");
        }
    }

    public String getReferencia() { return referencia; }
    public Cliente getCliente() { return cliente; }
    public double getCantidad() { return cantidad; }
    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public EstadoSolicitud getEstadoSolicitud() { return estadoSolicitud; }
}