package co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// PATRÓN PROXY - Sujeto real
public class TarjetaVirtual implements MedioPago {

    //Atributos
    private String numeroTarjeta;
    private double saldo;
    private EstadoTarjeta estadoTarjeta;
    private final List<Movimiento> listaMovimientos;

    private final MovimientoCreator recargaCreator = new RecargaCreator();
    private final MovimientoCreator pagoCreator = new PagoCreator();
    private final MovimientoCreator reembolsoCreator = new ReembolsoCreator();

    //Constructor
    public TarjetaVirtual(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
        this.saldo = 0;
        this.estadoTarjeta = EstadoTarjeta.ACTIVA;
        this.listaMovimientos = new ArrayList<>();
    }

    //Getters y setters


    public List<Movimiento> getListaMovimientos() {

        return Collections.unmodifiableList(listaMovimientos);
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

    public EstadoTarjeta getEstadoTarjeta() {
        return estadoTarjeta;
    }

    public void setEstadoTarjeta(EstadoTarjeta estadoTarjeta) {
        this.estadoTarjeta = estadoTarjeta;
    }

    //operaciones

    public void recargar(double monto) throws CinemaUQException.MontoInvalidoException, CinemaUQException.TarjetaInactivaException {
        validarMonto(monto);
        validarActiva();
        saldo += monto;
        listaMovimientos.add(recargaCreator.registrar(monto, saldo, "Recarga de saldo"));
    }

    public void debitar (double monto, String descripcion) throws CinemaUQException.MontoInvalidoException, CinemaUQException.TarjetaInactivaException, CinemaUQException.SaldoInsuficienteException {
        validarMonto(monto);
        validarActiva();
        if (saldo < monto) {
            throw new CinemaUQException.SaldoInsuficienteException("Saldo insuficiente. Saldo disponible: " + saldo);
        }
        saldo -= monto;
        listaMovimientos.add(pagoCreator.registrar(monto, saldo, descripcion));
    }

    public void acreditar (double monto, String descripcion) throws CinemaUQException.MontoInvalidoException, CinemaUQException.TarjetaInactivaException {
        validarMonto(monto);
        validarActiva();
        saldo += monto;
        listaMovimientos.add(reembolsoCreator.registrar(monto, saldo, descripcion));
    }

    public boolean puedePagar(double monto) {
        return estadoTarjeta == EstadoTarjeta.ACTIVA && saldo >= monto;
    }

    public void activar() {
        this.estadoTarjeta = EstadoTarjeta.ACTIVA;
    }
    public void desactivar() {
        this.estadoTarjeta = EstadoTarjeta.INACTIVA;
    }

    //validaciones privadas

    private void validarMonto(double monto) throws CinemaUQException.MontoInvalidoException {
        if (monto <= 0) throw new CinemaUQException.MontoInvalidoException("El monto debe ser mayor a 0");

    }

    private void validarActiva() throws CinemaUQException.TarjetaInactivaException {
        if (estadoTarjeta != EstadoTarjeta.ACTIVA) {
            throw new CinemaUQException.TarjetaInactivaException("La tarjeta no esta activa");
        }

    }
}
