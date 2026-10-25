package co.edu.uniquindio.poo.proyectofinalcinemauq.controller;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Funcion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException.AsientoNoDisponibleException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException.SaldoInsuficienteException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Cliente;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.promocion.DescuentoAplicado;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.facade.CinemaFacade;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.MedioPago;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.Pago;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Compra;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Entrada;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.LineaCompra;
import co.edu.uniquindio.poo.proyectofinalcinemauq.util.Formato;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class ResumenController {

    @FXML private VBox contenedorDetalle;
    @FXML private Label lblNumeroTarjeta;
    @FXML private Label lblSaldoActual;
    @FXML private Label lblSaldoDespues;
    @FXML private Label lblPuntos;
    @FXML private Label lblTotal;
    @FXML private Button btnPagar;

    private final CinemaFacade facade = CinemaFacade.getInstance();
    private final SesionCompra sesion = SesionCompra.get();
    private Compra compra;

    @FXML
    private void initialize() {
        try {
            compra = facade.prepararCompra(sesion.getFuncion(), sesion.getAsientos(),
                    sesion.getTipoEntrada(), sesion.getConfiteria());
        } catch (CinemaUQException e) {
            btnPagar.setDisable(true);
            Label error = new Label("No se pudo preparar la compra: " + e.getMessage());
            error.getStyleClass().add("error");
            error.setWrapText(true);
            contenedorDetalle.getChildren().add(error);
            return;
        }
        mostrarDetalle();
        mostrarPago();
    }

    private void mostrarDetalle() {
        Funcion funcion = compra.getListaEntradas().get(0).getFuncion();
        Label titulo = new Label(funcion.getPelicula().getTitulo());
        titulo.getStyleClass().add("titulo");
        Label detalle = new Label(Formato.fechaFuncion(funcion.getFechaFuncion()) + " · " + funcion.getSala().getNombre());
        detalle.getStyleClass().add("subtitulo");
        contenedorDetalle.getChildren().addAll(titulo, detalle, seccion("ENTRADAS"));

        for (Entrada e : compra.getListaEntradas()) {
            contenedorDetalle.getChildren().add(fila("Asiento " + e.getAsiento().getReferencia() + " · " + e.getTipoEntrada(),
                    Formato.moneda(e.getPrecio()), null));
        }

        contenedorDetalle.getChildren().add(seccion("CONFITERÍA"));
        if (compra.getListaLineas().isEmpty()) {
            Label sin = new Label("Sin productos");
            sin.getStyleClass().add("texto-suave");
            contenedorDetalle.getChildren().add(sin);
        }
        for (LineaCompra l : compra.getListaLineas()) {
            contenedorDetalle.getChildren().add(fila(l.getCantidad() + " × " + l.getItem().getNombre(),
                    Formato.moneda(l.getSubtotal()), null));
        }

        contenedorDetalle.getChildren().addAll(new Separator(), fila("Subtotal", Formato.moneda(compra.getSubtotal()), null));
        for (DescuentoAplicado d : compra.getDescuentosAplicados()) {
            contenedorDetalle.getChildren().add(fila("Promoción: " + d.getNombre(),
                    "- " + Formato.moneda(d.getValor()), "descuento"));
        }
        contenedorDetalle.getChildren().addAll(new Separator(), fila("TOTAL A PAGAR", Formato.moneda(compra.getTotal()), "item-titulo"));
    }

    private void mostrarPago() {
        Cliente cliente = Navegador.clienteActual();
        MedioPago tarjeta = facade.getMedioPago(cliente);
        lblNumeroTarjeta.setText(tarjeta.getNumeroTarjeta() + " · " + cliente.getTarjetaVirtual().getEstadoTarjeta());
        lblSaldoActual.setText(Formato.moneda(tarjeta.getSaldo()));
        lblTotal.setText(Formato.moneda(compra.getTotal()));

        if (tarjeta.puedePagar(compra.getTotal())) {
            lblSaldoDespues.setText(Formato.moneda(tarjeta.getSaldo() - compra.getTotal()));
        } else {
            lblSaldoDespues.setText("Saldo insuficiente");
            lblSaldoDespues.getStyleClass().add("error");
        }
        lblPuntos.setText("Ganarás " + facade.calcularPuntos(compra.getTotal()) + " puntos con esta compra.\n"
                + "Disponibles hoy: " + facade.getPuntosDisponibles(cliente));
    }

    @FXML
    private void onPagar() {
        try {
            Pago pago = facade.pagarCompra(compra);
            Alertas.info("¡Compra exitosa!",
                    "Referencia del pago: " + pago.getReferencia()
                            + "\nTotal pagado: " + Formato.moneda(pago.getCantidad())
                            + "\nPuntos ganados: " + compra.getPuntosGanados()
                            + "\nNuevo saldo: " + Formato.moneda(pago.getTarjeta().getSaldo()));
            sesion.limpiar();
            Navegador.ir("cartelera");
        } catch (SaldoInsuficienteException e) {
            if (Alertas.confirmar("Saldo insuficiente", e.getMessage() + "\n\n¿Quieres ir a tu tarjeta para solicitar una recarga?")) {
                Navegador.ir("tarjeta");
            }
        } catch (AsientoNoDisponibleException e) {
            Alertas.error("Asiento no disponible", e.getMessage() + "\nElige otro asiento.");
            sesion.getAsientos().removeIf(a -> !sesion.getFuncion().estaDisponible(a));
            Navegador.ir("asientos");
        } catch (CinemaUQException e) {
            Alertas.error("No se pudo completar el pago", e.getMessage());
        }
    }

    @FXML
    private void onVolver() {
        Navegador.ir("confiteria");
    }

    @FXML
    private void onCancelar() {
        if (Alertas.confirmar("Cancelar compra", "Se perderán los asientos y productos seleccionados. ¿Continuar?")) {
            sesion.limpiar();
            Navegador.ir("cartelera");
        }
    }

    private Label seccion(String texto) {
        Label l = new Label(texto);
        l.getStyleClass().add("seccion");
        l.setPadding(new javafx.geometry.Insets(10, 0, 0, 0));
        return l;
    }

    private HBox fila(String izquierda, String derecha, String estilo) {
        Label izq = new Label(izquierda);
        Label der = new Label(derecha);
        if (estilo != null) {
            izq.getStyleClass().add(estilo);
            der.getStyleClass().add(estilo);
        }
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        return new HBox(izq, espacio, der);
    }
}
