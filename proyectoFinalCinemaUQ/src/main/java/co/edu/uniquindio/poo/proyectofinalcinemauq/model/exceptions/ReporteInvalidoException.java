package co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions;

// Se lanza cuando no se puede generar o exportar un reporte
// (periodo inválido, tipo sin seleccionar, archivo que no se pudo escribir, etc.)
public class ReporteInvalidoException extends CinemaUQException {

    public ReporteInvalidoException(String mensaje) {
        super(mensaje);
    }
}
