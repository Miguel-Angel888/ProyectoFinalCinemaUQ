package co.edu.uniquindio.poo.proyectofinalcinemauq.model.servicio;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.Cine;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.DatosIniciales;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.ReporteInvalidoException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte.DatosReporte;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte.FormatoReporte;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte.ReportePdf;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte.ReporteTxt;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte.TipoReporte;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

// Exportación a TXT y PDF usando las fábricas de reportes
class ServicioReportesTest {

    private static ServicioEstadisticas estadisticas;
    private final ServicioReportes servicio = new ServicioReportes();

    @TempDir
    Path carpeta;

    @BeforeAll
    static void cargar() {
        Cine cine = new Cine("CinemaUQ");
        DatosIniciales.cargar(cine);
        estadisticas = new ServicioEstadisticas(cine);
    }

    private DatosReporte reporte(TipoReporte tipo) throws ReporteInvalidoException {
        return estadisticas.generar(tipo, LocalDate.now().minusDays(29), LocalDate.now());
    }

    @Test
    void elTxtIncluyeTituloFechaPeriodoRegistrosYTotales() throws Exception {
        DatosReporte datos = reporte(TipoReporte.VENTAS);
        Path archivo = servicio.exportar(datos, FormatoReporte.TXT, carpeta.resolve("ventas.txt"));

        String texto = Files.readString(archivo, StandardCharsets.UTF_8);
        assertTrue(texto.contains("Ventas por periodo"));
        assertTrue(texto.contains("Fecha de generación:"));
        assertTrue(texto.contains("Periodo consultado:"));
        assertTrue(texto.contains("Registros encontrados: " + datos.getRegistros()));
        assertTrue(texto.contains("TOTALES"));
        assertTrue(texto.contains("Total vendido:"));
    }

    @ParameterizedTest
    @EnumSource(TipoReporte.class)
    void todosLosReportesSeExportanAPdf(TipoReporte tipo) throws Exception {
        Path archivo = servicio.exportar(reporte(tipo), FormatoReporte.PDF, carpeta.resolve(tipo.name() + ".pdf"));

        assertTrue(Files.size(archivo) > 0);
        assertEquals("%PDF", new String(Files.readAllBytes(archivo), 0, 4, StandardCharsets.US_ASCII));
    }

    @Test
    void siFaltaLaExtensionSeAgrega() throws Exception {
        Path archivo = servicio.exportar(reporte(TipoReporte.PUNTOS), FormatoReporte.TXT, carpeta.resolve("puntos"));
        assertEquals("puntos.txt", archivo.getFileName().toString());
        assertTrue(Files.exists(archivo));
    }

    @Test
    void cadaFormatoUsaSuPropiaFabrica() throws ReporteInvalidoException {
        DatosReporte datos = reporte(TipoReporte.RECARGAS);
        assertInstanceOf(ReporteTxt.class, FormatoReporte.TXT.getFabrica().crearReporte(datos));
        assertInstanceOf(ReportePdf.class, FormatoReporte.PDF.getFabrica().crearReporte(datos));
    }

    @Test
    void sinDatosOSinFormatoNoSeExporta() {
        assertThrows(ReporteInvalidoException.class, () -> servicio.exportar(null, FormatoReporte.TXT, carpeta.resolve("x.txt")));
        assertThrows(ReporteInvalidoException.class, () -> servicio.exportar(reporte(TipoReporte.VENTAS), null, carpeta.resolve("x.txt")));
    }

    @Test
    void unaRutaInvalidaSeInformaComoReporteInvalido() throws IOException, ReporteInvalidoException {
        DatosReporte datos = reporte(TipoReporte.VENTAS);
        Path noEsCarpeta = Files.writeString(carpeta.resolve("archivo"), "x");
        assertThrows(ReporteInvalidoException.class,
                () -> servicio.exportar(datos, FormatoReporte.TXT, noEsCarpeta.resolve("reporte.txt")));
    }
}
