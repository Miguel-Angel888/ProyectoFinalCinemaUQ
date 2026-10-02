package co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta;

public class RecargaCreator extends MovimientoCreator {

    @Override
    protected Movimiento crearMovimiento(String ref, double cant, double saldo, String desc) {
        return new MovimientoRecarga(ref, cant, saldo, desc);
    }
}