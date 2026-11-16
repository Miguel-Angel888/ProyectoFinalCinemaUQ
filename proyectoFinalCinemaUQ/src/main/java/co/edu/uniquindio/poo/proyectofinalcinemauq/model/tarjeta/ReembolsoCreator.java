package co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta;

import java.time.LocalDateTime;

public class ReembolsoCreator extends MovimientoCreator {

    @Override
    protected Movimiento crearMovimiento(String ref, double cant, double saldo, String desc, LocalDateTime fecha) {
        return new MovimientoReembolso(ref, cant, saldo, desc, fecha);
    }
}
