package co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Compra;

import java.time.LocalDateTime;
import java.util.UUID;

public class Pago {

    private final String referencia;
    private final Compra compra;
    private final MedioPago tarjeta;   // tarjeta usada: el reembolso debe volver a ella
    private final double cantidad;
    private final LocalDateTime fechaPago;

    public Pago(Compra compra, MedioPago tarjeta) {
        if (compra == null || tarjeta == null) {
            throw new IllegalArgumentException("El pago requiere una compra y una tarjeta");
        }
        this.referencia = "PAG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.compra = compra;
        this.tarjeta = tarjeta;
        this.cantidad = compra.getTotal();
        this.fechaPago = LocalDateTime.now();
    }

    public String getReferencia() { return referencia; }
    public Compra getCompra() { return compra; }
    public MedioPago getTarjeta() { return tarjeta; }
    public double getCantidad() { return cantidad; }
    public LocalDateTime getFechaPago() { return fechaPago; }
}