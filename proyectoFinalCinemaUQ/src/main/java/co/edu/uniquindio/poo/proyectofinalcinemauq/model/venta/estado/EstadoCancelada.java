package co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.estado;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException.CompraInvalidaException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.EstadoCompra;

// PATRÓN STATE - Estado final
// La compra se descartó antes de pagarla: no hubo cobro ni reserva.
public class EstadoCancelada extends EstadoCompraBase {

    @Override
    public EstadoCompra getTipo() {
        return EstadoCompra.CANCELADA;
    }

    @Override
    public EstadoDeCompra cancelar() throws CompraInvalidaException {
        throw new CompraInvalidaException("La compra ya estaba cancelada");
    }
}
