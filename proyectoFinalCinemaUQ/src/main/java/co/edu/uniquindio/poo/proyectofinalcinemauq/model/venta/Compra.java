package co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException.CompraInvalidaException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Cliente;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.promocion.Cotizacion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.promocion.CotizacionBase;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.promocion.DescuentoAplicado;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.promocion.Promocion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.Pago;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.Reembolso;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.estado.EstadoDeCompra;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.estado.EstadoPendiente;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

// PATRÓN STATE - Contexto
// La compra guarda su estado actual como objeto y le delega qué se puede hacer
// (pagar, reembolsar, descartar). Así no hay condicionales sobre el estado aquí.
public class Compra {

    // Atributos
    private final String id;
    private final Cliente cliente;
    private final LocalDateTime fecha;
    private final List<Entrada> listaEntradas;
    private final List<LineaCompra> listaLineas;
    private final List<Promocion> promociones;
    private final double subtotal;
    private final double descuento;
    private final double total;
    private final List<DescuentoAplicado> descuentosAplicados;
    private EstadoDeCompra estado;
    private int puntosGanados;
    private Pago pago;
    private Reembolso reembolso;

    private Compra(CompraBuilder builder) {
        this.id = builder.id;
        this.cliente = builder.cliente;
        this.fecha = builder.fecha != null ? builder.fecha : LocalDateTime.now();
        this.listaEntradas = new ArrayList<>(builder.listaEntradas);
        this.listaLineas = new ArrayList<>(builder.listaLineas);
        this.promociones = new ArrayList<>(builder.promociones);
        this.estado = new EstadoPendiente();

        this.subtotal = calcularSubtotal();
        Cotizacion cotizacion = aplicarPromociones();
        this.total = cotizacion.calcularTotal();
        this.descuento = subtotal - total;
        this.descuentosAplicados = cotizacion.getDescuentosAplicados();
    }

    // calculos
    private double calcularSubtotal() {
        double suma = 0;
        for (Entrada e : listaEntradas) suma += e.getPrecio();
        for (LineaCompra l : listaLineas) suma += l.getSubtotal();
        return suma;
    }

    // Decorator: cada promoción aplicable envuelve la cotización anterior
    private Cotizacion aplicarPromociones() {
        Cotizacion cotizacion = new CotizacionBase(subtotal);
        for (Promocion p : promociones) {
            if (p.esAplicable(listaEntradas, subtotal)) {
                cotizacion = p.aplicar(cotizacion);
            }
        }
        return cotizacion;
    }

    // Transiciones: el estado actual valida y devuelve el siguiente.
    // Los datos solo se guardan si la transición fue aceptada.
    public void registrarPago(Pago pago, int puntosGanados) throws CompraInvalidaException {
        if (pago == null) {
            throw new IllegalArgumentException("El pago no puede ser nulo");
        }
        EstadoDeCompra siguiente = estado.pagar();
        this.pago = pago;
        this.puntosGanados = puntosGanados;
        this.estado = siguiente;
    }

    public void registrarReembolso(Reembolso reembolso) throws CompraInvalidaException {
        if (reembolso == null) {
            throw new IllegalArgumentException("El reembolso no puede ser nulo");
        }
        EstadoDeCompra siguiente = estado.reembolsar();
        this.reembolso = reembolso;
        this.estado = siguiente;
    }

    // Descarta una compra que todavía no se ha pagado
    public void cancelar() throws CompraInvalidaException {
        this.estado = estado.cancelar();
    }

    public boolean admiteReembolso() { return estado.admiteReembolso(); }
    public boolean fueCobrada() { return estado.fueCobrada(); }

    // Getters
    public String getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public LocalDateTime getFecha() { return fecha; }
    public List<Entrada> getListaEntradas() { return Collections.unmodifiableList(listaEntradas); }
    public List<LineaCompra> getListaLineas() { return Collections.unmodifiableList(listaLineas); }
    public List<Promocion> getPromociones() { return Collections.unmodifiableList(promociones); }
    public double getSubtotal() { return subtotal; }
    public double getDescuento() { return descuento; }
    public List<DescuentoAplicado> getDescuentosAplicados() { return Collections.unmodifiableList(descuentosAplicados); }
    public double getTotal() { return total; }
    public EstadoCompra getEstado() { return estado.getTipo(); }
    public int getPuntosGanados() { return puntosGanados; }
    public Pago getPago() { return pago; }
    public Reembolso getReembolso() { return reembolso; }

    // Builder
    public static class CompraBuilder {

        private String id = UUID.randomUUID().toString();
        private Cliente cliente;
        private LocalDateTime fecha;   // si no se indica, se usa el momento en que se arma
        private final List<Entrada> listaEntradas = new ArrayList<>();
        private final List<LineaCompra> listaLineas = new ArrayList<>();
        private final List<Promocion> promociones = new ArrayList<>();

        public CompraBuilder conId(String id) { this.id = id; return this; }

        public CompraBuilder conFecha(LocalDateTime fecha) { this.fecha = fecha; return this; }

        public CompraBuilder conCliente(Cliente cliente) { this.cliente = cliente; return this; }

        public CompraBuilder conEntrada(List<Entrada> entradas) {
            this.listaEntradas.clear();
            if (entradas != null) this.listaEntradas.addAll(entradas);
            return this;
        }

        public CompraBuilder agregarEntrada(Entrada entrada) { listaEntradas.add(entrada); return this; }
        public CompraBuilder agregarLinea(LineaCompra linea) { listaLineas.add(linea); return this; }
        public CompraBuilder agregarPromocion(Promocion promocion) { promociones.add(promocion); return this; }

        public Compra build() throws CompraInvalidaException {
            if (cliente == null) {
                throw new CompraInvalidaException("La compra debe tener un cliente");
            }
            if (listaEntradas.isEmpty()) {
                throw new CompraInvalidaException("La compra debe tener al menos una entrada");
            }
            if (id == null || id.isBlank()) {
                throw new CompraInvalidaException("La compra necesita una referencia");
            }
            return new Compra(this);
        }
    }
}