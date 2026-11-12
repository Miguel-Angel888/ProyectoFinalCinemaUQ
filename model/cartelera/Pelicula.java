package co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera;

public class Pelicula {

    //Atributos
    private String referencia;
    private String titulo;
    private String genero;
    private int duracionMinutos;
    private String clasificacion;
    private boolean enCartelera;

    //Constructor
    public Pelicula(String referencia, String titulo, String genero, int duracionMinutos, String clasificacion) {
        this.referencia = referencia;
        this.titulo = titulo;
        this.genero = genero;
        this.duracionMinutos = duracionMinutos;
        this.clasificacion = clasificacion;
        this.enCartelera = true;
    }

    //Getters y setters

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public String getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(String clasificacion) {
        this.clasificacion = clasificacion;
    }

    public boolean isEnCartelera() {
        return enCartelera;
    }

    public void setEnCartelera(boolean enCartelera) {
        this.enCartelera = enCartelera;
    }
}
