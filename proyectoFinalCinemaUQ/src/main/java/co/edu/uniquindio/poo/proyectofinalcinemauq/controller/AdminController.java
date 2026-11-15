package co.edu.uniquindio.poo.proyectofinalcinemauq.controller;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Cliente;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.facade.CinemaFacade;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.recompensa.Canje;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.SolicitudRecarga;
import co.edu.uniquindio.poo.proyectofinalcinemauq.util.Formato;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class AdminController {

    @FXML private Label lblAdmin;

    @FXML private TableView<SolicitudRecarga> tablaSolicitudes;
    @FXML private TableColumn<SolicitudRecarga, String> colSolCliente;
    @FXML private TableColumn<SolicitudRecarga, String> colSolTarjeta;
    @FXML private TableColumn<SolicitudRecarga, String> colSolSaldo;
    @FXML private TableColumn<SolicitudRecarga, String> colSolMonto;
    @FXML private TableColumn<SolicitudRecarga, String> colSolFecha;

    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, String> colCliNombre;
    @FXML private TableColumn<Cliente, String> colCliCorreo;
    @FXML private TableColumn<Cliente, String> colCliTarjeta;
    @FXML private TableColumn<Cliente, String> colCliSaldo;
    @FXML private TableColumn<Cliente, String> colCliPuntos;
    @FXML private TableColumn<Cliente, String> colCliCompras;

    @FXML private TableView<Canje> tablaVales;
    @FXML private TableColumn<Canje, String> colValCliente;
    @FXML private TableColumn<Canje, String> colValCodigo;
    @FXML private TableColumn<Canje, String> colValRecompensa;
    @FXML private TableColumn<Canje, String> colValFecha;

    private final CinemaFacade facade = CinemaFacade.getInstance();

    @FXML
    private void initialize() {
        lblAdmin.setText(facade.getUsuarioActual().getNombre());

        colSolCliente.setCellValueFactory(c -> texto(c.getValue().getCliente().getNombre()));
        colSolTarjeta.setCellValueFactory(c -> texto(c.getValue().getCliente().getTarjetaVirtual().getNumeroTarjeta()));
        colSolSaldo.setCellValueFactory(c -> texto(Formato.moneda(c.getValue().getCliente().getTarjetaVirtual().getSaldo())));
        colSolMonto.setCellValueFactory(c -> texto(Formato.moneda(c.getValue().getCantidad())));
        colSolFecha.setCellValueFactory(c -> texto(Formato.fechaHora(c.getValue().getFechaSolicitud())));
        tablaSolicitudes.setPlaceholder(new Label("No hay solicitudes pendientes"));

        colCliNombre.setCellValueFactory(c -> texto(c.getValue().getNombre()));
        colCliCorreo.setCellValueFactory(c -> texto(c.getValue().getCorreo()));
        colCliTarjeta.setCellValueFactory(c -> texto(c.getValue().getTarjetaVirtual().getNumeroTarjeta()));
        colCliSaldo.setCellValueFactory(c -> texto(Formato.moneda(c.getValue().getTarjetaVirtual().getSaldo())));
        colCliPuntos.setCellValueFactory(c -> texto(String.valueOf(facade.getPuntosDisponibles(c.getValue()))));
        colCliCompras.setCellValueFactory(c -> texto(String.valueOf(c.getValue().getListaCompras().size())));

        colValCliente.setCellValueFactory(c -> texto(c.getValue().getCliente().getNombre()));
        colValCodigo.setCellValueFactory(c -> texto(c.getValue().getCodigo()));
        colValRecompensa.setCellValueFactory(c -> texto(c.getValue().getRecompensa().getNombre()));
        colValFecha.setCellValueFactory(c -> texto(Formato.fechaHora(c.getValue().getFecha())));
        tablaVales.setPlaceholder(new Label("No hay vales pendientes"));

        refrescar();
    }

    private void refrescar() {
        tablaSolicitudes.setItems(FXCollections.observableArrayList(facade.getSolicitudesPendientes()));
        tablaClientes.setItems(FXCollections.observableArrayList(facade.getClientes()));
        tablaClientes.refresh();
        tablaVales.setItems(FXCollections.observableArrayList(facade.getValesConfiteriaPendientes()));
    }

    @FXML
    private void onAprobar() {
        SolicitudRecarga solicitud = tablaSolicitudes.getSelectionModel().getSelectedItem();
        if (solicitud == null) {
            Alertas.error("Selecciona una solicitud", "Elige la solicitud que quieres aprobar.");
            return;
        }
        try {
            facade.aprobarRecarga(solicitud);
            Alertas.info("Recarga realizada", "Se recargaron " + Formato.moneda(solicitud.getCantidad())
                    + " a la tarjeta de " + solicitud.getCliente().getNombre() + ".");
        } catch (CinemaUQException e) {
            Alertas.error("No se pudo aprobar", e.getMessage());
        }
        refrescar();
    }

    @FXML
    private void onRechazar() {
        SolicitudRecarga solicitud = tablaSolicitudes.getSelectionModel().getSelectedItem();
        if (solicitud == null) {
            Alertas.error("Selecciona una solicitud", "Elige la solicitud que quieres rechazar.");
            return;
        }
        try {
            facade.rechazarRecarga(solicitud);
        } catch (CinemaUQException e) {
            Alertas.error("No se pudo rechazar", e.getMessage());
        }
        refrescar();
    }

    @FXML
    private void onEntregarVale() {
        Canje vale = tablaVales.getSelectionModel().getSelectedItem();
        if (vale == null) {
            Alertas.error("Selecciona un vale", "Elige el vale que ya entregaste en la confitería.");
            return;
        }
        try {
            facade.marcarCanjeUsado(vale);
        } catch (CinemaUQException e) {
            Alertas.error("No se pudo marcar el vale", e.getMessage());
        }
        refrescar();
    }

    @FXML
    private void onCerrarSesion() {
        facade.cerrarSesion();
        Navegador.ir("login");
    }

    private static SimpleStringProperty texto(String valor) {
        return new SimpleStringProperty(valor);
    }
}
