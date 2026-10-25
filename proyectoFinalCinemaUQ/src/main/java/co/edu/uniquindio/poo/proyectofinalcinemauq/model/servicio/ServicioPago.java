package co.edu.uniquindio.poo.proyectofinalcinemauq.model.servicio;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException.*;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Cliente;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.MedioPago;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.Pago;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Compra;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.EstadoCompra;

import java.time.LocalDate;

public class ServicioPago {

    private final ReglaAcumulacionPuntos reglaPuntos;

    public ServicioPago(ReglaAcumulacionPuntos reglaPuntos) {
        this.reglaPuntos = reglaPuntos;
    }

    // Recibe la abstracción MedioPago: puede ser la tarjeta real o su proxy
    public Pago pagar(Compra compra, MedioPago tarjeta) throws CinemaUQException {
        // Se valida antes de cobrar, para no descontar y luego fallar
        if (compra.getEstado() != EstadoCompra.PENDIENTE) {
            throw new CompraInvalidaException("Solo se puede pagar una compra pendiente");
        }

        tarjeta.debitar(compra.getTotal(), "Compra " + compra.getId());

        Pago pago = new Pago(compra, tarjeta);
        int puntos = reglaPuntos.calcular(compra.getTotal());
        compra.registrarPago(pago, puntos);

        Cliente cliente = compra.getCliente();
        cliente.getPuntos().acumular(puntos, LocalDate.now());
        cliente.agregarCompra(compra);
        return pago;
    }

    public int calcularPuntos(double total) {
        return reglaPuntos.calcular(total);
    }
}