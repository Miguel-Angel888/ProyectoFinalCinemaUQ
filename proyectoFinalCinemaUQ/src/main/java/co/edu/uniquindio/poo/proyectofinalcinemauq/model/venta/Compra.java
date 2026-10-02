package co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Cliente;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.Pago;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.Reembolso;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Compra {

    //Atributos
    private String id;
    private Cliente cliente;
    private LocalDateTime fecha;
    private List<Entrada> entradas ;
    private List<Promocion> promociones ;
    private double subtotal;
    private double descuento ;
    private double total;
    private EstadoCompra estado ;
    private int puntosGanados;
    private Pago pago;
    private Reembolso reembolso;

    private Compra(CompraBuilder builder) {
        this.id = builder.id;
        this.cliente = builder.cliente;
        this.fecha = LocalDateTime.now();
        this.entradas = new ArrayList<>(builder.entradas);
        this.promociones = new ArrayList<>();
        this.estado = EstadoCompra.PENDIENTE;
    }

    //Getters

    public String getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public List<Entrada> getEntradas() {
        return entradas;
    }

    public List<Promocion> getPromociones() {
        return promociones;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getDescuento() {
        return descuento;
    }

    public double getTotal() {
        return total;
    }

    public EstadoCompra getEstado() {
        return estado;
    }

    public int getPuntosGanados() {
        return puntosGanados;
    }

    public Pago getPago() {
        return pago;
    }

    public Reembolso getReembolso() {
        return reembolso;
    }

    //Clase Builder
    public static class CompraBuilder {

        private String id;
        private Cliente cliente;
        private List<Entrada> listaEntradas;



        public CompraBuilder conId(String id) {
            this.id = id;
            return this;
        }

        public CompraBuilder conCliente(Cliente cliente) {
            this.cliente = cliente;
            return this;
        }

        public CompraBuilder conEntrada(List<Entrada> listaEntradas){
            this.listaEntradas = listaEntradas;
            return this;
        }



        //Metodo build
        public Compra build() {
            // VALIDACIÓN 1: cliente
            if (cliente == null) {
                throw new IllegalStateException("La compra debe tener un cliente");
            }
            if (listaEntradas == null) {
                throw new IllegalStateException("La compra debe tener entradas");
            }

            // La compra necesita una referncia
            if (id == null) {
                throw new IllegalStateException("La compra necesita una referencia");
            }
            return new Compra(this);
        }



    }


}
