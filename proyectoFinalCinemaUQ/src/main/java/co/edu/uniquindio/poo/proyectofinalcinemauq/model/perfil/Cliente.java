package co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil;

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

    //Constructor
    public Cliente(String id, String nombre, String correo, String contrasenia, LocalDate fechaRegistro,
                   TarjetaVirtual tarjetaVirtual) {
        super(id, nombre, correo, contrasenia, fechaRegistro);
        this.tarjetaVirtual = tarjetaVirtual;
        this.puntos = new CuentaPuntos();
        this.listaCompras = new ArrayList<>();
    }

    //operaciones

    public void agregarCompra(Compra compra) {
        if (compra == null) {
            throw new IllegalArgumentException("La compra no puede ser nula");
        }
        listaCompras.add(compra);
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
