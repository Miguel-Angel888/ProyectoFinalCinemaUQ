package co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions;

public class CinemaUQException extends Exception {

    public CinemaUQException(String mensaje) {
        super(mensaje);
    }

    public static class MontoInvalidoException extends CinemaUQException {
        public MontoInvalidoException(String mensaje) { super(mensaje); }
    }

    public static class TarjetaInactivaException extends CinemaUQException {
        public TarjetaInactivaException(String mensaje) { super(mensaje); }
    }

    public static class SaldoInsuficienteException extends CinemaUQException {
        public SaldoInsuficienteException(String mensaje) { super(mensaje); }
    }

    public static class CompraInvalidaException extends CinemaUQException {
        public CompraInvalidaException(String mensaje) { super(mensaje); }
    }

    public static class SolicitudInvalidaException extends CinemaUQException {
        public SolicitudInvalidaException(String mensaje) { super(mensaje); }
    }

    public static class PuntosInsuficientesException extends CinemaUQException {
        public PuntosInsuficientesException(String mensaje) { super(mensaje); }
    }
}
