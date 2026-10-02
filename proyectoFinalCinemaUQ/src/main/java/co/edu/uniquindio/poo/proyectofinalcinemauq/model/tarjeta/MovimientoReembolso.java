package co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta;

public class MovimientoReembolso extends Movimiento{

    public MovimientoReembolso(String referencia, double cantidad, double saldoResultante, String descripcion) {
        super(referencia, cantidad, saldoResultante, descripcion);
    }
    @Override public TipoMovimiento getTipoMovimiento() { return TipoMovimiento.REEMBOLSO; }
    @Override public boolean esIngreso() { return true; }
}
