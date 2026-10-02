package co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta;

import java.time.LocalDateTime;

public class Movimiento {

    //Atributso
    private String referencia;
    private TipoMovimiento tipoMovimiento;
    private double cantidad;
    private LocalDateTime fechaMovimiento;
    private double saldoResultante;
    private String descripcion;

    //Constructor
    public Movimiento(String referencia, TipoMovimiento tipoMovimiento,
                      double cantidad, LocalDateTime fechaMovimiento, double saldoResultante, String descripcion) {
        this.referencia = referencia;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidad = cantidad;
        this.fechaMovimiento = fechaMovimiento;
        this.saldoResultante = saldoResultante;
        this.descripcion = descripcion;
    }

    //Getters y setters

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public TipoMovimiento getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(LocalDateTime fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public double getSaldoResultante() {
        return saldoResultante;
    }

    public void setSaldoResultante(double saldoResultante) {
        this.saldoResultante = saldoResultante;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
