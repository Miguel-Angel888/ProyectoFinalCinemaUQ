package co.edu.uniquindio.poo.proyectofinalcinemauq.model.servicio;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.Cine;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.DatosIniciales;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.Escenario;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Funcion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.ReporteInvalidoException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte.DatosReporte;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte.SerieGrafico;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte.TipoGrafico;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte.TipoReporte;
import co.edu.uniquindio.poo.proyectofinalcinemauq.util.Formato;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServicioEstadisticasTest {

    private static ServicioEstadisticas conHistorial;
    private static final LocalDate HOY = LocalDate.now();

    @BeforeAll
    static void cargar() {
        Cine cine = new Cine("CinemaUQ");
        DatosIniciales.cargar(cine);
        conHistorial = new ServicioEstadisticas(cine);
    }

    @ParameterizedTest
    @EnumSource(TipoReporte.class)
    void cadaReporteTieneRegistrosTotalesYSerieEnLosUltimos90Dias(TipoReporte tipo) throws ReporteInvalidoException {
        DatosReporte datos = conHistorial.generar(tipo, HOY.minusDays(89), HOY);

        assertEquals(tipo, datos.getTipo());
        assertTrue(datos.getRegistros() > 0, "Sin registros para " + tipo);
        assertFalse(datos.getTotales().isEmpty());
        assertFalse(datos.getSeries().isEmpty());
        for (List<String> fila : datos.getFilas()) assertEquals(datos.getColumnas().size(), fila.size());
    }

    @Test
    void hayGraficosDeBarrasPastelYLineas() {
        List<TipoGrafico> usados = new java.util.ArrayList<>();
        for (TipoReporte t : TipoReporte.values()) usados.add(t.getTipoGrafico());
        assertTrue(usados.containsAll(List.of(TipoGrafico.BARRAS, TipoGrafico.PASTEL, TipoGrafico.LINEAS)));
    }

    @Test
    void elPeriodoInvalidoSeRechaza() {
        assertThrows(ReporteInvalidoException.class, () -> conHistorial.generar(TipoReporte.VENTAS, HOY, HOY.minusDays(1)));
        assertThrows(ReporteInvalidoException.class, () -> conHistorial.generar(TipoReporte.VENTAS, null, HOY));
        assertThrows(ReporteInvalidoException.class, () -> conHistorial.generar(null, HOY, HOY));
    }

    @Test
    void lasVentasCuadranConLasComprasDelPeriodo() throws CinemaUQException {
        Escenario escenario = new Escenario();
        Funcion funcion = escenario.funcionEn(LocalDateTime.now().plusDays(2));
        escenario.comprar(funcion, 2);   // 2 x $20.000
        escenario.comprar(funcion, 1);   // 1 x $20.000

        DatosReporte ventas = new ServicioEstadisticas(escenario.cine).generar(TipoReporte.VENTAS, HOY, HOY);

        assertEquals(2, ventas.getRegistros());
        assertEquals(Formato.moneda(60000), ventas.getTotales().get("Total vendido"));
        assertEquals("3", ventas.getTotales().get("Entradas vendidas"));
        SerieGrafico serie = ventas.getSeries().get(0);
        assertEquals(60000, serie.getTotal(), 0.001);
    }

    @Test
    void laOcupacionCuentaLosAsientosVendidos() throws CinemaUQException {
        Escenario escenario = new Escenario();
        Funcion funcion = escenario.funcionEn(LocalDateTime.now().plusDays(1));
        escenario.comprar(funcion, 4);   // 4 de 10 asientos

        DatosReporte ocupacion = new ServicioEstadisticas(escenario.cine)
                .generar(TipoReporte.OCUPACION, HOY, HOY.plusDays(2));

        assertEquals(Formato.porcentaje(40), ocupacion.getTotales().get("Ocupación promedio"));
        assertEquals(40, ocupacion.getSeries().get(0).valores().get("Sala 1"), 0.001);
    }

    @Test
    void unPeriodoSinMovimientosDevuelveCeroRegistros() throws CinemaUQException {
        DatosReporte datos = new ServicioEstadisticas(new Escenario().cine)
                .generar(TipoReporte.RECARGAS, HOY.minusYears(3), HOY.minusYears(2));
        assertEquals(0, datos.getRegistros());
        assertEquals(Formato.moneda(0), datos.getTotales().get("Total recargado"));
    }
}
