package co.edu.uniquindio.poo.proyectofinalcinemauq.controller;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Asiento;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Funcion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Sala;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.TipoAsiento;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.facade.CinemaFacade;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.TipoEntrada;
import co.edu.uniquindio.poo.proyectofinalcinemauq.util.Formato;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class AsientosController {

    @FXML private Label lblPelicula;
    @FXML private Label lblFecha;
    @FXML private Label lblSala;
    @FXML private Label lblTituloPelicula;
    @FXML private Label lblDetalleFuncion;
    @FXML private GridPane gridAsientos;
    @FXML private FlowPane panelSeleccionados;
    @FXML private ComboBox<TipoEntrada> cbTipoEntrada;
    @FXML private Label lblCantidad;
    @FXML private Label lblPrecioUnitario;
    @FXML private Label lblSubtotal;

    private final CinemaFacade facade = CinemaFacade.getInstance();
    private final SesionCompra sesion = SesionCompra.get();
    private Funcion funcion;

    @FXML
    private void initialize() {
        funcion = sesion.getFuncion();

        lblPelicula.setText(funcion.getPelicula().getTitulo());
        lblFecha.setText(Formato.fechaFuncion(funcion.getFechaFuncion()));
        lblSala.setText(funcion.getSala().getNombre());
        lblTituloPelicula.setText(funcion.getPelicula().getTitulo());
        lblDetalleFuncion.setText(Formato.fechaFuncion(funcion.getFechaFuncion()) + "\n" + funcion.getSala().getNombre()
                + " · " + funcion.getCantidadDisponibles() + " de " + funcion.getSala().getCapacidad() + " libres");

        cbTipoEntrada.setItems(FXCollections.observableArrayList(TipoEntrada.values()));
        cbTipoEntrada.setValue(sesion.getTipoEntrada());
        cbTipoEntrada.valueProperty().addListener((obs, anterior, nuevo) -> {
            sesion.setTipoEntrada(nuevo);
            actualizarResumen();
        });

        construirMapa();
        actualizarResumen();
    }

    // Dibuja un botón por asiento, en la fila y columna que le corresponde
    private void construirMapa() {
        Sala sala = funcion.getSala();
        Set<Character> filas = new TreeSet<>();

        for (Asiento asiento : sala.getListaAsientos()) {
            filas.add(asiento.getFila());
            ToggleButton boton = new ToggleButton(String.valueOf(asiento.getNumero()));
            boton.getStyleClass().add("asiento");
            boton.setTooltip(new Tooltip(asiento.getReferencia()
                    + (asiento.getTipoAsiento() == TipoAsiento.ACCESIBLE ? " · accesible" : "")));

            if (asiento.getTipoAsiento() == TipoAsiento.ACCESIBLE) {
                boton.getStyleClass().add("asiento-accesible");
            }
            if (!funcion.estaDisponible(asiento)) {
                boton.getStyleClass().add("asiento-ocupado");
                boton.setDisable(true);
            }
            boton.setSelected(sesion.getAsientos().contains(asiento));
            boton.selectedProperty().addListener((obs, anterior, seleccionado) -> alternar(boton, asiento, seleccionado));

            gridAsientos.add(boton, asiento.getNumero(), asiento.getFila() - 'A');
        }

        // Letra de cada fila en la primera columna
        for (char fila : filas) {
            Label etiquetaFila = new Label(String.valueOf(fila));
            etiquetaFila.getStyleClass().add("texto-suave");
            gridAsientos.add(etiquetaFila, 0, fila - 'A');
        }
    }

    private void alternar(ToggleButton boton, Asiento asiento, boolean seleccionado) {
        if (seleccionado) {
            if (sesion.getAsientos().size() >= CinemaFacade.MAX_ENTRADAS_POR_COMPRA) {
                boton.setSelected(false);
                Alertas.info("Límite de entradas", "Puedes comprar máximo "
                        + CinemaFacade.MAX_ENTRADAS_POR_COMPRA + " entradas por compra.");
                return;
            }
            if (!sesion.getAsientos().contains(asiento)) sesion.getAsientos().add(asiento);
        } else {
            sesion.getAsientos().remove(asiento);
        }
        actualizarResumen();
    }

    private void actualizarResumen() {
        List<Asiento> ordenados = new ArrayList<>(sesion.getAsientos());
        ordenados.sort(Comparator.comparing(Asiento::getFila).thenComparing(Asiento::getNumero));

        panelSeleccionados.getChildren().clear();
        if (ordenados.isEmpty()) {
            Label ninguno = new Label("Haz clic en los asientos disponibles");
            ninguno.getStyleClass().add("texto-suave");
            panelSeleccionados.getChildren().add(ninguno);
        }
        for (Asiento a : ordenados) {
            Label chip = new Label(a.getReferencia());
            chip.getStyleClass().add("chip");
            panelSeleccionados.getChildren().add(chip);
        }

        double precio = facade.calcularPrecioEntrada(funcion, sesion.getTipoEntrada());
        lblCantidad.setText(ordenados.size() + " × " + sesion.getTipoEntrada());
        lblPrecioUnitario.setText(Formato.moneda(precio) + " c/u");
        lblSubtotal.setText(Formato.moneda(precio * ordenados.size()));
    }

    @FXML
    private void onContinuar() {
        if (sesion.getAsientos().isEmpty()) {
            Alertas.error("Sin asientos", "Selecciona al menos un asiento para continuar.");
            return;
        }
        Navegador.ir("confiteria");
    }

    @FXML
    private void onVolver() {
        sesion.limpiar();
        Navegador.ir("cartelera");
    }
}
