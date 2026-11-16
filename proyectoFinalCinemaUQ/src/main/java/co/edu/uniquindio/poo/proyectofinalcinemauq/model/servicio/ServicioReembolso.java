package co.edu.uniquindio.poo.proyectofinalcinemauq.model.servicio;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.EstadoFuncion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Funcion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.eventos.EventoCine;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.eventos.PublicadorEventos;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.eventos.TipoEvento;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException.CompraInvalidaException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Cliente;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.promocion.Promocion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.promocion.PromocionBonoEntrada;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.reembolso_Strategy.PoliticaFuncionCancelada;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.reembolso_Strategy.PoliticaReembolso;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.reembolso_Strategy.PoliticaReembolsoCompleto;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.reembolso_Strategy.PoliticaReembolsoParcial;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.reembolso_Strategy.PoliticaSinReembolso;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.MedioPago;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.Reembolso;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Compra;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Entrada;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.LineaCompra;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// Cancela una compra pagada y deshace todo lo que la compra movió:
// dinero, asientos, stock, puntos ganados y bono de entrada.
// PATRÓN STRATEGY - Contexto: el porcentaje a devolver lo decide la política que aplique.
public class ServicioReembolso {

    private final List<PoliticaReembolso> politicas;
    private final PublicadorEventos eventos;

    // El orden importa: se usa la primera política que aplique y la última debe aplicar siempre
    public ServicioReembolso(PublicadorEventos eventos) {
        this(eventos, List.of(
                new PoliticaFuncionCancelada(),
                new PoliticaReembolsoCompleto(),
                new PoliticaReembolsoParcial(),
                new PoliticaSinReembolso()));
    }

    public ServicioReembolso(PublicadorEventos eventos, List<PoliticaReembolso> politicas) {
        if (eventos == null) {
            throw new IllegalArgumentException("El servicio de reembolso requiere un publicador de eventos");
        }
        if (politicas == null || politicas.isEmpty()) {
            throw new IllegalArgumentException("Se necesita al menos una política de reembolso");
        }
        this.eventos = eventos;
        this.politicas = List.copyOf(politicas);
    }

    // Devuelve la política que corresponde a la función en este momento
    public PoliticaReembolso seleccionarPolitica(Funcion funcion, LocalDateTime ahora) {
        for (PoliticaReembolso politica : politicas) {
            if (politica.aplica(funcion, ahora)) return politica;
        }
        throw new IllegalStateException("Ninguna política de reembolso aplica; la última debe aplicar siempre");
    }

    // Devuelve 0 si la compra ya no se puede cancelar
    public double porcentajeAplicable(Compra compra, LocalDateTime ahora) {
        if (!compra.admiteReembolso()) return 0;
        return seleccionarPolitica(funcionDe(compra), ahora).getPorcentaje();
    }

    // Cancelación pedida por el cliente: el porcentaje depende de cuánto falta para la función
    public Reembolso reembolsar(Compra compra, MedioPago tarjeta, LocalDateTime ahora) throws CinemaUQException {
        validarDatos(compra, tarjeta, ahora);
        if (!compra.admiteReembolso()) {
            throw new CompraInvalidaException("Solo se puede cancelar una compra pagada");
        }
        PoliticaReembolso politica = seleccionarPolitica(funcionDe(compra), ahora);
        if (politica.getPorcentaje() == 0) {
            throw new CompraInvalidaException("Ya no es posible cancelar: faltan menos de "
                    + PoliticaReembolsoParcial.HORAS_MINIMAS + " horas para la función o ya empezó");
        }
        return ejecutar(compra, tarjeta, politica, ahora);
    }

    // Cancelación hecha por el cine: la función ya debe estar CANCELADA, así aplica la política del 100 %
    public Reembolso reembolsarPorFuncionCancelada(Compra compra, MedioPago tarjeta, LocalDateTime ahora)
            throws CinemaUQException {
        validarDatos(compra, tarjeta, ahora);
        if (funcionDe(compra).getEstadoFuncion() != EstadoFuncion.CANCELADA) {
            throw new CompraInvalidaException("La función de la compra " + compra.getId() + " no fue cancelada por el cine");
        }
        if (!compra.admiteReembolso()) {
            throw new CompraInvalidaException("La compra " + compra.getId() + " no está pagada");
        }
        return ejecutar(compra, tarjeta, seleccionarPolitica(funcionDe(compra), ahora), ahora);
    }

    private Reembolso ejecutar(Compra compra, MedioPago tarjeta, PoliticaReembolso politica, LocalDateTime ahora)
            throws CinemaUQException {
        // 1. Validar todo antes de mover nada
        Cliente cliente = compra.getCliente();
        LocalDate hoy = ahora.toLocalDate();
        int puntosDisponibles = cliente.getPuntos().getPuntosDisponibles(hoy);
        if (politica.exigePuntosIntactos() && puntosDisponibles < compra.getPuntosGanados()) {
            throw new CompraInvalidaException("No se puede cancelar: ya usaste los puntos que ganaste con esta compra");
        }

        // 2. Devolver el dinero primero: es lo único que puede fallar (p. ej. tarjeta inactiva),
        //    así si falla no queda nada a medias
        double porcentaje = politica.getPorcentaje();
        double cantidad = compra.getTotal() * porcentaje / 100;
        if (cantidad > 0) {   // una entrada pagada solo con bono puede tener total $0
            tarjeta.acreditar(cantidad, "Reembolso compra " + compra.getId());
        }

        // 3. Deshacer el resto. Si canceló el cine y el cliente ya gastó parte de esos puntos,
        //    solo se quitan los que le queden.
        cliente.getPuntos().revertir(Math.min(compra.getPuntosGanados(), puntosDisponibles), hoy);

        Funcion funcion = funcionDe(compra);
        for (Entrada entrada : compra.getListaEntradas()) funcion.liberarAsiento(entrada.getAsiento());
        for (LineaCompra linea : compra.getListaLineas()) linea.getItem().reponerStock(linea.getCantidad());
        for (Promocion p : compra.getPromociones()) {
            if (p instanceof PromocionBonoEntrada bono) bono.getBono().restaurar();
        }

        // La descripción guarda la política aplicada: el reporte de cancelaciones agrupa por ella
        Reembolso reembolso = new Reembolso(compra, "REE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                porcentaje, cantidad, ahora, politica.getNombre());
        compra.registrarReembolso(reembolso);

        String detalle = "Reembolso: $" + String.format("%,.0f", cantidad) + " (" + (int) porcentaje + " %)";
        String mensaje = politica.exigePuntosIntactos()
                ? "Compra " + compra.getId() + " cancelada. " + detalle
                : "El cine canceló la función de \"" + funcion.getPelicula().getTitulo() + "\" del "
                        + funcion.getFechaFuncion().toLocalDate() + ". " + detalle + " a tu tarjeta.";
        eventos.publicar(new EventoCine(TipoEvento.COMPRA_CANCELADA, cliente, mensaje));
        return reembolso;
    }

    private void validarDatos(Compra compra, MedioPago tarjeta, LocalDateTime ahora) {
        if (compra == null || tarjeta == null || ahora == null) {
            throw new IllegalArgumentException("El reembolso requiere una compra, una tarjeta y una fecha");
        }
    }

    private Funcion funcionDe(Compra compra) {
        return compra.getListaEntradas().get(0).getFuncion();
    }
}
