package co.edu.uniquindio.poo.proyectofinalcinemauq.model.servicio;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.Escenario;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.EstadoFuncion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Funcion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.eventos.TipoEvento;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException.CompraInvalidaException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.MedioPago;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.Reembolso;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Compra;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.EstadoCompra;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

// Patrón Strategy (políticas de reembolso) + Observer (notificación al cliente)
class ServicioReembolsoTest {

    private Escenario escenario;
    private ServicioReembolso servicio;
    private MedioPago tarjeta;

    @BeforeEach
    void preparar() throws CinemaUQException {
        escenario = new Escenario();
        servicio = new ServicioReembolso(escenario.eventos);
        tarjeta = escenario.compraFacade.getMedioPago(escenario.cliente);
    }

    @Test
    void conMasDe24HorasSeDevuelveTodo() throws CinemaUQException {
        Funcion funcion = escenario.funcionEn(LocalDateTime.now().plusDays(3));
        Compra compra = escenario.comprar(funcion, 2);
        double saldoAntes = tarjeta.getSaldo();

        Reembolso reembolso = servicio.reembolsar(compra, tarjeta, LocalDateTime.now());

        assertEquals(100, reembolso.getPorcentaje());
        assertEquals(compra.getTotal(), reembolso.getCantidad(), 0.001);
        assertEquals(saldoAntes + compra.getTotal(), tarjeta.getSaldo(), 0.001);
        assertEquals(EstadoCompra.REEMBOLSADA, compra.getEstado());
        assertEquals(10, funcion.getCantidadDisponibles());   // se liberaron los asientos
        assertEquals(1, escenario.registro.contar(TipoEvento.COMPRA_CANCELADA));
    }

    @Test
    void entre2y24HorasSeDevuelveEl80PorCiento() throws CinemaUQException {
        Funcion funcion = escenario.funcionEn(LocalDateTime.now().plusHours(10));
        Compra compra = escenario.comprar(funcion, 1);

        Reembolso reembolso = servicio.reembolsar(compra, tarjeta, LocalDateTime.now());

        assertEquals(80, reembolso.getPorcentaje());
        assertEquals(compra.getTotal() * 0.8, reembolso.getCantidad(), 0.001);
        assertEquals("Reembolso parcial", reembolso.getDescripcion());
    }

    @Test
    void conMenosDe2HorasNoSePuedeCancelar() throws CinemaUQException {
        Funcion funcion = escenario.funcionEn(LocalDateTime.now().plusMinutes(90));
        Compra compra = escenario.comprar(funcion, 1);

        assertEquals(0, servicio.porcentajeAplicable(compra, LocalDateTime.now()));
        assertThrows(CompraInvalidaException.class, () -> servicio.reembolsar(compra, tarjeta, LocalDateTime.now()));
        assertEquals(EstadoCompra.PAGADA, compra.getEstado());
    }

    @Test
    void siElClienteGastoLosPuntosNoPuedeCancelar() throws CinemaUQException {
        Funcion funcion = escenario.funcionEn(LocalDateTime.now().plusDays(3));
        Compra compra = escenario.comprar(funcion, 2);
        escenario.cliente.getPuntos().redimir(compra.getPuntosGanados(), LocalDate.now());

        assertThrows(CompraInvalidaException.class, () -> servicio.reembolsar(compra, tarjeta, LocalDateTime.now()));
    }

    @Test
    void siElCineCancelaLaFuncionDevuelveTodoAunqueFalteMenosDe2Horas() throws CinemaUQException {
        Funcion funcion = escenario.funcionEn(LocalDateTime.now().plusMinutes(60));
        Compra compra = escenario.comprar(funcion, 3);
        // El cliente ya gastó sus puntos: cuando cancela el cine no se le exige tenerlos
        escenario.cliente.getPuntos().redimir(compra.getPuntosGanados(), LocalDate.now());
        funcion.setEstadoFuncion(EstadoFuncion.CANCELADA);

        Reembolso reembolso = servicio.reembolsarPorFuncionCancelada(compra, tarjeta, LocalDateTime.now());

        assertEquals(100, reembolso.getPorcentaje());
        assertEquals("Función cancelada por el cine", reembolso.getDescripcion());
        assertEquals(EstadoCompra.REEMBOLSADA, compra.getEstado());
        assertEquals(0, escenario.cliente.getPuntos().getPuntosDisponibles(LocalDate.now()));
        // El Observer dejó la notificación al cliente
        String ultima = escenario.cliente.getListaNotificaciones()
                .get(escenario.cliente.getListaNotificaciones().size() - 1).getMensaje();
        assertTrue(ultima.contains("canceló la función"), ultima);
    }

    @Test
    void noSeReembolsaPorCancelacionSiLaFuncionSigueProgramada() throws CinemaUQException {
        Funcion funcion = escenario.funcionEn(LocalDateTime.now().plusDays(1));
        Compra compra = escenario.comprar(funcion, 1);

        assertThrows(CompraInvalidaException.class,
                () -> servicio.reembolsarPorFuncionCancelada(compra, tarjeta, LocalDateTime.now()));
    }
}
