package co.edu.uniquindio.poo.proyectofinalcinemauq.model.eventos;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.Escenario;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.recompensa.Recompensa;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.recompensa.ReglaCostoPorValor;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.servicio.ServicioCanje;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Patrón Observer: los servicios publican y los observadores reaccionan sin conocerse
class PublicadorEventosTest {

    @Test
    void todosLosObservadoresSuscritosRecibenElEvento() throws CinemaUQException {
        Escenario escenario = new Escenario();
        List<EventoCine> recibidos = new ArrayList<>();
        escenario.eventos.suscribir(recibidos::add);

        escenario.eventos.publicar(new EventoCine(TipoEvento.RECARGA_APROBADA, escenario.cliente, "Recarga aprobada"));

        assertEquals(1, recibidos.size());
        assertEquals(1, escenario.registro.contar(TipoEvento.RECARGA_APROBADA));
        assertEquals("Recarga aprobada", escenario.cliente.getListaNotificaciones().get(0).getMensaje());
    }

    @Test
    void unObservadorDesuscritoYaNoRecibeEventos() throws CinemaUQException {
        Escenario escenario = new Escenario();
        List<EventoCine> recibidos = new ArrayList<>();
        ObservadorCine observador = recibidos::add;
        escenario.eventos.suscribir(observador);
        escenario.eventos.desuscribir(observador);

        escenario.eventos.publicar(new EventoCine(TipoEvento.CANJE_REALIZADO, escenario.cliente, "Canje"));

        assertTrue(recibidos.isEmpty());
    }

    @Test
    void pagarUnaCompraNotificaAlCliente() throws CinemaUQException {
        Escenario escenario = new Escenario();
        escenario.comprar(escenario.funcionEn(LocalDateTime.now().plusDays(2)), 2);

        assertEquals(1, escenario.registro.contar(TipoEvento.COMPRA_PAGADA));
        assertEquals(1, escenario.cliente.getListaNotificaciones().size());
    }

    @Test
    void canjearPuntosPublicaElEvento() throws CinemaUQException {
        Escenario escenario = new Escenario();
        escenario.cliente.getPuntos().acumular(500, LocalDate.now());
        ServicioCanje canje = new ServicioCanje(new ReglaCostoPorValor(), escenario.eventos);
        Recompensa bono = Recompensa.deEntrada("R-ENT", "Entrada hasta $15.000", 15000, new ReglaCostoPorValor());

        canje.canjear(escenario.cliente, bono);

        assertEquals(1, escenario.registro.contar(TipoEvento.CANJE_REALIZADO));
        assertEquals(350, escenario.cliente.getPuntos().getPuntosDisponibles(LocalDate.now()));
    }
}
