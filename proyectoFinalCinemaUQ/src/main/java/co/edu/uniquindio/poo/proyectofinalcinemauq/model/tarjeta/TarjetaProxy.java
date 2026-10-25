package co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException.SaldoInsuficienteException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException.TarjetaInactivaException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// PATRÓN PROXY - Proxy de protección
// Se interpone entre quien cobra y la tarjeta real: antes de dejar pasar un débito
// verifica que la tarjeta esté activa y tenga saldo, y deja registro de cada
// intento rechazado. TarjetaVirtual no se modifica para nada de esto.
public class TarjetaProxy implements MedioPago {

    private final TarjetaVirtual tarjetaReal;
    private final List<String> intentosRechazados;

    public TarjetaProxy(TarjetaVirtual tarjetaReal) {
        if (tarjetaReal == null) {
            throw new IllegalArgumentException("El proxy necesita una tarjeta real");
        }
        this.tarjetaReal = tarjetaReal;
        this.intentosRechazados = new ArrayList<>();
    }

    @Override
    public void debitar(double monto, String descripcion) throws CinemaUQException {
        if (tarjetaReal.getEstadoTarjeta() != EstadoTarjeta.ACTIVA) {
            registrarRechazo(monto, "tarjeta " + tarjetaReal.getEstadoTarjeta());
            throw new TarjetaInactivaException("La tarjeta " + tarjetaReal.getNumeroTarjeta() + " no está activa");
        }
        if (tarjetaReal.getSaldo() < monto) {
            registrarRechazo(monto, "saldo insuficiente");
            throw new SaldoInsuficienteException("Saldo insuficiente. Disponible: " + tarjetaReal.getSaldo()
                    + ", requerido: " + monto + ". Puedes solicitar una recarga.");
        }
        // Pasó las verificaciones: delega en la tarjeta real
        tarjetaReal.debitar(monto, descripcion);
    }

    @Override
    public void acreditar(double monto, String descripcion) throws CinemaUQException {
        tarjetaReal.acreditar(monto, descripcion);
    }

    @Override
    public boolean puedePagar(double monto) {
        return tarjetaReal.puedePagar(monto);
    }

    @Override
    public double getSaldo() {
        return tarjetaReal.getSaldo();
    }

    @Override
    public String getNumeroTarjeta() {
        return tarjetaReal.getNumeroTarjeta();
    }

    private void registrarRechazo(double monto, String motivo) {
        intentosRechazados.add(LocalDateTime.now() + " - Pago por " + monto + " rechazado: " + motivo);
    }

    public List<String> getIntentosRechazados() {
        return Collections.unmodifiableList(intentosRechazados);
    }
}
