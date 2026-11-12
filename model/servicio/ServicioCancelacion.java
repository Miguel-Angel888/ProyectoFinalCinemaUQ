package co.edu.uniquindio.poo.proyectofinalcinemauq.model.servicio;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.EstadoFuncion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Funcion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException.CancelacionNoPermitidaException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException.CompraInvalidaException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Cliente;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte.Notificacion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.MotivoReembolso;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.Reembolso;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.TarjetaVirtual;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Compra;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Entrada;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.EstadoCompra;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class ServicioCancelacion {

    // dos estrategias distintas según quién cancele (DIP: solo se conoce la interfaz)
    private final PoliticaReembolso politicaCliente;
    private final PoliticaReembolso politicaCine;

    public ServicioCancelacion(PoliticaReembolso politicaCliente, PoliticaReembolso politicaCine) {
        this.politicaCliente = politicaCliente;
        this.politicaCine = politicaCine;
    }

    // el cliente cancela su compra: se devuelve lo que diga la política y se liberan los asientos
    public Reembolso cancelarCompra(Compra compra, LocalDateTime ahora) throws CinemaUQException {
        validarReembolsable(compra);

        Funcion funcion = compra.getFuncion();
        if (funcion.getEstadoFuncion() != EstadoFuncion.PROGRAMADA) {
            throw new CancelacionNoPermitidaException("La función ya no está programada");
        }

        Reembolso reembolso = reembolsar(compra, politicaCliente, MotivoReembolso.CANCELACION_CLIENTE, ahora);
        for (Entrada entrada : compra.getListaEntradas()) {
            funcion.liberarAsiento(entrada.getAsiento());
        }
        return reembolso;
    }

    // el cine cancela la función: se reembolsan todas las compras pagadas de esa función.
    // Si algo falla a la mitad (por ejemplo una tarjeta inactiva) se puede volver a llamar:
    // las compras que ya se reembolsaron no se tocan de nuevo
    public List<Reembolso> cancelarFuncion(Funcion funcion, Collection<Compra> compras, LocalDateTime ahora)
            throws CinemaUQException {
        if (funcion.getEstadoFuncion() == EstadoFuncion.FINALIZADA) {
            throw new CancelacionNoPermitidaException("No se puede cancelar una función que ya finalizó");
        }
        funcion.setEstadoFuncion(EstadoFuncion.CANCELADA);   // desde ya no se pueden vender más entradas

        List<Reembolso> reembolsos = new ArrayList<>();
        for (Compra compra : compras) {
            boolean esDeLaFuncion = compra.getFuncion().getReferencia().equals(funcion.getReferencia());
            if (esDeLaFuncion && compra.getEstado() == EstadoCompra.PAGADA) {
                reembolsos.add(reembolsar(compra, politicaCine, MotivoReembolso.FUNCION_CANCELADA, ahora));
            }
        }
        return reembolsos;
    }

    // Lógica común. Primero se calcula y se acredita a la tarjeta; si algo falla ahí no cambia nada
    private Reembolso reembolsar(Compra compra, PoliticaReembolso politica, MotivoReembolso motivo,
                                 LocalDateTime ahora) throws CinemaUQException {
        validarReembolsable(compra);

        double porcentaje = politica.calcularPorcentaje(ahora, compra.getFuncion().getFechaFuncion());
        double monto = compra.getTotal() * porcentaje / 100;

        // el dinero vuelve a la misma tarjeta con la que se pagó
        TarjetaVirtual tarjeta = compra.getPago().getTarjeta();
        tarjeta.acreditar(monto, "Reembolso de la compra " + compra.getId());

        Reembolso reembolso = new Reembolso(compra, "REE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                porcentaje, monto, ahora, descripcion(motivo), motivo);
        compra.registrarReembolso(reembolso);

        // regla adicional: se quitan los puntos ganados en la misma proporción del reembolso
        Cliente cliente = compra.getCliente();
        int puntosARevertir = (int) Math.round(compra.getPuntosGanados() * porcentaje / 100);
        cliente.getPuntos().revertir(puntosARevertir, ahora.toLocalDate());

        cliente.agregarNotificacion(new Notificacion(cliente, "Tu compra " + compra.getId()
                + " fue reembolsada (" + porcentaje + " %). Se devolvieron $" + monto + " a tu tarjeta."));
        return reembolso;
    }

    private void validarReembolsable(Compra compra) throws CompraInvalidaException {
        if (compra.getReembolso() != null) {
            throw new CompraInvalidaException("Esta compra ya fue reembolsada");
        }
        if (compra.getEstado() != EstadoCompra.PAGADA) {
            throw new CompraInvalidaException("Solo se puede reembolsar una compra pagada");
        }
    }

    private String descripcion(MotivoReembolso motivo) {
        return motivo == MotivoReembolso.FUNCION_CANCELADA
                ? "Función cancelada por el cine"
                : "Cancelación solicitada por el cliente";
    }
}
