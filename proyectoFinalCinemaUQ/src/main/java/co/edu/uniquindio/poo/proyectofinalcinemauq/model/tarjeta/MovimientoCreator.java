package co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta;

import java.time.LocalDateTime;
import java.util.UUID;

// PATRÓN FACTORY METHOD - Creador
public abstract class MovimientoCreator {

    //factory method, cada creator concreto decide qué subclase instanciar
    protected abstract Movimiento crearMovimiento(String referencia, double cantidad, double saldoResultante,
                                                  String descripcion, LocalDateTime fecha);

    //movimiento con la fecha actual
    public Movimiento registrar(double cantidad, double saldoResultante, String descripcion) {
        return registrar(cantidad, saldoResultante, descripcion, LocalDateTime.now());
    }

    //la referencia se genera en un solo lugar
    public Movimiento registrar(double cantidad, double saldoResultante, String descripcion, LocalDateTime fecha) {
        String referencia = "MOV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return crearMovimiento(referencia, cantidad, saldoResultante, descripcion, fecha);
    }

}
