package co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta;

public class MovimientoPago extends Movimiento {

    public MovimientoPago(String referencia, double cantidad, double saldoResultante, String descripcion) {
        super(referencia, cantidad, saldoResultante, descripcion);
    }
    @Override public TipoMovimiento getTipoMovimiento() { return TipoMovimiento.PAGO; }
    @Override public boolean esIngreso() { return false; }
}
