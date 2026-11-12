package co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta;

public enum TipoEntrada {

    // Cada tipo guarda su porcentaje de descuento sobre el precio base de la función
    GENERAL(0.0),
    NINO(0.20),
    ADULTO_MAYOR(0.30);

    private final double descuento;

    TipoEntrada(double descuento) {
        this.descuento = descuento;
    }

    public double getDescuento() {
        return descuento;
    }

    public double calcularPrecio(double precioBase) {
        return precioBase * (1 - descuento);
    }
}
