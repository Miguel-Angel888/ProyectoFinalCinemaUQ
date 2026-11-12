package co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Cliente;

import java.time.LocalDateTime;
import java.util.UUID;

public class Notificacion {

    private final String referencia;
    private final Cliente cliente;
    private final String mensaje;
    private final LocalDateTime fecha;
    private boolean leida;

    public Notificacion(Cliente cliente, String mensaje) {
        if (cliente == null) {
            throw new IllegalArgumentException("La notificación debe tener un cliente");
        }
        if (mensaje == null || mensaje.isBlank()) {
            throw new IllegalArgumentException("La notificación debe tener un mensaje");
        }
        this.referencia = "NOT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.cliente = cliente;
        this.mensaje = mensaje;
        this.fecha = LocalDateTime.now();
        this.leida = false;
    }

    public void marcarComoLeida() {
        this.leida = true;
    }

    public String getReferencia() { return referencia; }
    public Cliente getCliente() { return cliente; }
    public String getMensaje() { return mensaje; }
    public LocalDateTime getFecha() { return fecha; }
    public boolean isLeida() { return leida; }
}
