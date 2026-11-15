package co.edu.uniquindio.poo.proyectofinalcinemauq.model.reembolso_Strategy;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Funcion;

import java.time.Duration;
import java.time.LocalDateTime;

public interface PoliticaReembolso {

    boolean aplica(Funcion funcion, LocalDateTime ahora);

    // Porcentaje del valor pagado que se devuelve (0 = no se puede cancelar)
    double getPorcentaje();

    String getNombre();

    // Horas que faltan para la función (negativas si ya empezó)
    default long horasRestantes(Funcion funcion, LocalDateTime ahora) {
        return Duration.between(ahora, funcion.getFechaFuncion()).toHours();
    }
}






