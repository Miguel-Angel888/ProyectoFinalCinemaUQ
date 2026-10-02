package co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte;

import java.time.LocalDateTime;
import java.util.List;

public abstract class Reporte {

    //Atributos
    private String titulo;
    private LocalDateTime periodoInicio;
    private LocalDateTime periodoFin;
    private double total;
    /*Organizar */private int registros;

    //Constructor


    public Reporte(String titulo, LocalDateTime periodoInicio,
                   LocalDateTime periodoFin, double total, int registros) {
        this.titulo = titulo;
        this.periodoInicio = periodoInicio;
        this.periodoFin = periodoFin;
        this.total = total;
        this.registros = registros;
    }

    //Getters y setters

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDateTime getPeriodoInicio() {
        return periodoInicio;
    }

    public void setPeriodoInicio(LocalDateTime periodoInicio) {
        this.periodoInicio = periodoInicio;
    }

    public LocalDateTime getPeriodoFin() {
        return periodoFin;
    }

    public void setPeriodoFin(LocalDateTime periodoFin) {
        this.periodoFin = periodoFin;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getRegistros() {
        return registros;
    }

    public void setRegistros(int registros) {
        this.registros = registros;
    }
}
