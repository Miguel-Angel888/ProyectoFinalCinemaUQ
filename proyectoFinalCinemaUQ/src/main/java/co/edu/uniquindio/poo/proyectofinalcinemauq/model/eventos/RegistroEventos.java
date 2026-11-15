package co.edu.uniquindio.poo.proyectofinalcinemauq.model.eventos;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class RegistroEventos implements ObservadorCine {
    private final List<EventoCine> eventos = new ArrayList<>();

    @Override
    public void actualizar(EventoCine evento) {
        eventos.add(evento);
    }

    public int contar(TipoEvento tipo) {
        int total = 0;
        for (EventoCine e : eventos) {
            if (e.getTipo() == tipo) total++;
        }
        return total;
    }

    public List<EventoCine> getEventos(TipoEvento tipo, LocalDate desde, LocalDate hasta) {
        List<EventoCine> resultado = new ArrayList<>();
        for (EventoCine e : eventos) {
            LocalDate dia = e.getFecha().toLocalDate();
            if (e.getTipo() == tipo && !dia.isBefore(desde) && !dia.isAfter(hasta)) resultado.add(e);
        }
        return resultado;
    }

    public List<EventoCine> getEventos() {
        return Collections.unmodifiableList(eventos);
    }
}

