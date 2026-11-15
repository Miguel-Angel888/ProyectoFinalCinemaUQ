package co.edu.uniquindio.poo.proyectofinalcinemauq.model.eventos;
import java.util.ArrayList;
import java.util.List;
public class PublicadorEventos {
    private final List<ObservadorCine> observadores = new ArrayList<>();

    public void suscribir(ObservadorCine observador) {
        if (observador == null) {
            throw new IllegalArgumentException("El observador no puede ser nulo");
        }
        if (!observadores.contains(observador)) observadores.add(observador);
    }

    public void desuscribir(ObservadorCine observador) {
        observadores.remove(observador);
    }

    public void publicar(EventoCine evento) {
        // Se recorre una copia por si un observador se suscribe o se retira mientras tanto
        for (ObservadorCine observador : new ArrayList<>(observadores)) {
            observador.actualizar(evento);
        }
    }
}
