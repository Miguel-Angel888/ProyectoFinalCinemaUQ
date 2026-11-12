package co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil;

import java.time.LocalDate;

//cada acumulacion guarda su fecha, porque los puntos vencen un año despues de obtenidos
public class AcumulacionPuntos {

    private final int puntosObtenidos;
    private final LocalDate fechaObtencion;
    private final LocalDate fechaVencimiento;
    private int puntosDisponibles;

    public AcumulacionPuntos(int puntosObtenidos, LocalDate fechaObtencion) {
        if (puntosObtenidos <= 0 || fechaObtencion == null) {
            throw new IllegalArgumentException("La acumulación requiere puntos mayores a cero y una fecha");
        }
        this.puntosObtenidos = puntosObtenidos;
        this.fechaObtencion = fechaObtencion;
        this.fechaVencimiento = fechaObtencion.plusYears(1);
        this.puntosDisponibles = puntosObtenidos;
    }

    public boolean estaVencida(LocalDate fecha) {
        return !fecha.isBefore(fechaVencimiento);
    }

    //descuenta hasta lo que tenga disponible y retorna cuanto pudo descontar
    int consumir(int puntos) {
        int consumidos = Math.min(puntos, puntosDisponibles);
        puntosDisponibles -= consumidos;
        return consumidos;
    }

    public int getPuntosObtenidos() { return puntosObtenidos; }
    public LocalDate getFechaObtencion() { return fechaObtencion; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public int getPuntosDisponibles() { return puntosDisponibles; }
}
