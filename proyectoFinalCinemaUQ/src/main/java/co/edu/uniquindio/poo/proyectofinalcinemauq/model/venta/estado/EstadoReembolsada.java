package co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.estado;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException.CompraInvalidaException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.EstadoCompra;

// PATRÓN STATE - Estado final
// La compra se pagó y luego se canceló con devolución de dinero (total o parcial).
public class EstadoReembolsada extends EstadoCompraBase {

    @Override
    public EstadoCompra getTipo() {
        return EstadoCompra.REEMBOLSADA;
    }

    @Override
    public EstadoDeCompra reembolsar() throws CompraInvalidaException {
        throw new CompraInvalidaException("Esta compra ya fue reembolsada");
    }

    @Override
    public boolean fueCobrada() {
        return true;
    }
}
