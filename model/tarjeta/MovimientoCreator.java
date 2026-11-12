package co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta;
import java.util.UUID;

public abstract class MovimientoCreator {

    //factory method, cada creator concreto decide qué subclase instanciar
    protected abstract Movimiento crearMovimiento(String referencia, double cantidad, double saldoResultante, String descripcion);

    //la referencia se genera en un solo lugar
    public Movimiento registrar(double cantidad, double saldoResultante, String descripcion) {
        String referencia = "MOV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return crearMovimiento(referencia, cantidad, saldoResultante, descripcion);
    }

}
