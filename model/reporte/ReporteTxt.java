package co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class ReporteTxt extends Reporte {

    public ReporteTxt(String titulo, LocalDate desde, LocalDate hasta,
                      List<String> lineas, double total) {
        super(titulo, desde, hasta, lineas, total);
    }

    @Override
    public void exportar(String ruta) throws IOException {
        Files.writeString(Path.of(ruta), construirTexto());
    }
}
