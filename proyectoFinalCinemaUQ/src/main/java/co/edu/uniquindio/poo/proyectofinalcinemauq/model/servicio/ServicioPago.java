package co.edu.uniquindio.poo.proyectofinalcinemauq.model.servicio;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException.*;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.Pago;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.TarjetaVirtual;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Compra;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.EstadoCompra;

public class ServicioPago {

    private final ReglaAcumulacionPuntos reglaPuntos;

    public ServicioPago(ReglaAcumulacionPuntos reglaPuntos) {
        this.reglaPuntos = reglaPuntos;
    }

    public Pago pagar(Compra compra, TarjetaVirtual tarjeta) throws CinemaUQException {
        // Se valida antes de cobrar, para no descontar y luego fallar
        if (compra.getEstado() != EstadoCompra.PENDIENTE) {
            throw new CompraInvalidaException("Solo se puede pagar una compra pendiente");
        }

        tarjeta.debitar(compra.getTotal(), "Compra " + compra.getId());

        Pago pago = new Pago(compra, tarjeta);
        int puntos = reglaPuntos.calcular(compra.getTotal());
        compra.registrarPago(pago, puntos);
        return pago;
    }
}