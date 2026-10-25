package co.edu.uniquindio.poo.proyectofinalcinemauq.model;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Funcion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Pelicula;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Sala;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Usuario;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.promocion.Promocion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.SolicitudRecarga;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Compra;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.ItemConfiteria;

import java.util.ArrayList;
import java.util.List;

// Guarda toda la información del cine en memoria
public class Cine {

    private final String nombre;
    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<Pelicula> peliculas = new ArrayList<>();
    private final List<Sala> salas = new ArrayList<>();
    private final List<Funcion> funciones = new ArrayList<>();
    private final List<ItemConfiteria> confiteria = new ArrayList<>();
    private final List<Promocion> promociones = new ArrayList<>();
    private final List<Compra> compras = new ArrayList<>();
    private final List<SolicitudRecarga> solicitudesRecarga = new ArrayList<>();

    public Cine(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() { return nombre; }
    public List<Usuario> getUsuarios() { return usuarios; }
    public List<Pelicula> getPeliculas() { return peliculas; }
    public List<Sala> getSalas() { return salas; }
    public List<Funcion> getFunciones() { return funciones; }
    public List<ItemConfiteria> getConfiteria() { return confiteria; }
    public List<Promocion> getPromociones() { return promociones; }
    public List<Compra> getCompras() { return compras; }
    public List<SolicitudRecarga> getSolicitudesRecarga() { return solicitudesRecarga; }
}
