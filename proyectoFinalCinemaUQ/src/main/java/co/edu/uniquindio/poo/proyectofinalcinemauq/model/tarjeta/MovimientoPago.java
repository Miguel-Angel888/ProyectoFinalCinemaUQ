package co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta;

import java.time.LocalDateTime;

public class MovimientoPago extends Movimiento {

    public MovimientoPago(String referencia, double cantidad, double saldoResultante,
                          String descripcion, LocalDateTime fecha) {
        super(referencia, cantidad, saldoResultante, descripcion, fecha);
    }
    @Override public TipoMovimiento getTipoMovimiento() { return TipoMovimiento.PAGO; }
    @Override public boolean esIngreso() { return false; }
}
