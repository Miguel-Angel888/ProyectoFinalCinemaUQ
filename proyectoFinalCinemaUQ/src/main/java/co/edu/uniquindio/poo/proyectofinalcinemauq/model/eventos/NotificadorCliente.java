package co.edu.uniquindio.poo.proyectofinalcinemauq.model.eventos;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte.Notificacion;

public class NotificadorCliente implements ObservadorCine{
    @Override
    public void actualizar(EventoCine evento) {
        evento.getCliente().agregarNotificacion(new Notificacion(evento.getCliente(), evento.getMensaje()));
    }
}
