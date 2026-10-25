package co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sala {

    //Atributos
    private String referencia;
    private String nombre;
    private final List<Asiento> listaAsientos;

    //Constructor
    public Sala(String referencia, String nombre) {
        this.referencia = referencia;
        this.nombre = nombre;
        this.listaAsientos = new ArrayList<>();
    }

    //operaciones

    public void agregarAsiento(Asiento asiento) {
        if (asiento == null) {
            throw new IllegalArgumentException("El asiento no puede ser nulo");
        }
        listaAsientos.add(asiento);
    }

    public Asiento buscarAsiento(String referencia) {
        for (Asiento asiento : listaAsientos) {
            if (asiento.getReferencia().equals(referencia)) return asiento;
        }
        return null;
    }

    public int getCapacidad() {
        return listaAsientos.size();
    }

    //Getters y setters

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Asiento> getListaAsientos() {
        return Collections.unmodifiableList(listaAsientos);
    }
}
