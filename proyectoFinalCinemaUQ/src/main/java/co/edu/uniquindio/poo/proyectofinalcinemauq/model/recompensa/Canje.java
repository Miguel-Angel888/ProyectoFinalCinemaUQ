package co.edu.uniquindio.poo.proyectofinalcinemauq.model.recompensa;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Cliente;

import java.time.LocalDateTime;
import java.util.UUID;

// Comprobante de una redención: guarda qué se canjeó y cuántos puntos costó.
// Los puntos se descuentan en CuentaPuntos; esto es el historial y el "vale" del cliente.
public class Canje {

    private final String codigo;
    private final Cliente cliente;
    private final Recompensa recompensa;
    private final int puntosUsados;       // se guarda aparte porque el costo del catálogo puede cambiar
    private final LocalDateTime fecha;
    private EstadoCanje estado;

    public Canje(Cliente cliente, Recompensa recompensa) {
        this(cliente, recompensa, LocalDateTime.now());
    }

    // Permite registrar canjes históricos (para el reporte de puntos redimidos)
    public Canje(Cliente cliente, Recompensa recompensa, LocalDateTime fecha) {
        if (cliente == null || recompensa == null || fecha == null) {
            throw new IllegalArgumentException("El canje requiere un cliente, una recompensa y una fecha");
        }
        this.cliente = cliente;
        this.codigo = "CNJ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.recompensa = recompensa;
        this.puntosUsados = recompensa.getPuntosRequeridos();
        this.fecha = fecha;
        this.estado = EstadoCanje.DISPONIBLE;
    }

    public boolean estaDisponible() {
        return estado == EstadoCanje.DISPONIBLE;
    }

    public void marcarUsado() {
        if (estado == EstadoCanje.USADO) {
            throw new IllegalStateException("Este canje ya fue usado");
        }
        this.estado = EstadoCanje.USADO;
    }

    // Si se cancela la compra donde se usó el bono, el bono vuelve a estar disponible
    public void restaurar() {
        this.estado = EstadoCanje.DISPONIBLE;
    }

    public Cliente getCliente() { return cliente; }
    public String getCodigo() { return codigo; }
    public Recompensa getRecompensa() { return recompensa; }
    public int getPuntosUsados() { return puntosUsados; }
    public LocalDateTime getFecha() { return fecha; }
    public EstadoCanje getEstado() { return estado; }
}

