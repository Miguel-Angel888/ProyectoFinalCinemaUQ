package co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil;

import java.time.LocalDate;
import java.util.ArrayList;

public class Cliente extends Usuario {

    //Atributos
    private TarjetaVirtual tarjetaVirtual;
    private CuentaPuntos puntos;
    private ArrayList<Compra> listaCompras;

    //Constructor
    public Cliente(String id, String nombre, String correo, String contrasenia, LocalDate fechaRegistro,
                   TarjetaVirtual tarjetaVirtual, CuentaPuntos puntos, ArrayList<Compra> listaCompras) {
        super(id, nombre, correo, contrasenia, fechaRegistro);
        this.tarjetaVirtual = tarjetaVirtual;
        this.puntos = puntos;
        this.listaCompras = listaCompras;
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

    public void setPuntos(CuentaPuntos puntos) {
        this.puntos = puntos;
    }

    public ArrayList<Compra> getListaCompras() {
        return listaCompras;
    }

    public void setListaCompras(ArrayList<Compra> listaCompras) {
        this.listaCompras = listaCompras;
    }
}
