package co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta;

import java.time.LocalDateTime;

public abstract class Movimiento {

    private final String referencia;
    private final double cantidad;
    private final LocalDateTime fechaMovimiento;
    private final double saldoResultante;
    private final String descripcion;

    // La fecha se recibe para poder registrar movimientos históricos y filtrarlos por periodo en los reportes
    protected Movimiento(String referencia, double cantidad, double saldoResultante,
                         String descripcion, LocalDateTime fechaMovimiento) {
        if (fechaMovimiento == null) {
            throw new IllegalArgumentException("El movimiento debe tener fecha");
        }
        this.referencia = referencia;
        this.cantidad = cantidad;
        this.fechaMovimiento = fechaMovimiento;
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
