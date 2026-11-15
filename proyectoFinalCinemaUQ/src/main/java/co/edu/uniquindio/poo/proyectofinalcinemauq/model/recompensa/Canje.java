package co.edu.uniquindio.poo.proyectofinalcinemauq.model.recompensa;

import java.time.LocalDateTime;
import java.util.UUID;

public class Canje {

    private final String codigo;
    private final Recompensa recompensa;
    private final int puntosUsados;       // se guarda aparte porque el costo del catálogo puede cambiar
    private final LocalDateTime fecha;
    private EstadoCanje estado;

    public Canje(Recompensa recompensa) {
        if (recompensa == null) {
            throw new IllegalArgumentException("El canje requiere una recompensa");
        }
        this.codigo = "CNJ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.recompensa = recompensa;
        this.puntosUsados = recompensa.getPuntosRequeridos();
        this.fecha = LocalDateTime.now();
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

    public String getCodigo() { return codigo; }
    public Recompensa getRecompensa() { return recompensa; }
    public int getPuntosUsados() { return puntosUsados; }
    public LocalDateTime getFecha() { return fecha; }
    public EstadoCanje getEstado() { return estado; }
}
