package co.edu.uniquindio.poo.proyectofinalcinemauq.model.recompensa;

public enum TipoRecompensa {

    CONFITERIA("Confitería"),
    ENTRADA("Entrada");

    private final String etiqueta;

    TipoRecompensa(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String toString() { return etiqueta; }
}
