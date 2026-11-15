package co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.recompensa.Canje;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte.Notificacion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.TarjetaVirtual;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Compra;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cliente extends Usuario {

    //Atributos
    private TarjetaVirtual tarjetaVirtual;
    private final CuentaPuntos puntos;
    private final List<Compra> listaCompras;
    private final List<Notificacion> listaNotificaciones;
    private final List<Canje> listaCanjes;

    //Constructor
    public Cliente(String id, String nombre, String correo, String contrasenia, LocalDate fechaRegistro,
                   TarjetaVirtual tarjetaVirtual) {
        super(id, nombre, correo, contrasenia, fechaRegistro);
        this.tarjetaVirtual = tarjetaVirtual;
        this.puntos = new CuentaPuntos();
        this.listaCompras = new ArrayList<>();
        this.listaNotificaciones = new ArrayList<>();
        this.listaCanjes = new ArrayList<>();
    }

    //operaciones

    public void agregarCompra(Compra compra) {
        if (compra == null) {
            throw new IllegalArgumentException("La compra no puede ser nula");
        }
        listaCompras.add(compra);
    }

    public void agregarNotificacion(Notificacion notificacion) {
        if (notificacion == null) {
            throw new IllegalArgumentException("La notificación no puede ser nula");
        }
        listaNotificaciones.add(notificacion);
    }

    public void agregarCanje(Canje canje) {
        if (canje == null) {
            throw new IllegalArgumentException("El canje no puede ser nulo");
        }
        listaCanjes.add(canje);
    }

    public List<Canje> getListaCanjes() {
        return Collections.unmodifiableList(listaCanjes);
    }

    public List<Notificacion> getListaNotificaciones() {
        return Collections.unmodifiableList(listaNotificaciones);
    }

    //Getters y setters

    public TarjetaVirtual getTarjetaVirtual() {
        return tarjetaVirtual;
    }

    public void setTarjetaVirtual(TarjetaVirtual tarjetaVirtual) {
        this.tarjetaVirtual = tarjetaVirtual;
    }

    public CuentaPuntos getPuntos() {
        return puntos;
    }

    public List<Compra> getListaCompras() {
        return Collections.unmodifiableList(listaCompras);
    }
}
