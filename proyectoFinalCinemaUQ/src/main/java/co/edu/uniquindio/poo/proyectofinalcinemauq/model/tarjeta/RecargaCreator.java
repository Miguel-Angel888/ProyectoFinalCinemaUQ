package co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta;

import java.time.LocalDateTime;

public class RecargaCreator extends MovimientoCreator {

    @Override
    protected Movimiento crearMovimiento(String ref, double cant, double saldo, String desc, LocalDateTime fecha) {
        return new MovimientoRecarga(ref, cant, saldo, desc, fecha);
    }
}
