package co.edu.uniquindio.poo.proyectofinalcinemauq.controller;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Funcion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Pelicula;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Cliente;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.promocion.Promocion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.facade.CinemaFacade;
import co.edu.uniquindio.poo.proyectofinalcinemauq.util.Formato;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class CarteleraController {

    @FXML private Label lblUsuario;
    @FXML private Label lblSaldo;
    @FXML private ListView<Pelicula> listaPeliculas;
    @FXML private Label lblTitulo;
    @FXML private Label lblDetalles;
    @FXML private ListView<Funcion> listaFunciones;
    @FXML private Label lblPromociones;
    @FXML private Button btnSeleccionarAsientos;

    private final CinemaFacade facade = CinemaFacade.getInstance();

    @FXML
    private void initialize() {
        Cliente cliente = Navegador.clienteActual();
        lblUsuario.setText("Hola, " + cliente.getNombre());
        lblSaldo.setText("Saldo " + Formato.moneda(cliente.getTarjetaVirtual().getSaldo()));

        listaPeliculas.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Pelicula p, boolean vacio) {
                super.updateItem(p, vacio);
                if (vacio || p == null) {
                    setGraphic(null);
                    return;
                }
                Label titulo = new Label(p.getTitulo());
                titulo.getStyleClass().add("item-titulo");
                Label sub = new Label(p.getGenero() + " · " + p.getDuracionMinutos() + " min · " + p.getClasificacion());
                sub.getStyleClass().add("texto-suave");
                setGraphic(new VBox(2, titulo, sub));
            }
        });

        listaFunciones.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Funcion f, boolean vacio) {
                super.updateItem(f, vacio);
                if (vacio || f == null) {
                    setText(null);
                    return;
                }
                setText(Formato.fechaFuncion(f.getFechaFuncion()) + "   |   " + f.getSala().getNombre()
                        + "   |   desde " + Formato.moneda(f.getPrecioBase())
                        + "   |   " + f.getCantidadDisponibles() + " asientos libres");
            }
        });
        listaFunciones.setPlaceholder(new Label("No hay funciones programadas para esta película"));

        listaPeliculas.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, nueva) -> mostrarPelicula(nueva));
        listaFunciones.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, nueva) -> btnSeleccionarAsientos.setDisable(nueva == null));

        List<String> nombresPromo = new ArrayList<>();
        for (Promocion p : facade.getPromociones()) nombresPromo.add(p.getNombre());
        lblPromociones.setText(nombresPromo.isEmpty() ? "" : "Promociones vigentes: " + String.join("  ·  ", nombresPromo));

        listaPeliculas.setItems(FXCollections.observableArrayList(facade.getPeliculasEnCartelera()));
        btnSeleccionarAsientos.setDisable(true);
        listaPeliculas.getSelectionModel().selectFirst();
    }

    private void mostrarPelicula(Pelicula p) {
        if (p == null) return;
        lblTitulo.setText(p.getTitulo());
        lblDetalles.setText(p.getGenero() + "  ·  " + p.getDuracionMinutos() + " min  ·  Clasificación " + p.getClasificacion());
        listaFunciones.setItems(FXCollections.observableArrayList(facade.getFuncionesDisponibles(p)));
    }

    @FXML
    private void onSeleccionarAsientos() {
        Funcion funcion = listaFunciones.getSelectionModel().getSelectedItem();
        if (funcion == null) {
            Alertas.error("Selecciona una función", "Elige el horario que quieres ver.");
            return;
        }
        try {
            facade.validarHorarioDisponible(funcion);
        } catch (CinemaUQException e) {
            Alertas.error("Horario no disponible", e.getMessage());
            return;
        }
        SesionCompra.get().iniciar(funcion);
        Navegador.ir("asientos");
    }

    @FXML
    private void onMiTarjeta() {
        Navegador.ir("tarjeta");
    }

    @FXML
    private void onCerrarSesion() {
        facade.cerrarSesion();
        SesionCompra.get().limpiar();
        Navegador.ir("login");
    }
}
