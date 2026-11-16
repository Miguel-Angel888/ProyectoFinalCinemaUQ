package co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta;

// Nombre visible de cada estado de la compra. El comportamiento de cada uno
// está en el paquete venta.estado (patrón State).
public enum EstadoCompra {
    PENDIENTE("Pendiente"),
    PAGADA("Pagada"),
    CANCELADA("Cancelada sin cobro"),
    REEMBOLSADA("Reembolsada");

    private final String etiqueta;

    EstadoCompra(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
