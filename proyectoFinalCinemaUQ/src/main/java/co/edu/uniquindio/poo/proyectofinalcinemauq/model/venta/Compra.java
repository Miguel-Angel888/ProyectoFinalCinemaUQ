package co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException.CompraInvalidaException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Cliente;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.promocion.Cotizacion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.promocion.CotizacionBase;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.promocion.DescuentoAplicado;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.promocion.Promocion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.Pago;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.Reembolso;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
    private EstadoCompra estado;
    private int puntosGanados;
    private Pago pago;
    private Reembolso reembolso;

    private Compra(CompraBuilder builder) {
        this.id = builder.id;
        this.cliente = builder.cliente;
        this.fecha = LocalDateTime.now();
        this.listaEntradas = new ArrayList<>(builder.listaEntradas);
        this.listaLineas = new ArrayList<>(builder.listaLineas);
        this.promociones = new ArrayList<>(builder.promociones);
        this.estado = EstadoCompra.PENDIENTE;

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

    // registros
    public void registrarPago(Pago pago, int puntosGanados) throws CompraInvalidaException {
        if (estado != EstadoCompra.PENDIENTE) {
            throw new CompraInvalidaException("Solo se puede pagar una compra pendiente");
        }
        this.pago = pago;
        this.puntosGanados = puntosGanados;
        this.estado = EstadoCompra.PAGADA;
    }

    public void registrarReembolso(Reembolso reembolso) throws CompraInvalidaException {
        if (estado != EstadoCompra.PAGADA) {
            throw new CompraInvalidaException("Solo se puede cancelar una compra pagada");
        }
        if (this.reembolso != null) {
            throw new CompraInvalidaException("Esta compra ya fue reembolsada");
        }
        this.reembolso = reembolso;
        this.estado = EstadoCompra.CANCELADA;
    }

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
    public EstadoCompra getEstado() { return estado; }
    public int getPuntosGanados() { return puntosGanados; }
    public Pago getPago() { return pago; }
    public Reembolso getReembolso() { return reembolso; }

    // Builder
    public static class CompraBuilder {

        private String id = UUID.randomUUID().toString();
        private Cliente cliente;
        private final List<Entrada> listaEntradas = new ArrayList<>();
        private final List<LineaCompra> listaLineas = new ArrayList<>();
        private final List<Promocion> promociones = new ArrayList<>();

        public CompraBuilder conId(String id) { this.id = id; return this; }

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