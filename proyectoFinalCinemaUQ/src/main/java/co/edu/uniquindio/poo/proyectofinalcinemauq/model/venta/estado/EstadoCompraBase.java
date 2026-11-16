package co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.estado;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException.CompraInvalidaException;

// Comportamiento por defecto: ninguna transición está permitida.
// Cada estado concreto sobrescribe solo las que sí admite.
abstract class EstadoCompraBase implements EstadoDeCompra {

    @Override
    public EstadoDeCompra pagar() throws CompraInvalidaException {
        throw new CompraInvalidaException("Solo se puede pagar una compra pendiente");
    }

    @Override
    public EstadoDeCompra reembolsar() throws CompraInvalidaException {
        throw new CompraInvalidaException("Solo se puede cancelar una compra pagada");
    }

    @Override
    public EstadoDeCompra cancelar() throws CompraInvalidaException {
        throw new CompraInvalidaException("Solo se puede descartar una compra que aún no se ha pagado");
    }

    @Override
    public boolean admiteReembolso() {
        return false;
    }

    @Override
    public boolean fueCobrada() {
        return false;
    }

    @Override
    public String toString() {
        return getTipo().toString();
    }
}
