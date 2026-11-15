package co.edu.uniquindio.poo.proyectofinalcinemauq.model.servicio;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Funcion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException.CompraInvalidaException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Cliente;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.promocion.Promocion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.promocion.PromocionBonoEntrada;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte.Notificacion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.MedioPago;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.Reembolso;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Compra;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.EstadoCompra;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Entrada;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.LineaCompra;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

// Cancela una compra pagada y deshace todo lo que la compra movió:
// dinero, asientos, stock, puntos ganados y bono de entrada.
public class ServicioReembolso {

    // Política de reembolso (ajustar a gusto): según cuánto falta para la función
    public static final long HORAS_REEMBOLSO_COMPLETO = 24;   // 24 h o más antes: 100 %
    public static final long HORAS_MINIMAS = 2;               // entre 2 y 24 h antes: 50 %; menos de 2 h: no se puede
    public static final double PORCENTAJE_COMPLETO = 100;
    public static final double PORCENTAJE_PARCIAL = 50;

    // Devuelve 0 si la compra ya no se puede cancelar
    public double porcentajeAplicable(Compra compra, LocalDateTime ahora) {
        if (compra.getEstado() != EstadoCompra.PAGADA) return 0;
        LocalDateTime inicio = compra.getListaEntradas().get(0).getFuncion().getFechaFuncion();
        if (!ahora.isBefore(inicio)) return 0;
        long horas = Duration.between(ahora, inicio).toHours();
        if (horas >= HORAS_REEMBOLSO_COMPLETO) return PORCENTAJE_COMPLETO;
        if (horas >= HORAS_MINIMAS) return PORCENTAJE_PARCIAL;
        return 0;
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
                    + HORAS_MINIMAS + " horas para la función o ya empezó");
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
        cliente.agregarNotificacion(new Notificacion(cliente, "Compra " + compra.getId() + " cancelada. Reembolso: $"
                + String.format("%,.0f", cantidad) + " (" + (int) porcentaje + " %)"));
        return reembolso;
    }
}
