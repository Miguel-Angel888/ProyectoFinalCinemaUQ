package co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Reporte {

    private final String titulo;
    private final LocalDate desde;
    private final LocalDate hasta;
    private final LocalDateTime fechaGeneracion;
    private final List<String> lineas;
    private final double total;

    protected Reporte(String titulo, LocalDate desde, LocalDate hasta,
                      List<String> lineas, double total) {
        this.titulo = titulo;
        this.desde = desde;
        this.hasta = hasta;
        this.fechaGeneracion = LocalDateTime.now();
        this.lineas = new ArrayList<>(lineas);
        this.total = total;
    }

    // Cada formato exporta a su manera
    public abstract void exportar(String ruta) throws IOException;

    // Contenido común (título, fecha, periodo, registros, totales)
    protected String construirTexto() {
        StringBuilder sb = new StringBuilder();
        sb.append(titulo).append("\n");
        sb.append("Periodo: ").append(desde).append(" al ").append(hasta).append("\n");
        sb.append("Generado: ").append(fechaGeneracion).append("\n\n");
        lineas.forEach(l -> sb.append(l).append("\n"));
        sb.append("\nRegistros encontrados: ").append(lineas.size());
        sb.append("\nTotal: ").append(total);
        return sb.toString();
    }
}