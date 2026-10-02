package co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta;

import java.util.List;

public class TarjetaVirtual {

    //Artributos
    private String numeroTarjeta;
    private double saldo;
    private EstadoTarjeta estadoTarjeta;
    private List<Movimiento> listaMovimientos;

    //Constructor
    public TarjetaVirtual(String numeroTarjeta, double saldo,
                          EstadoTarjeta estadoTarjeta, List<Movimiento> listaMovimientos) {
        this.numeroTarjeta = numeroTarjeta;
        this.saldo = saldo;
        this.estadoTarjeta = estadoTarjeta;
        this.listaMovimientos = listaMovimientos;
    }

    //Getters y setters


    public List<Movimiento> getListaMovimientos() {
        return listaMovimientos;
    }

    public void setListaMovimientos(List<Movimiento> listaMovimientos) {
        this.listaMovimientos = listaMovimientos;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public EstadoTarjeta getEstadoTarjeta() {
        return estadoTarjeta;
    }

    public void setEstadoTarjeta(EstadoTarjeta estadoTarjeta) {
        this.estadoTarjeta = estadoTarjeta;
    }
}
