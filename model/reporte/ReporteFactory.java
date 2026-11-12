package co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte;

import java.time.LocalDate;
import java.util.List;

public interface ReporteFactory {
    Reporte crearReporte(String titulo, LocalDate desde, LocalDate hasta, List<String> lineas, double total);
}
