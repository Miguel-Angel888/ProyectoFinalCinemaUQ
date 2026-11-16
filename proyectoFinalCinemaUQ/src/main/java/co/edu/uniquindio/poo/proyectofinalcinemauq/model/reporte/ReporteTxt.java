package co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ReporteTxt extends Reporte {

    public ReporteTxt(DatosReporte datos) {
        super(datos);
    }

    @Override
    public void exportar(Path archivo) throws IOException {
        Files.writeString(archivo, construirTexto(), StandardCharsets.UTF_8);
    }
}
