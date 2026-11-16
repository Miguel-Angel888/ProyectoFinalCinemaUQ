package co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.Escenario;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Funcion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException.CompraInvalidaException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.Pago;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.Reembolso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Patrón State: cada estado permite solo las transiciones válidas
class CompraEstadoTest {

    private Escenario escenario;
    private Funcion funcion;

    @BeforeEach
    void preparar() throws CinemaUQException {
        escenario = new Escenario();
        funcion = escenario.funcionEn(LocalDateTime.now().plusDays(3));
    }

    private Compra compraPendiente() throws CinemaUQException {
        return escenario.compraFacade.cotizar(escenario.cliente, funcion,
                List.of(escenario.sala.buscarAsiento("A1")), TipoEntrada.GENERAL, List.of(), List.of());
    }

    private Reembolso reembolsoDe(Compra compra) {
        return new Reembolso(compra, "REE-1", 100, compra.getTotal(), LocalDateTime.now(), "Prueba");
    }

    @Test
    void unaCompraNuevaEstaPendienteYNoAdmiteReembolso() throws CinemaUQException {
        Compra compra = compraPendiente();
        assertEquals(EstadoCompra.PENDIENTE, compra.getEstado());
        assertFalse(compra.admiteReembolso());
        assertFalse(compra.fueCobrada());
    }

    @Test
    void pendientePasaAPagadaAlRegistrarElPago() throws CinemaUQException {
        Compra compra = compraPendiente();
        compra.registrarPago(new Pago(compra, escenario.cliente.getTarjetaVirtual()), 20);

        assertEquals(EstadoCompra.PAGADA, compra.getEstado());
        assertTrue(compra.admiteReembolso());
        assertTrue(compra.fueCobrada());
        assertEquals(20, compra.getPuntosGanados());
    }

    @Test
    void pagadaPasaAReembolsadaYNoSePuedeReembolsarDosVeces() throws CinemaUQException {
        Compra compra = compraPendiente();
        compra.registrarPago(new Pago(compra, escenario.cliente.getTarjetaVirtual()), 0);
        compra.registrarReembolso(reembolsoDe(compra));

        assertEquals(EstadoCompra.REEMBOLSADA, compra.getEstado());
        assertTrue(compra.fueCobrada());
        assertFalse(compra.admiteReembolso());
        CompraInvalidaException e = assertThrows(CompraInvalidaException.class,
                () -> compra.registrarReembolso(reembolsoDe(compra)));
        assertEquals("Esta compra ya fue reembolsada", e.getMessage());
    }

    @Test
    void noSePuedePagarDosVeces() throws CinemaUQException {
        Compra compra = compraPendiente();
        Pago pago = new Pago(compra, escenario.cliente.getTarjetaVirtual());
        compra.registrarPago(pago, 0);

        assertThrows(CompraInvalidaException.class, () -> compra.registrarPago(pago, 0));
        assertSame(pago, compra.getPago());
    }

    @Test
    void unaCompraPendienteNoSePuedeReembolsar() throws CinemaUQException {
        Compra compra = compraPendiente();
        assertThrows(CompraInvalidaException.class, () -> compra.registrarReembolso(reembolsoDe(compra)));
        assertNull(compra.getReembolso());
        assertEquals(EstadoCompra.PENDIENTE, compra.getEstado());
    }

    @Test
    void pendienteSePuedeDescartarYYaNoSePuedePagar() throws CinemaUQException {
        Compra compra = compraPendiente();
        compra.cancelar();

        assertEquals(EstadoCompra.CANCELADA, compra.getEstado());
        assertFalse(compra.fueCobrada());
        assertThrows(CompraInvalidaException.class,
                () -> compra.registrarPago(new Pago(compra, escenario.cliente.getTarjetaVirtual()), 0));
        assertThrows(CompraInvalidaException.class, compra::cancelar);
    }

    @Test
    void unaCompraPagadaNoSePuedeDescartar() throws CinemaUQException {
        Compra compra = escenario.comprar(funcion, 1);
        assertThrows(CompraInvalidaException.class, compra::cancelar);
        assertEquals(EstadoCompra.PAGADA, compra.getEstado());
    }

    @Test
    void elBuilderRespetaLaFechaIndicada() throws CinemaUQException {
        LocalDateTime fecha = LocalDateTime.now().minusDays(10);
        Compra compra = new Compra.CompraBuilder()
                .conCliente(escenario.cliente)
                .conFecha(fecha)
                .agregarEntrada(new Entrada("E1", funcion, escenario.sala.buscarAsiento("A2"), TipoEntrada.GENERAL, 20000))
                .build();
        assertEquals(fecha, compra.getFecha());
    }
}
