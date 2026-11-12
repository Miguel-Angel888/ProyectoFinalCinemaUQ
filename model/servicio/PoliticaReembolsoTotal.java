package co.edu.uniquindio.poo.proyectofinalcinemauq.model.servicio;

import java.time.LocalDateTime;

// Política para cuando el cine cancela la función: siempre se devuelve todo
public class PoliticaReembolsoTotal implements PoliticaReembolso {

    @Override
    public double calcularPorcentaje(LocalDateTime ahora, LocalDateTime fechaFuncion) {
        return 100;
    }
}
