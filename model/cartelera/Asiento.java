package co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera;

public class Asiento {

    //Atributos
    private String referencia;
    private char fila;
    private int numero;
    private TipoAsiento tipoAsiento;

    //Constructor


    public Asiento(String referencia, int numero, char fila, TipoAsiento tipoAsiento) {
        this.referencia = referencia;
        this.numero = numero;
        this.fila = fila;
        this.tipoAsiento = tipoAsiento;
    }

    //Getters y setters

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public char getFila() {
        return fila;
    }

    public void setFila(char fila) {
        this.fila = fila;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public TipoAsiento getTipoAsiento() {
        return tipoAsiento;
    }

    public void setTipoAsiento(TipoAsiento tipoAsiento) {
        this.tipoAsiento = tipoAsiento;
    }
}
