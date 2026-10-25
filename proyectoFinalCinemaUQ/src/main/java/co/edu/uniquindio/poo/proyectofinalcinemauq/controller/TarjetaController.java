package co.edu.uniquindio.poo.proyectofinalcinemauq.controller;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.AcumulacionPuntos;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Cliente;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.facade.CinemaFacade;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.Movimiento;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.SolicitudRecarga;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.TarjetaVirtual;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Compra;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Entrada;
import co.edu.uniquindio.poo.proyectofinalcinemauq.util.Formato;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TarjetaController {

    @FXML private Label lblSaldo;
    @FXML private Label lblNumero;
    @FXML private Label lblEstado;
    @FXML private Label lblPuntos;
    @FXML private Button btnVolver;
    @FXML private TextField txtMontoRecarga;

    @FXML private TableView<Movimiento> tablaMovimientos;
    @FXML private TableColumn<Movimiento, String> colMovFecha;
    @FXML private TableColumn<Movimiento, String> colMovTipo;
    @FXML private TableColumn<Movimiento, String> colMovDescripcion;
    @FXML private TableColumn<Movimiento, String> colMovMonto;
    @FXML private TableColumn<Movimiento, String> colMovSaldo;

    @FXML private TableView<SolicitudRecarga> tablaSolicitudes;
    @FXML private TableColumn<SolicitudRecarga, String> colSolReferencia;
    @FXML private TableColumn<SolicitudRecarga, String> colSolFecha;
    @FXML private TableColumn<SolicitudRecarga, String> colSolMonto;
    @FXML private TableColumn<SolicitudRecarga, String> colSolEstado;

    @FXML private TableView<AcumulacionPuntos> tablaPuntos;
    @FXML private TableColumn<AcumulacionPuntos, String> colPtsObtenidos;
    @FXML private TableColumn<AcumulacionPuntos, String> colPtsRestantes;
    @FXML private TableColumn<AcumulacionPuntos, String> colPtsFecha;
    @FXML private TableColumn<AcumulacionPuntos, String> colPtsVence;
    @FXML private TableColumn<AcumulacionPuntos, String> colPtsEstado;

    @FXML private TableView<Compra> tablaCompras;
    @FXML private TableColumn<Compra, String> colCmpFecha;
    @FXML private TableColumn<Compra, String> colCmpPelicula;
    @FXML private TableColumn<Compra, String> colCmpAsientos;
    @FXML private TableColumn<Compra, String> colCmpTotal;
    @FXML private TableColumn<Compra, String> colCmpPuntos;
    @FXML private TableColumn<Compra, String> colCmpEstado;

    private final CinemaFacade facade = CinemaFacade.getInstance();
    private Cliente cliente;

    @FXML
    private void initialize() {
        cliente = Navegador.clienteActual();
        if (SesionCompra.get().hayCompraEnCurso()) {
            btnVolver.setText("← Volver a mi compra");
        }

        colMovFecha.setCellValueFactory(c -> texto(Formato.fechaHora(c.getValue().getFechaMovimiento())));
        colMovTipo.setCellValueFactory(c -> texto(c.getValue().getTipoMovimiento().name()));
        colMovDescripcion.setCellValueFactory(c -> texto(c.getValue().getDescripcion()));
        colMovMonto.setCellValueFactory(c -> texto((c.getValue().esIngreso() ? "+ " : "- ")
                + Formato.moneda(c.getValue().getCantidad())));
        colMovSaldo.setCellValueFactory(c -> texto(Formato.moneda(c.getValue().getSaldoResultante())));
        tablaMovimientos.setPlaceholder(new Label("Aún no hay movimientos"));

        colSolReferencia.setCellValueFactory(c -> texto(c.getValue().getReferencia()));
        colSolFecha.setCellValueFactory(c -> texto(Formato.fechaHora(c.getValue().getFechaSolicitud())));
        colSolMonto.setCellValueFactory(c -> texto(Formato.moneda(c.getValue().getCantidad())));
        colSolEstado.setCellValueFactory(c -> texto(c.getValue().getEstadoSolicitud().name()));
        tablaSolicitudes.setPlaceholder(new Label("No has solicitado recargas"));

        LocalDate hoy = LocalDate.now();
        colPtsObtenidos.setCellValueFactory(c -> texto(String.valueOf(c.getValue().getPuntosObtenidos())));
        colPtsRestantes.setCellValueFactory(c -> texto(String.valueOf(c.getValue().getPuntosDisponibles())));
        colPtsFecha.setCellValueFactory(c -> texto(Formato.fecha(c.getValue().getFechaObtencion())));
        colPtsVence.setCellValueFactory(c -> texto(Formato.fecha(c.getValue().getFechaVencimiento())));
        colPtsEstado.setCellValueFactory(c -> texto(c.getValue().estaVencida(hoy) ? "Vencido"
                : c.getValue().getPuntosDisponibles() == 0 ? "Usado" : "Vigente"));
        tablaPuntos.setPlaceholder(new Label("Aún no has acumulado puntos"));

        colCmpFecha.setCellValueFactory(c -> texto(Formato.fechaHora(c.getValue().getFecha())));
        colCmpPelicula.setCellValueFactory(c -> texto(c.getValue().getListaEntradas().get(0).getFuncion().getPelicula().getTitulo()));
        colCmpAsientos.setCellValueFactory(c -> {
            List<String> refs = new ArrayList<>();
            for (Entrada e : c.getValue().getListaEntradas()) refs.add(e.getAsiento().getReferencia());
            return texto(String.join(", ", refs));
        });
        colCmpTotal.setCellValueFactory(c -> texto(Formato.moneda(c.getValue().getTotal())));
        colCmpPuntos.setCellValueFactory(c -> texto(String.valueOf(c.getValue().getPuntosGanados())));
        colCmpEstado.setCellValueFactory(c -> texto(c.getValue().getEstado().name()));
        tablaCompras.setPlaceholder(new Label("Aún no has realizado compras"));

        refrescar();
    }

    private void refrescar() {
        TarjetaVirtual tarjeta = cliente.getTarjetaVirtual();
        lblSaldo.setText(Formato.moneda(tarjeta.getSaldo()));
        lblNumero.setText(tarjeta.getNumeroTarjeta());
        lblEstado.setText(tarjeta.getEstadoTarjeta().name());
        lblPuntos.setText(String.valueOf(facade.getPuntosDisponibles(cliente)));

        // Lo más reciente primero
        List<Movimiento> movimientos = new ArrayList<>(tarjeta.getListaMovimientos());
        Collections.reverse(movimientos);
        tablaMovimientos.setItems(FXCollections.observableArrayList(movimientos));

        List<SolicitudRecarga> solicitudes = facade.getSolicitudesCliente(cliente);
        Collections.reverse(solicitudes);
        tablaSolicitudes.setItems(FXCollections.observableArrayList(solicitudes));

        tablaPuntos.setItems(FXCollections.observableArrayList(cliente.getPuntos().getListaAcumulaciones()));

        List<Compra> compras = new ArrayList<>(cliente.getListaCompras());
        Collections.reverse(compras);
        tablaCompras.setItems(FXCollections.observableArrayList(compras));
    }

    @FXML
    private void onSolicitarRecarga() {
        double monto;
        try {
            monto = Double.parseDouble(txtMontoRecarga.getText().replace(".", "").replace(",", "").replace("$", "").trim());
        } catch (NumberFormatException e) {
            Alertas.error("Monto inválido", "Escribe el monto solo con números, por ejemplo 50000.");
            return;
        }
        try {
            SolicitudRecarga solicitud = facade.solicitarRecarga(monto);
            Alertas.info("Solicitud enviada", "Tu solicitud " + solicitud.getReferencia() + " por "
                    + Formato.moneda(monto) + " quedó pendiente de aprobación por el administrador.");
            txtMontoRecarga.clear();
            refrescar();
        } catch (CinemaUQException e) {
            Alertas.error("No se pudo solicitar la recarga", e.getMessage());
        }
    }

    @FXML
    private void onVolver() {
        Navegador.ir(SesionCompra.get().hayCompraEnCurso() ? "resumen" : "cartelera");
    }

    private static SimpleStringProperty texto(String valor) {
        return new SimpleStringProperty(valor);
    }
}
