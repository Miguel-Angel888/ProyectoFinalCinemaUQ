package co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CuentaPuntos {

    private final List<AcumulacionPuntos> listaAcumulaciones;

    public CuentaPuntos() {
        this.listaAcumulaciones = new ArrayList<>();
    }

    //operaciones

    public void acumular(int puntos, LocalDate fechaObtencion) {
        if (puntos <= 0) return;   // una compra pequeña puede no generar puntos
        listaAcumulaciones.add(new AcumulacionPuntos(puntos, fechaObtencion));
    }

    //solo cuenta los puntos que no han vencido
    public int getPuntosDisponibles(LocalDate fecha) {
        int total = 0;
        for (AcumulacionPuntos a : listaAcumulaciones) {
            if (!a.estaVencida(fecha)) total += a.getPuntosDisponibles();
        }
        return total;
    }

    //se usan primero los puntos que estan mas cerca de vencer
    public void redimir(int puntos, LocalDate fecha) throws CinemaUQException.PuntosInsuficientesException {
        if (puntos <= 0) {
            throw new IllegalArgumentException("Los puntos a redimir deben ser mayores a cero");
        }
        int disponibles = getPuntosDisponibles(fecha);
        if (disponibles < puntos) {
            throw new CinemaUQException.PuntosInsuficientesException(
                    "Puntos insuficientes. Puntos disponibles: " + disponibles);
        }

        consumirVigentes(puntos, fecha);
    }

    //quita los puntos de una compra reembolsada. Si ya se gastaron, quita solo los que queden
    //y retorna cuantos pudo quitar
    public int revertir(int puntos, LocalDate fecha) {
        if (puntos <= 0) return 0;
        return consumirVigentes(Math.min(puntos, getPuntosDisponibles(fecha)), fecha);
    }

    //descuenta de las acumulaciones vigentes, primero las que vencen antes
    private int consumirVigentes(int puntos, LocalDate fecha) {
        List<AcumulacionPuntos> vigentes = new ArrayList<>();
        for (AcumulacionPuntos a : listaAcumulaciones) {
            if (!a.estaVencida(fecha)) vigentes.add(a);
        }
        vigentes.sort(Comparator.comparing(AcumulacionPuntos::getFechaVencimiento));

        int pendientes = puntos;
        for (AcumulacionPuntos a : vigentes) {
            if (pendientes == 0) break;
            pendientes -= a.consumir(pendientes);
        }
        return puntos - pendientes;
    }

    public List<AcumulacionPuntos> getListaAcumulaciones() {
        return Collections.unmodifiableList(listaAcumulaciones);
    }
}
