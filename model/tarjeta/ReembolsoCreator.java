package co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta;

public class ReembolsoCreator extends MovimientoCreator {

    @Override
    protected Movimiento crearMovimiento(String ref, double cant, double saldo, String desc) {
        return new MovimientoReembolso(ref, cant, saldo, desc);
    }
}
