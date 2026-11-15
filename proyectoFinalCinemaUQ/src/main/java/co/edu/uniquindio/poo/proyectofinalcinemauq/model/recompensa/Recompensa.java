package co.edu.uniquindio.poo.proyectofinalcinemauq.model.recompensa;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.ItemConfiteria;

// Un premio del catálogo que el cliente puede obtener con sus puntos
public class Recompensa {

    private final String referencia;
    private final String nombre;
    private final TipoRecompensa tipo;
    private final int puntosRequeridos;
    private final ItemConfiteria item;     // solo si es de confitería
    private final double valorMaximo;      // solo si es entrada: hasta cuánto cubre

    private Recompensa(String referencia, String nombre, TipoRecompensa tipo, int puntosRequeridos,
                       ItemConfiteria item, double valorMaximo) {
        if (puntosRequeridos <= 0) {
            throw new IllegalArgumentException("Una recompensa debe costar al menos un punto");
        }
        this.referencia = referencia;
        this.nombre = nombre;
        this.tipo = tipo;
        this.puntosRequeridos = puntosRequeridos;
        this.item = item;
        this.valorMaximo = valorMaximo;
    }

    // El costo sale del ítem (un Producto o un Combo, gracias al Composite)
    public static Recompensa deConfiteria(ItemConfiteria item) {
        return new Recompensa("R-" + item.getReferencia(), item.getNombre(), TipoRecompensa.CONFITERIA,
                item.getPuntosRedencion(), item, 0);
    }

    public static Recompensa deEntrada(String referencia, String nombre, double valorMaximo,
                                       ReglaCostoRedencion regla) {
        return new Recompensa(referencia, nombre, TipoRecompensa.ENTRADA,
                regla.calcular(valorMaximo), null, valorMaximo);
    }

    public String getReferencia() { return referencia; }
    public String getNombre() { return nombre; }
    public TipoRecompensa getTipo() { return tipo; }
    public int getPuntosRequeridos() { return puntosRequeridos; }
    public ItemConfiteria getItem() { return item; }
    public double getValorMaximo() { return valorMaximo; }

    @Override
    public String toString() { return nombre; }
}
