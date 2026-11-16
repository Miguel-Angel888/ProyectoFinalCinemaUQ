package co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.estado;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.EstadoCompra;

// PATRÓN STATE - Estado concreto
// Se cobró la compra y los asientos quedaron reservados. Puede reembolsarse
// (por el cliente según la política vigente o por el cine si cancela la función).
public class EstadoPagada extends EstadoCompraBase {

    @Override
    public EstadoCompra getTipo() {
        return EstadoCompra.PAGADA;
    }

    @Override
    public EstadoDeCompra reembolsar() {
        return new EstadoReembolsada();
    }

    @Override
    public boolean admiteReembolso() {
        return true;
    }

    @Override
    public boolean fueCobrada() {
        return true;
    }
}
