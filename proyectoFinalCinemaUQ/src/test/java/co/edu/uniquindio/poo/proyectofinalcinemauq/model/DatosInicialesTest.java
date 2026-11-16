package co.edu.uniquindio.poo.proyectofinalcinemauq.model;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.EstadoFuncion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Funcion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Cliente;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Usuario;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.Movimiento;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Compra;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.EstadoCompra;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// El historial debe quedar coherente: saldos, fechas, estados y reembolsos
class DatosInicialesTest {

    private static Cine cine;

    @BeforeAll
    static void cargar() {
        cine = new Cine("CinemaUQ");
        DatosIniciales.cargar(cine);
    }

    private static Cliente cliente(String id) {
        for (Usuario u : cine.getUsuarios()) {
            if (u instanceof Cliente c && c.getId().equals(id)) return c;
        }
        throw new AssertionError("No existe el cliente " + id);
    }

    @Test
    void hayHistorialDeComprasEnElPasado() {
        assertTrue(cine.getCompras().size() > 100, "Se esperaban más compras históricas");
        LocalDateTime ahora = LocalDateTime.now();
        for (Compra c : cine.getCompras()) {
            assertTrue(c.fueCobrada());
            assertTrue(c.getFecha().isBefore(ahora));
            assertFalse(c.getFecha().isAfter(c.getListaEntradas().get(0).getFuncion().getFechaFuncion()));
        }
    }

    @Test
    void lasTarjetasNuncaQuedanEnNegativoYLosMovimientosEstanEnOrden() {
        for (Usuario u : cine.getUsuarios()) {
            if (!(u instanceof Cliente c)) continue;
            List<Movimiento> movimientos = c.getTarjetaVirtual().getListaMovimientos();
            for (int i = 0; i < movimientos.size(); i++) {
                assertTrue(movimientos.get(i).getSaldoResultante() >= 0);
                if (i > 0) {
                    assertFalse(movimientos.get(i).getFechaMovimiento().isBefore(movimientos.get(i - 1).getFechaMovimiento()),
                            "Movimientos fuera de orden en la tarjeta de " + c.getNombre());
                }
            }
        }
    }

    @Test
    void luisConservaPocoSaldoParaProbarElRechazoDelPago() {
        assertEquals(10000, cliente("1002").getTarjetaVirtual().getSaldo(), 0.001);
        assertTrue(cliente("1002").getListaCompras().isEmpty());
    }

    @Test
    void anaTieneSuRecargaDeHoyYPuntosVigentes() {
        Cliente ana = cliente("1001");
        assertTrue(ana.getTarjetaVirtual().getSaldo() >= 150000);
        assertTrue(ana.getPuntos().getPuntosDisponibles(LocalDate.now()) > 0);
    }

    @Test
    void hayUnaFuncionCanceladaPorElCineConTodoReembolsado() {
        Funcion cancelada = null;
        for (Funcion f : cine.getFunciones()) {
            if (f.getEstadoFuncion() == EstadoFuncion.CANCELADA) cancelada = f;
        }
        assertNotNull(cancelada);
        assertEquals(cancelada.getSala().getCapacidad(), cancelada.getCantidadDisponibles());
        int reembolsadasPorElCine = 0;
        for (Compra c : cine.getCompras()) {
            if (c.getListaEntradas().get(0).getFuncion() != cancelada) continue;
            // Ninguna compra queda pagada; las que el cliente ya había cancelado conservan su porcentaje
            assertEquals(EstadoCompra.REEMBOLSADA, c.getEstado());
            if (c.getReembolso().getDescripcion().equals("Función cancelada por el cine")) {
                assertEquals(100, c.getReembolso().getPorcentaje());
                reembolsadasPorElCine++;
            }
        }
        assertTrue(reembolsadasPorElCine > 0);
    }

    @Test
    void lasFuncionesHistoricasNoSeCruzanEnLaMismaSala() {
        List<Funcion> historicas = new java.util.ArrayList<>();
        for (Funcion f : cine.getFunciones()) {
            if (f.getReferencia().startsWith("H")) historicas.add(f);
        }
        assertFalse(historicas.isEmpty());
        for (int i = 0; i < historicas.size(); i++) {
            for (int j = i + 1; j < historicas.size(); j++) {
                Funcion a = historicas.get(i);
                Funcion b = historicas.get(j);
                if (a.getSala() == b.getSala()) {
                    assertFalse(a.seCruzaCon(b), a.getReferencia() + " se cruza con " + b.getReferencia());
                }
            }
        }
    }

    @Test
    void lasFuncionesPasadasNoAparecenEnLaCartelera() {
        for (Funcion f : cine.getFunciones()) {
            if (f.getFechaFuncion().isBefore(LocalDateTime.now())) assertFalse(f.estaProgramada());
        }
    }
}
