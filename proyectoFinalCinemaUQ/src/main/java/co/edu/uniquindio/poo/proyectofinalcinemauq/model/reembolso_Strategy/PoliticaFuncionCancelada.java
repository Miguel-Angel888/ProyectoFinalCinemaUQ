package co.edu.uniquindio.poo.proyectofinalcinemauq.model.reembolso_Strategy;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.EstadoFuncion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Funcion;

import java.time.LocalDateTime;
public class PoliticaFuncionCancelada implements PoliticaReembolso {
    @Override
    public boolean aplica(Funcion funcion, LocalDateTime ahora) {
        return funcion.getEstadoFuncion() == EstadoFuncion.CANCELADA;
    }

    @Override
    public double getPorcentaje() { return 100; }

    @Override
    public String getNombre() { return "Función cancelada por el cine"; }

    @Override
    public boolean exigePuntosIntactos() { return false; }
}
