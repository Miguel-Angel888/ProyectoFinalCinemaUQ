package co.edu.uniquindio.poo.proyectofinalcinemauq.model;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Asiento;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.EstadoFuncion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Funcion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Pelicula;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Sala;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.TipoAsiento;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.eventos.NotificadorCliente;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.eventos.PublicadorEventos;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.eventos.RegistroEventos;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.facade.CompraFacade;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Cliente;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.servicio.ReglaPuntosPorMonto;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.servicio.ServicioPago;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.TarjetaVirtual;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Compra;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.TipoEntrada;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// Arma un cine pequeño y controlado para las pruebas del modelo
public class Escenario {

    public final Cine cine = new Cine("CinemaUQ pruebas");
    public final PublicadorEventos eventos = new PublicadorEventos();
    public final RegistroEventos registro = new RegistroEventos();
    public final CompraFacade compraFacade;
    public final Sala sala;
    public final Pelicula pelicula;
    public final Cliente cliente;

    public Escenario() throws CinemaUQException {
        eventos.suscribir(new NotificadorCliente());
        eventos.suscribir(registro);
        compraFacade = new CompraFacade(new ServicioPago(new ReglaPuntosPorMonto()), eventos);

        sala = new Sala("S1", "Sala 1");
        for (int n = 1; n <= 10; n++) sala.agregarAsiento(new Asiento("A" + n, n, 'A', TipoAsiento.NORMAL));
        cine.getSalas().add(sala);

        pelicula = new Pelicula("P1", "Película de prueba", "Drama", 100, "Todos");
        cine.getPeliculas().add(pelicula);

        TarjetaVirtual tarjeta = new TarjetaVirtual("CUQ-T1");
        tarjeta.recargar(200000);
        cliente = new Cliente("1", "Cliente Prueba", "prueba@cinemauq.com", "1234", LocalDate.now(), tarjeta);
        cine.getUsuarios().add(cliente);
    }

    public Funcion funcionEn(LocalDateTime fecha) {
        Funcion funcion = new Funcion("F" + cine.getFunciones().size(), pelicula, sala, fecha, 20000, EstadoFuncion.PROGRAMADA);
        cine.getFunciones().add(funcion);
        return funcion;
    }

    // Compra pagada de "cantidad" entradas generales (sin confitería ni promociones)
    public Compra comprar(Funcion funcion, int cantidad) throws CinemaUQException {
        List<Asiento> asientos = new java.util.ArrayList<>();
        for (Asiento a : sala.getListaAsientos()) {
            if (asientos.size() == cantidad) break;
            if (funcion.estaDisponible(a)) asientos.add(a);
        }
        Compra compra = compraFacade.comprar(cliente, funcion, asientos, TipoEntrada.GENERAL, List.of(), List.of());
        cine.getCompras().add(compra);
        return compra;
    }
}
