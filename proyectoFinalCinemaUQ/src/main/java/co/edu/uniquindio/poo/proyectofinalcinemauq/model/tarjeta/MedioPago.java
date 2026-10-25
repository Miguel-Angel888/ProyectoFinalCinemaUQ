package co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException;

// PATRÓN PROXY - Sujeto
// Lo implementan la tarjeta real y su proxy, así quien paga no sabe con cuál habla.
public interface MedioPago {

    String getNumeroTarjeta();

    double getSaldo();

    boolean puedePagar(double monto);

    void debitar(double monto, String descripcion) throws CinemaUQException;

    void acreditar(double monto, String descripcion) throws CinemaUQException;
}
