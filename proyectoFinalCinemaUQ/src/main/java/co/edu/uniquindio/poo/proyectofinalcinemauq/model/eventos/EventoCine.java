package co.edu.uniquindio.poo.proyectofinalcinemauq.model.eventos;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Cliente;

import java.time.LocalDateTime;
public class EventoCine {
    private final TipoEvento tipo;
    private final Cliente cliente;
    private final String mensaje;
    private final LocalDateTime fecha;

    public EventoCine(TipoEvento tipo, Cliente cliente, String mensaje) {
        if (tipo == null || cliente == null || mensaje == null || mensaje.isBlank()) {
            throw new IllegalArgumentException("El evento requiere tipo, cliente y mensaje");
        }
        this.tipo = tipo;
        this.cliente = cliente;
        this.mensaje = mensaje;
        this.fecha = LocalDateTime.now();
    }

    public TipoEvento getTipo() { return tipo; }
    public Cliente getCliente() { return cliente; }
    public String getMensaje() { return mensaje; }
    public LocalDateTime getFecha() { return fecha; }
}
