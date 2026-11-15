package co.edu.uniquindio.poo.proyectofinalcinemauq.model.reembolso_Strategy;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Funcion;

import java.time.LocalDateTime;
public class PoliticaReembolsoCompleto implements PoliticaReembolso {
    public static final long HORAS_MINIMAS = 24;

    @Override
    public boolean aplica(Funcion funcion, LocalDateTime ahora) {
        return horasRestantes(funcion, ahora) >= HORAS_MINIMAS;
    }

    @Override
    public double getPorcentaje() { return 100; }

    @Override
    public String getNombre() { return "Reembolso completo"; }
}
