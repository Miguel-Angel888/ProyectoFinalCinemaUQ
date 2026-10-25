package co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta;

public enum TipoEntrada {
    GENERAL(1.0, "General"),
    NINO(0.8, "Niño"),
    ADULTO_MAYOR(0.7, "Adulto mayor");

    private final double factorPrecio;   // multiplica el precio base de la función
    private final String etiqueta;

    TipoEntrada(double factorPrecio, String etiqueta) {
        this.factorPrecio = factorPrecio;
        this.etiqueta = etiqueta;
    }

    public double getFactorPrecio() { return factorPrecio; }

    @Override
    public String toString() { return etiqueta; }
}
