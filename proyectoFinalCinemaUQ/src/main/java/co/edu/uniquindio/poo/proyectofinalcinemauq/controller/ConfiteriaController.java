package co.edu.uniquindio.poo.proyectofinalcinemauq.controller;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.facade.CinemaFacade;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Combo;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.ItemConfiteria;
import co.edu.uniquindio.poo.proyectofinalcinemauq.util.Formato;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Map;

public class ConfiteriaController {

    @FXML private Label lblResumenEntradas;
    @FXML private ListView<ItemConfiteria> listaItems;
    @FXML private Spinner<Integer> spCantidad;
    @FXML private ListView<ItemConfiteria> listaPedido;
    @FXML private Label lblTotalConfiteria;

    private final CinemaFacade facade = CinemaFacade.getInstance();
    private final SesionCompra sesion = SesionCompra.get();

    @FXML
    private void initialize() {
        lblResumenEntradas.setText(sesion.getFuncion().getPelicula().getTitulo() + "  ·  "
                + sesion.getAsientos().size() + " entrada(s)");

        // La celda funciona igual para un producto o un combo: solo usa la interfaz ItemConfiteria
        listaItems.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ItemConfiteria item, boolean vacio) {
                super.updateItem(item, vacio);
                if (vacio || item == null) {
                    setGraphic(null);
                    return;
                }
                Label nombre = new Label(item.getNombre());
                nombre.getStyleClass().add("item-titulo");
                HBox encabezado = new HBox(8, nombre);
                if (item instanceof Combo combo) {
                    Label badge = new Label("COMBO -" + (int) combo.getPorcentajeDescuento() + "%");
                    badge.getStyleClass().add("badge");
                    encabezado.getChildren().add(badge);
                }
                if (!item.hayStock(1)) {
                    Label agotado = new Label("AGOTADO");
                    agotado.getStyleClass().add("error");
                    encabezado.getChildren().add(agotado);
                }
                Region espacio = new Region();
                HBox.setHgrow(espacio, Priority.ALWAYS);
                Label precio = new Label(Formato.moneda(item.getPrecio()));
                precio.getStyleClass().add("precio");
                encabezado.getChildren().addAll(espacio, precio);

                Label descripcion = new Label(item.getDescripcion());
                descripcion.getStyleClass().add("texto-suave");
                descripcion.setWrapText(true);
                setGraphic(new VBox(3, encabezado, descripcion));
            }
        });

        listaPedido.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ItemConfiteria item, boolean vacio) {
                super.updateItem(item, vacio);
                if (vacio || item == null) {
                    setText(null);
                    return;
                }
                int cantidad = sesion.getConfiteria().getOrDefault(item, 0);
                setText(cantidad + " × " + item.getNombre() + "   " + Formato.moneda(item.getPrecio() * cantidad));
            }
        });
        listaPedido.setPlaceholder(new Label("Aún no has agregado productos"));

        spCantidad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
        listaItems.setItems(FXCollections.observableArrayList(facade.getConfiteria()));
        refrescarPedido();
    }

    @FXML
    private void onAgregar() {
        ItemConfiteria item = listaItems.getSelectionModel().getSelectedItem();
        if (item == null) {
            Alertas.error("Selecciona un producto", "Elige un producto o combo de la lista.");
            return;
        }
        int nuevaCantidad = sesion.getConfiteria().getOrDefault(item, 0) + spCantidad.getValue();
        if (!item.hayStock(nuevaCantidad)) {
            Alertas.error("Stock insuficiente", "No hay unidades suficientes de " + item.getNombre() + ".");
            return;
        }
        sesion.getConfiteria().put(item, nuevaCantidad);
        refrescarPedido();
    }

    @FXML
    private void onQuitar() {
        ItemConfiteria item = listaPedido.getSelectionModel().getSelectedItem();
        if (item != null) {
            sesion.getConfiteria().remove(item);
            refrescarPedido();
        }
    }

    private void refrescarPedido() {
        listaPedido.setItems(FXCollections.observableArrayList(sesion.getConfiteria().keySet()));
        listaPedido.refresh();
        double total = 0;
        for (Map.Entry<ItemConfiteria, Integer> e : sesion.getConfiteria().entrySet()) {
            total += e.getKey().getPrecio() * e.getValue();
        }
        lblTotalConfiteria.setText(Formato.moneda(total));
    }

    @FXML
    private void onContinuar() {
        Navegador.ir("resumen");
    }

    @FXML
    private void onVolver() {
        Navegador.ir("asientos");
    }
}
