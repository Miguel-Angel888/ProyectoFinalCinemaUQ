package co.edu.uniquindio.poo.proyectofinalcinemauq.model.servicio;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException.CancelacionNoPermitidaException;

import java.time.LocalDateTime;

// Strategy: cada política decide qué porcentaje del valor pagado se devuelve
public interface PoliticaReembolso {

    // retorna el porcentaje a devolver (0 a 100) o lanza excepción si no se permite cancelar
    double calcularPorcentaje(LocalDateTime ahora, LocalDateTime fechaFuncion) throws CancelacionNoPermitidaException;
}
