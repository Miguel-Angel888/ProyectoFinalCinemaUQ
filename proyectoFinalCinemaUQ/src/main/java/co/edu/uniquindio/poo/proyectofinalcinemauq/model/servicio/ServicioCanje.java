package co.edu.uniquindio.poo.proyectofinalcinemauq.model.servicio;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.eventos.EventoCine;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.eventos.PublicadorEventos;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.eventos.TipoEvento;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException.StockInsuficienteException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Cliente;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.recompensa.Canje;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.recompensa.Recompensa;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.recompensa.ReglaCostoRedencion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.recompensa.TipoRecompensa;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.ItemConfiteria;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Arma el catálogo de recompensas y procesa los canjes
public class ServicioCanje {

    // Valores máximos que cubren los bonos de entrada (su costo en puntos sale de la regla)
    private static final double[] VALORES_BONO_ENTRADA = {15000, 20000};

    private final ReglaCostoRedencion reglaCosto;
    private final PublicadorEventos eventos;

    public ServicioCanje(ReglaCostoRedencion reglaCosto) {
        this.reglaCosto = reglaCosto;
        this.eventos = eventos;
    }

    // Se arma cada vez que se pide, así refleja cambios en la confitería
    public List<Recompensa> armarCatalogo(List<ItemConfiteria> confiteria) {
        List<Recompensa> catalogo = new ArrayList<>();
        for (ItemConfiteria item : confiteria) {
            if (item.getPuntosRedencion() > 0) catalogo.add(Recompensa.deConfiteria(item));
        }
        for (double valor : VALORES_BONO_ENTRADA) {
            catalogo.add(Recompensa.deEntrada("R-ENT-" + (int) valor,
                    "Entrada hasta $" + String.format("%,.0f", valor).replace(',', '.'), valor, reglaCosto));
        }
        return catalogo;
    }

    public Canje canjear(Cliente cliente, Recompensa recompensa) throws CinemaUQException {
        if (cliente == null || recompensa == null) {
            throw new IllegalArgumentException("El canje requiere un cliente y una recompensa");
        }

        // 1. Validar todo antes de gastar puntos
        ItemConfiteria item = recompensa.getItem();
        if (recompensa.getTipo() == TipoRecompensa.CONFITERIA && !item.hayStock(1)) {
            throw new StockInsuficienteException("Por ahora no hay existencias de " + recompensa.getNombre());
        }

        // 2. Descontar puntos (lanza PuntosInsuficientesException si no alcanzan; usa los que vencen primero)
        cliente.getPuntos().redimir(recompensa.getPuntosRequeridos(), LocalDate.now());

        // 3. Entregar el premio
        if (item != null) item.descontarStock(1);
        Canje canje = new Canje(cliente, recompensa);
        cliente.agregarCanje(canje);
        eventos.publicar(new EventoCine(TipoEvento.CANJE_REALIZADO, cliente, "Canjeaste " + recompensa.getNombre()
                + " por " + recompensa.getPuntosRequeridos() + " puntos. Código: " + canje.getCodigo()));
        return canje;
    }
}
