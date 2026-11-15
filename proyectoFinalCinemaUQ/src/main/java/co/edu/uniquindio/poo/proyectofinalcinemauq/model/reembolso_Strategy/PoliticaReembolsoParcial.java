package co.edu.uniquindio.poo.proyectofinalcinemauq.model.reembolso_Strategy;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Funcion;

import java.time.LocalDateTime;
public class PoliticaReembolsoParcial implements PoliticaReembolso {
public static final long HORAS_MINIMAS = 2;

@Override
public boolean aplica(Funcion funcion, LocalDateTime ahora) {
    long horas = horasRestantes(funcion, ahora);
    return horas >= HORAS_MINIMAS && horas < PoliticaReembolsoCompleto.HORAS_MINIMAS;
}

@Override
public double getPorcentaje() { return 80; }

@Override
public String getNombre() { return "Reembolso parcial"; }
}
