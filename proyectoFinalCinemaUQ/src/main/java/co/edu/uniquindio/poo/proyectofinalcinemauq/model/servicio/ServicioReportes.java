package co.edu.uniquindio.poo.proyectofinalcinemauq.model.servicio;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.ReporteInvalidoException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte.DatosReporte;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte.FormatoReporte;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte.Reporte;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;

// Exporta un reporte ya calculado al formato elegido.
// No sabe cómo se escribe un TXT o un PDF: se lo pide a la fábrica del formato.
public class ServicioReportes {

    public Path exportar(DatosReporte datos, FormatoReporte formato, Path archivo) throws ReporteInvalidoException {
        if (datos == null) {
            throw new ReporteInvalidoException("Primero genera el reporte que quieres exportar");
        }
        if (formato == null) {
            throw new ReporteInvalidoException("Selecciona el formato de exportación");
        }
        if (archivo == null) {
            throw new ReporteInvalidoException("Selecciona dónde guardar el archivo");
        }
        Path destino = conExtension(archivo, formato);
        Reporte reporte = formato.getFabrica().crearReporte(datos);
        try {
            reporte.exportar(destino);
        } catch (IOException | RuntimeException e) {
            throw new ReporteInvalidoException("No se pudo guardar el reporte en " + destino + ": " + e.getMessage());
        }
        return destino;
    }

    public String nombreSugerido(DatosReporte datos, FormatoReporte formato) {
        return "reporte-" + datos.getTipo().name().toLowerCase(Locale.ROOT) + "-"
                + datos.getDesde() + "_" + datos.getHasta() + "." + formato.getExtension();
    }

    // Si el usuario no escribió la extensión, se agrega
    private static Path conExtension(Path archivo, FormatoReporte formato) {
        String nombre = archivo.getFileName().toString();
        String extension = "." + formato.getExtension();
        if (nombre.toLowerCase(Locale.ROOT).endsWith(extension)) return archivo;
        return archivo.resolveSibling(nombre + extension);
    }
}
