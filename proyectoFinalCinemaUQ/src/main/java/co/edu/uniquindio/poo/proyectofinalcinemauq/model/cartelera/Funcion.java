package co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Funcion {

    //Atributos
    private String referencia;
    private Pelicula pelicula;
    private Sala sala;
    private LocalDateTime fechaFuncion;
    private double precioBase;
    private EstadoFuncion estadoFuncion;
    private final Set<String> asientosOcupados;   // referencias de los asientos vendidos en esta función

    //Constructor


    public Funcion(String referencia, Pelicula pelicula, Sala sala,
                   LocalDateTime fechaFuncion, double precioBase, EstadoFuncion estadoFuncion) {
        this.referencia = referencia;
        this.pelicula = pelicula;
        this.sala = sala;
        this.fechaFuncion = fechaFuncion;
        this.precioBase = precioBase;
        this.estadoFuncion = estadoFuncion;
        this.asientosOcupados = new HashSet<>();
    }

    public boolean estaDisponible(Asiento asiento) {
        return !asientosOcupados.contains(asiento.getReferencia());
    }

    public void reservarAsiento(Asiento asiento) throws CinemaUQException.AsientoNoDisponibleException {
        if (sala.buscarAsiento(asiento.getReferencia()) == null) {
            throw new IllegalArgumentException("El asiento " + asiento.getReferencia() + " no pertenece a la sala");
        }
        boolean seAgrego = asientosOcupados.add(asiento.getReferencia());
        if (!seAgrego) {
            throw new CinemaUQException.AsientoNoDisponibleException(
                    "El asiento " + asiento.getReferencia() + " ya está ocupado en esta función");
        }
    }

    public void liberarAsiento(Asiento asiento) {
        asientosOcupados.remove(asiento.getReferencia());
    }

    // Se puede vender mientras esté programada y no haya empezado
    public boolean estaProgramada() {
        return estadoFuncion == EstadoFuncion.PROGRAMADA && fechaFuncion.isAfter(LocalDateTime.now());
    }

    // Hora en que termina la función según la duración de la película
    public LocalDateTime getFechaFin() {
        return fechaFuncion.plusMinutes(pelicula.getDuracionMinutos());
    }

    // Dos funciones se cruzan si una empieza antes de que la otra termine
    public boolean seCruzaCon(Funcion otra) {
        return fechaFuncion.isBefore(otra.getFechaFin()) && otra.fechaFuncion.isBefore(getFechaFin());
    }

    public int getCantidadDisponibles() {
        return sala.getCapacidad() - asientosOcupados.size();
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
