package co.edu.uniquindio.poo.proyectofinalcinemauq.model.facade;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Asiento;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Funcion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Cliente;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.promocion.Promocion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte.Notificacion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.servicio.ServicioPago;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.Pago;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.TarjetaProxy;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// PATRÓN FACADE
// Reúne en un solo lugar todo el proceso de compra de una función:
//    Validar que la función esté activa
//    Reservar los asientos (verificar y marcar a la vez)
//    Construir las entradas y armar la compra con CompraBuilder, agregando promociones, líneas de productos, etc.
//    Pagar con ServicioPago a través del proxy de la tarjeta (si falla: liberar asientos y relanzar)
//    Notificar al cliente
public class CompraFacade {

    public static final int MAX_ENTRADAS_POR_COMPRA = 10;

    private final ServicioPago servicioPago;
    private final Map<String, TarjetaProxy> proxiesTarjeta = new HashMap<>();

    public CompraFacade(ServicioPago servicioPago) {
        this.servicioPago = servicioPago;
    }

    // Compra completa en un solo paso: arma la compra y la paga
    public Compra comprar(Cliente cliente, Funcion funcion, List<Asiento> asientos,
                          TipoEntrada tipoEntrada, List<LineaCompra> productos,
                          List<Promocion> promociones) throws CinemaUQException {
        Compra compra = cotizar(cliente, funcion, asientos, tipoEntrada, productos, promociones);
        confirmar(compra);
        return compra;
    }

    // Arma la compra en estado PENDIENTE para mostrar el resumen; todavía no reserva ni cobra
    public Compra cotizar(Cliente cliente, Funcion funcion, List<Asiento> asientos,
                          TipoEntrada tipoEntrada, List<LineaCompra> productos,
                          List<Promocion> promociones) throws CinemaUQException {

        // 1. Validar la función y los asientos antes de tocar nada
        if (funcion == null || !funcion.estaProgramada()) {
            throw new CinemaUQException.FuncionNoDisponibleException("La función no está disponible para la venta");
        }

        validarSinCruceDeHorario(cliente, funcion);

        if (asientos == null || asientos.isEmpty()) {
            throw new CinemaUQException.CompraInvalidaException("Selecciona al menos un asiento");
        }
        if (asientos.size() > MAX_ENTRADAS_POR_COMPRA) {
            throw new CinemaUQException.CompraInvalidaException(
                    "Máximo " + MAX_ENTRADAS_POR_COMPRA + " entradas por compra");
        }
        for (Asiento asiento : asientos) {
            if (!funcion.estaDisponible(asiento)) {
                throw new CinemaUQException.AsientoNoDisponibleException(
                        "El asiento " + asiento.getReferencia() + " ya está ocupado en esta función");
            }
        }

        // 2. Crear una entrada por asiento
        Compra.CompraBuilder builder = new Compra.CompraBuilder()
                .conId("CMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .conCliente(cliente);
        double precio = calcularPrecioEntrada(funcion, tipoEntrada);
        for (Asiento asiento : asientos) {
            builder.agregarEntrada(new Entrada(UUID.randomUUID().toString(), funcion, asiento, tipoEntrada, precio));
        }

        // 3. Agregar productos y promociones
        for (LineaCompra linea : productos) {
            validarStock(linea);
            builder.agregarLinea(linea);
        }
        for (Promocion promocion : promociones) builder.agregarPromocion(promocion);

        return builder.build();
    }

    // Reserva los asientos, cobra y notifica. Si algo falla devuelve los asientos.
    public Pago confirmar(Compra compra) throws CinemaUQException {
        Cliente cliente = compra.getCliente();
        Funcion funcion = compra.getListaEntradas().get(0).getFuncion();
        validarSinCruceDeHorario(cliente, funcion);
        for (LineaCompra linea : compra.getListaLineas()) validarStock(linea);

        // Lista donde anotamos los asientos que logramos reservar
        List<Asiento> reservados = new ArrayList<>();
        try {
            for (Entrada entrada : compra.getListaEntradas()) {
                funcion.reservarAsiento(entrada.getAsiento());
                reservados.add(entrada.getAsiento());
            }

            Pago pago = servicioPago.pagar(compra, getMedioPago(cliente));

            for (LineaCompra linea : compra.getListaLineas()) {
                linea.getItem().descontarStock(linea.getCantidad());
            }
            cliente.agregarNotificacion(new Notificacion(cliente,
                    "Compra " + compra.getId() + " realizada con éxito. Total: $" + compra.getTotal()));
            return pago;

        } catch (CinemaUQException e) {
            // Si algo falló, devolver los asientos y avisar del error
            for (Asiento asiento : reservados) {
                funcion.liberarAsiento(asiento);
            }
            throw e;
        }
    }

    // Un cliente no puede tener entradas pagadas para dos funciones distintas que se crucen en el tiempo.
// Comprar más sillas para la misma función en otra ocasión sí está permitido.
    public void validarSinCruceDeHorario(Cliente cliente, Funcion funcion) throws CinemaUQException {
        for (Compra previa : cliente.getListaCompras()) {
            if (previa.getEstado() != EstadoCompra.PAGADA) continue;
            for (Entrada entrada : previa.getListaEntradas()) {
                Funcion otra = entrada.getFuncion();
                if (otra != funcion && otra.seCruzaCon(funcion)) {
                    throw new CinemaUQException.CruceHorarioException(
                            "Ya tienes entradas para \"" + otra.getPelicula().getTitulo() + "\" ("
                                    + otra.getFechaFuncion().toLocalTime() + " - " + otra.getFechaFin().toLocalTime()
                                    + ") y se cruza con esta función.");
                }
            }
        }
    }

    public double calcularPrecioEntrada(Funcion funcion, TipoEntrada tipoEntrada) {
        return funcion.getPrecioBase() * tipoEntrada.getFactorPrecio();
    }

    // Cada tarjeta se usa siempre a través de su proxy
    public TarjetaProxy getMedioPago(Cliente cliente) {
        return proxiesTarjeta.computeIfAbsent(cliente.getTarjetaVirtual().getNumeroTarjeta(),
                numero -> new TarjetaProxy(cliente.getTarjetaVirtual()));
    }

    private void validarStock(LineaCompra linea) throws CinemaUQException.StockInsuficienteException {
        if (!linea.getItem().hayStock(linea.getCantidad())) {
            throw new CinemaUQException.StockInsuficienteException(
                    "No hay suficiente stock de " + linea.getItem().getNombre());
        }
    }
}
