package co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta;

import java.time.LocalDateTime;

//cambie la clase movimiento a una abstracta

public abstract class Movimiento {

    private final String referencia;
    private final double cantidad;
    private final LocalDateTime fechaMovimiento;
    private final double saldoResultante;
    private final String descripcion;

    protected Movimiento(String referencia, double cantidad,
                         double saldoResultante, String descripcion) {
        this.referencia = referencia;
        this.cantidad = cantidad;
        this.fechaMovimiento = LocalDateTime.now();
        this.saldoResultante = saldoResultante;
        this.descripcion = descripcion;
    }

    // Cada subclase declara su tipo y si suma o resta al saldo
    public abstract TipoMovimiento getTipoMovimiento();
    public abstract boolean esIngreso();

    public String getReferencia() { return referencia; }
    public double getCantidad() { return cantidad; }
    public LocalDateTime getFechaMovimiento() { return fechaMovimiento; }
    public double getSaldoResultante() { return saldoResultante; }
    public String getDescripcion() { return descripcion; }
}