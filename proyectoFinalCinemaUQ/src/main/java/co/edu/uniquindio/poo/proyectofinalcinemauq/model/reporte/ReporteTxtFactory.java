package co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte;

import java.time.LocalDate;
import java.util.List;

public class ReporteTxtFactory implements ReporteFactory {

    @Override
    public Reporte crearReporte(String titulo, LocalDate desde, LocalDate hasta, List<String> lineas, double total) {
        return new ReporteTxt(titulo, desde, hasta, lineas, total);
    }
}
