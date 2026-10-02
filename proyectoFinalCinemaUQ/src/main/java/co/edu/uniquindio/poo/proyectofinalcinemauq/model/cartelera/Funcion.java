package co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera;

import java.time.LocalDateTime;

public class Funcion {

    //Atributos
    private String referencia;
    private Pelicula pelicula;
    private Sala sala;
    private LocalDateTime fechaFuncion;
    private double precioBase;
    private EstadoFuncion estadoFuncion;

    //Constructor


    public Funcion(String referencia, Pelicula pelicula, Sala sala,
                   LocalDateTime fechaFuncion, double precioBase, EstadoFuncion estadoFuncion) {
        this.referencia = referencia;
        this.pelicula = pelicula;
        this.sala = sala;
        this.fechaFuncion = fechaFuncion;
        this.precioBase = precioBase;
        this.estadoFuncion = estadoFuncion;
    }


    //Getters y setters


    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    public void setPelicula(Pelicula pelicula) {
        this.pelicula = pelicula;
    }

    public LocalDateTime getFechaFuncion() {
        return fechaFuncion;
    }

    public void setFechaFuncion(LocalDateTime fechaFuncion) {
        this.fechaFuncion = fechaFuncion;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(double precioBase) {
        this.precioBase = precioBase;
    }

    public EstadoFuncion getEstadoFuncion() {
        return estadoFuncion;
    }

    public void setEstadoFuncion(EstadoFuncion estadoFuncion) {
        this.estadoFuncion = estadoFuncion;
    }
}
