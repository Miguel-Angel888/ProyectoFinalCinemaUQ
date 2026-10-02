package co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta;

public class PagoCreator extends MovimientoCreator {

    @Override
    protected Movimiento crearMovimiento(String ref, double cant, double saldo, String desc) {
        return new MovimientoPago(ref, cant, saldo, desc);
    }
}
