package co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.estado;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.EstadoCompra;

// PATRÓN STATE - Estado concreto
// La compra está armada (resumen) pero todavía no se ha cobrado ni reservado nada.
public class EstadoPendiente extends EstadoCompraBase {

    @Override
    public EstadoCompra getTipo() {
        return EstadoCompra.PENDIENTE;
    }

    @Override
    public EstadoDeCompra pagar() {
        return new EstadoPagada();
    }

    @Override
    public EstadoDeCompra cancelar() {
        return new EstadoCancelada();
    }
}
