package co.edu.uniquindio.poo.proyectofinalcinemauq.model.servicio;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Funcion;


import co.edu.uniquindio.poo.proyectofinalcinemauq.model.evento.EventoCine;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.evento.PublicadorEventos;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.evento.TipoEvento;

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
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.EstadoCompra;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Entrada;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.LineaCompra;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// Cancela una compra pagada y deshace todo lo que la compra movió:
// dinero, asientos, stock, puntos ganados y bono de entrada.
public class ServicioReembolso {

    private final List<PoliticaReembolso> politicas;
    private final PublicadorEventos eventos;

    public ServicioReembolso() {
        this(eventos, List.of(
                new PoliticaFuncionCancelada(),
                new PoliticaReembolsoCompleto(),
                new PoliticaReembolsoParcial(),
                new PoliticaSinReembolso()));
    }

    public ServicioReembolso(co.edu.uniquindio.poo.proyectofinalcinemauq.model.servicio.PublicadorEventos eventos, List<PoliticaReembolso> politicas) {
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
        if (compra.getEstado() != EstadoCompra.PAGADA) return 0;
        Funcion funcion = compra.getListaEntradas().get(0).getFuncion();
        return seleccionarPolitica(funcion, ahora).getPorcentaje();
    }

    public Reembolso reembolsar(Compra compra, MedioPago tarjeta, LocalDateTime ahora) throws CinemaUQException {
        if (compra == null || tarjeta == null) {
            throw new IllegalArgumentException("El reembolso requiere una compra y una tarjeta");
        }
        // 1. Validar todo antes de mover nada
        if (compra.getEstado() != EstadoCompra.PAGADA) {
            throw new CompraInvalidaException("Solo se puede cancelar una compra pagada");
        }
        double porcentaje = porcentajeAplicable(compra, ahora);
        if (porcentaje == 0) {
            throw new CompraInvalidaException("Ya no es posible cancelar: faltan menos de "
                    + PoliticaReembolsoParcial.HORAS_MINIMAS + " horas para la función o ya empezó");
        }
        Cliente cliente = compra.getCliente();
        LocalDate hoy = ahora.toLocalDate();
        if (cliente.getPuntos().getPuntosDisponibles(hoy) < compra.getPuntosGanados()) {
            throw new CompraInvalidaException("No se puede cancelar: ya usaste los puntos que ganaste con esta compra");
        }

        // 2. Devolver el dinero primero: es lo único que puede fallar (p. ej. tarjeta inactiva),
        //    así si falla no queda nada a medias
        double cantidad = compra.getTotal() * porcentaje / 100;
        if (cantidad > 0) {   // una entrada pagada solo con bono puede tener total $0
            tarjeta.acreditar(cantidad, "Reembolso compra " + compra.getId());
        }

        // 3. Deshacer el resto
        cliente.getPuntos().revertir(compra.getPuntosGanados(), hoy);

        Funcion funcion = compra.getListaEntradas().get(0).getFuncion();
        for (Entrada entrada : compra.getListaEntradas()) funcion.liberarAsiento(entrada.getAsiento());
        for (LineaCompra linea : compra.getListaLineas()) linea.getItem().reponerStock(linea.getCantidad());
        for (Promocion p : compra.getPromociones()) {
            if (p instanceof PromocionBonoEntrada bono) bono.getBono().restaurar();
        }

        Reembolso reembolso = new Reembolso(compra, "REE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                porcentaje, cantidad, ahora, "Cancelación de la compra " + compra.getId());
        compra.registrarReembolso(reembolso);
        eventos.publicar(new EventoCine(TipoEvento.COMPRA_CANCELADA, cliente, "Compra " + compra.getId()
                + " cancelada. Reembolso: $" + String.format("%,.0f", cantidad) + " (" + (int) porcentaje + " %)"));
        return reembolso;
    }
}
