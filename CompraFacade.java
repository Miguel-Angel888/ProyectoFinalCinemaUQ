package co.edu.uniquindio.poo.proyectofinalcinemauq.model.facade;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Asiento;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.EstadoFuncion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Funcion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Cliente;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte.Notificacion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.servicio.ServicioPago;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CompraFacade {
//    Validar que la función esté activa
//    Reservar los asientos (verificar y marcar a la vez)
//    Construir las entradas y armar la compra con CompraBuilder, agregando promociones, líneas de productos, etc.
//    Pagar con ServicioPago (si falla: liberar asientos y relanzar)
//    Notificar al cliente

    private final ServicioPago servicioPago;

    public CompraFacade(ServicioPago servicioPago) {
        this.servicioPago = servicioPago;
    }

    public Compra comprar(Cliente cliente, Funcion funcion, List<Asiento> asientos,
                          TipoEntrada tipoEntrada, List<LineaCompra> productos,
                          List<Promocion> promociones) throws CinemaUQException {

        // 1. Validar la función antes de tocar nada
        if (funcion.getEstadoFuncion() != EstadoFuncion.PROGRAMADA) {
            throw new CinemaUQException.CompraInvalidaException("La función no está disponible para la venta");
        }

        // 2. Lista donde anotamos los asientos que logramos reservar
        List<Asiento> reservados = new ArrayList<>();

        try {
            Compra.CompraBuilder builder = new Compra.CompraBuilder().conCliente(cliente);

            // 3. Reservar cada asiento y crear su entrada
            for (Asiento asiento : asientos) {
                funcion.reservarAsiento(asiento);
                reservados.add(asiento);
                Entrada entrada = new Entrada(UUID.randomUUID().toString(), funcion, asiento,
                        tipoEntrada, funcion.getPrecioBase());
                builder.agregarEntrada(entrada);
            }

            // 4. Agregar productos y promociones
            for (LineaCompra linea : productos) builder.agregarLinea(linea);
            for (Promocion promocion : promociones) builder.agregarPromocion(promocion);

            // 5. Armar la compra y pagar
            Compra compra = builder.build();
            servicioPago.pagar(compra, cliente.getTarjetaVirtual());
            cliente.agregarNotificacion(new Notificacion(cliente,
                    "Compra " + compra.getId() + " realizada con éxito. Total: $" + compra.getTotal()));
            return compra;

        } catch (CinemaUQException e) {
            // 6. Si algo falló, devolver los asientos y avisar del error
            for (Asiento asiento : reservados) {
                funcion.liberarAsiento(asiento);
            }
            throw e;
        }
    }
}
