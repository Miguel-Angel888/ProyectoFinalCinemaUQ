package co.edu.uniquindio.poo.proyectofinalcinemauq.model.reembolso_Strategy;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Funcion;

import java.time.Duration;
import java.time.LocalDateTime;

public interface PoliticaReembolso {

    boolean aplica(Funcion funcion, LocalDateTime ahora);

    // Porcentaje del valor pagado que se devuelve (0 = no se puede cancelar)
    double getPorcentaje();

    String getNombre();

    // Si es true, no se puede cancelar cuando el cliente ya gastó los puntos que ganó con la compra.
    // Cuando cancela el cine no se le exige: se quitan solo los puntos que le queden.
    default boolean exigePuntosIntactos() {
        return true;
    }

    // Horas que faltan para la función (negativas si ya empezó)
    default long horasRestantes(Funcion funcion, LocalDateTime ahora) {
        return Duration.between(ahora, funcion.getFechaFuncion()).toHours();
    }
}






