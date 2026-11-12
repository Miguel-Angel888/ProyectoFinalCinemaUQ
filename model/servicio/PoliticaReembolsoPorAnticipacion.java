package co.edu.uniquindio.poo.proyectofinalcinemauq.model.servicio;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException.CancelacionNoPermitidaException;

import java.time.Duration;
import java.time.LocalDateTime;

// Política para cuando el cliente cancela: depende del tiempo que falte para la función
//   más de 24 horas -> 100 %,  entre 2 y 24 horas -> 80 %,  menos de 2 horas -> no se permite
public class PoliticaReembolsoPorAnticipacion implements PoliticaReembolso {

    private static final Duration LIMITE_TOTAL = Duration.ofHours(24);
    private static final Duration LIMITE_MINIMO = Duration.ofHours(2);

    @Override
    public double calcularPorcentaje(LocalDateTime ahora, LocalDateTime fechaFuncion) throws CancelacionNoPermitidaException {
        Duration restante = Duration.between(ahora, fechaFuncion);

        if (restante.compareTo(LIMITE_TOTAL) > 0) return 100;
        if (restante.compareTo(LIMITE_MINIMO) >= 0) return 80;

        throw new CancelacionNoPermitidaException(
                "No se puede cancelar con menos de 2 horas de anticipación a la función");
    }
}
