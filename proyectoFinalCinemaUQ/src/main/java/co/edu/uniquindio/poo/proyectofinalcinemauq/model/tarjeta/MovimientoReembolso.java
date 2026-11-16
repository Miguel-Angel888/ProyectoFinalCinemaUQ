package co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta;

import java.time.LocalDateTime;

public class MovimientoReembolso extends Movimiento {

    public MovimientoReembolso(String referencia, double cantidad, double saldoResultante,
                               String descripcion, LocalDateTime fecha) {
        super(referencia, cantidad, saldoResultante, descripcion, fecha);
    }
    @Override public TipoMovimiento getTipoMovimiento() { return TipoMovimiento.REEMBOLSO; }
    @Override public boolean esIngreso() { return true; }
}
