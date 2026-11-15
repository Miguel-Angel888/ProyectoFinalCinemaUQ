package co.edu.uniquindio.poo.proyectofinalcinemauq.controller;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.AcumulacionPuntos;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Cliente;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.facade.CinemaFacade;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.recompensa.Canje;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.recompensa.Recompensa;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.Movimiento;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.Reembolso;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.SolicitudRecarga;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.TarjetaVirtual;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Compra;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.EstadoCompra;
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

    @FXML private Label lblAvisoPuntos;
    @FXML private TableView<Recompensa> tablaRecompensas;
    @FXML private TableColumn<Recompensa, String> colRecNombre;
    @FXML private TableColumn<Recompensa, String> colRecTipo;
    @FXML private TableColumn<Recompensa, String> colRecPuntos;

    @FXML private TableView<Canje> tablaCanjes;
    @FXML private TableColumn<Canje, String> colCjCodigo;
    @FXML private TableColumn<Canje, String> colCjRecompensa;
    @FXML private TableColumn<Canje, String> colCjPuntos;
    @FXML private TableColumn<Canje, String> colCjFecha;
    @FXML private TableColumn<Canje, String> colCjEstado;

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

        colRecNombre.setCellValueFactory(c -> texto(c.getValue().getNombre()));
        colRecTipo.setCellValueFactory(c -> texto(c.getValue().getTipo().toString()));
        colRecPuntos.setCellValueFactory(c -> texto(String.valueOf(c.getValue().getPuntosRequeridos())));
        tablaRecompensas.setPlaceholder(new Label("No hay recompensas disponibles"));

        colCjCodigo.setCellValueFactory(c -> texto(c.getValue().getCodigo()));
        colCjRecompensa.setCellValueFactory(c -> texto(c.getValue().getRecompensa().getNombre()));
        colCjPuntos.setCellValueFactory(c -> texto(String.valueOf(c.getValue().getPuntosUsados())));
        colCjFecha.setCellValueFactory(c -> texto(Formato.fechaHora(c.getValue().getFecha())));
        colCjEstado.setCellValueFactory(c -> texto(c.getValue().getEstado().name()));
        tablaCanjes.setPlaceholder(new Label("Aún no has canjeado puntos"));

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

        int porVencer = facade.getPuntosPorVencer(cliente, 30);
        lblAvisoPuntos.setText(porVencer > 0
                ? "Tienes " + porVencer + " puntos que vencen en los próximos 30 días. ¡Úsalos pronto!"
                : "Tus puntos duran " + AcumulacionPuntos.MESES_VIGENCIA + " meses. Al canjear se usan primero los que vencen antes.");
        tablaRecompensas.setItems(FXCollections.observableArrayList(facade.getCatalogoRecompensas()));
        List<Canje> canjes = new ArrayList<>(cliente.getListaCanjes());
        Collections.reverse(canjes);
        tablaCanjes.setItems(FXCollections.observableArrayList(canjes));

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
    private void onCanjear() {
        Recompensa recompensa = tablaRecompensas.getSelectionModel().getSelectedItem();
        if (recompensa == null) {
            Alertas.error("Selecciona una recompensa", "Elige en la tabla lo que quieres canjear.");
            return;
        }
        if (!Alertas.confirmar("Confirmar canje", "¿Canjear " + recompensa.getNombre() + " por "
                + recompensa.getPuntosRequeridos() + " puntos?")) {
            return;
        }
        try {
            Canje canje = facade.canjearPuntos(recompensa);
            Alertas.info("¡Canje exitoso!", "Canjeaste " + recompensa.getNombre() + ".\nCódigo: " + canje.getCodigo());
            refrescar();
        } catch (CinemaUQException e) {
            Alertas.error("No se pudo canjear", e.getMessage());
        }
    }

    @FXML
    private void onCancelarCompra() {
        Compra compra = tablaCompras.getSelectionModel().getSelectedItem();
        if (compra == null) {
            Alertas.error("Selecciona una compra", "Elige en la tabla la compra que quieres cancelar.");
            return;
        }
        double porcentaje = facade.getPorcentajeReembolso(compra);
        if (porcentaje == 0) {
            Alertas.error("No se puede cancelar", compra.getEstado() != EstadoCompra.PAGADA
                    ? "Solo se pueden cancelar compras pagadas."
                    : "Faltan menos de 2 horas para la función o ya empezó.");
            return;
        }
        double monto = compra.getTotal() * porcentaje / 100;
        if (!Alertas.confirmar("Cancelar compra", "Se te devolverá el " + (int) porcentaje + " % ("
                + Formato.moneda(monto) + ") a tu tarjeta y se descontarán los " + compra.getPuntosGanados()
                + " puntos que ganaste con esta compra. ¿Continuar?")) {
            return;
        }
        try {
            Reembolso reembolso = facade.cancelarCompra(compra);
            Alertas.info("Compra cancelada", "Reembolso " + reembolso.getReferencia() + " por "
                    + Formato.moneda(reembolso.getCantidad()) + ".");
            refrescar();
        } catch (CinemaUQException e) {
            Alertas.error("No se pudo cancelar", e.getMessage());
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
