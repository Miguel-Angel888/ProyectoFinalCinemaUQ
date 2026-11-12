package co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta;

public class MovimientoRecarga extends Movimiento {

    public MovimientoRecarga(String referencia, double cantidad, double saldoResultante, String descripcion) {
        super(referencia, cantidad, saldoResultante, descripcion);
    }
    @Override public TipoMovimiento getTipoMovimiento() { return TipoMovimiento.RECARGA; }
    @Override public boolean esIngreso() { return true; }
}
