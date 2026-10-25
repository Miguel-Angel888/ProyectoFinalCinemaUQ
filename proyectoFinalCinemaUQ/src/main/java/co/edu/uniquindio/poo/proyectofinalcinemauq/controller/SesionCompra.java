package co.edu.uniquindio.poo.proyectofinalcinemauq.controller;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Asiento;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Funcion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.ItemConfiteria;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.TipoEntrada;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Guarda lo que el cliente va eligiendo mientras pasa por las pantallas
// (función -> asientos -> confitería -> resumen)
public final class SesionCompra {

    private static final SesionCompra INSTANCIA = new SesionCompra();

    private Funcion funcion;
    private TipoEntrada tipoEntrada = TipoEntrada.GENERAL;
    private final List<Asiento> asientos = new ArrayList<>();
    private final Map<ItemConfiteria, Integer> confiteria = new LinkedHashMap<>();

    private SesionCompra() { }

    public static SesionCompra get() {
        return INSTANCIA;
    }

    public void iniciar(Funcion funcion) {
        limpiar();
        this.funcion = funcion;
    }

    public void limpiar() {
        funcion = null;
        tipoEntrada = TipoEntrada.GENERAL;
        asientos.clear();
        confiteria.clear();
    }

    public boolean hayCompraEnCurso() {
        return funcion != null && !asientos.isEmpty();
    }

    public Funcion getFuncion() { return funcion; }
    public TipoEntrada getTipoEntrada() { return tipoEntrada; }
    public void setTipoEntrada(TipoEntrada tipoEntrada) { this.tipoEntrada = tipoEntrada; }
    public List<Asiento> getAsientos() { return asientos; }
    public Map<ItemConfiteria, Integer> getConfiteria() { return confiteria; }
}
