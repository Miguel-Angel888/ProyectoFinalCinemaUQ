package co.edu.uniquindio.poo.proyectofinalcinemauq.controller;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.facade.CinemaFacade;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte.DatosReporte;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte.FormatoReporte;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte.SerieGrafico;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte.TipoReporte;
import co.edu.uniquindio.poo.proyectofinalcinemauq.util.Formato;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

// Pantalla de reportes del administrador: consulta un periodo, muestra el gráfico
// (barras, pastel o líneas según el reporte), la tabla de registros y los totales,
// y exporta el mismo resultado a TXT o PDF.
public class ReportesController {

    @FXML private ListView<TipoReporte> listaTipos;
    @FXML private DatePicker dpDesde;
    @FXML private DatePicker dpHasta;
    @FXML private Label lblTitulo;
    @FXML private Label lblDetalle;
    @FXML private Label lblRegistros;
    @FXML private StackPane panelGrafico;
    @FXML private TableView<List<String>> tablaDatos;
    @FXML private GridPane gridTotales;

    private final CinemaFacade facade = CinemaFacade.getInstance();
    private DatosReporte datosActuales;

    @FXML
    private void initialize() {
        listaTipos.setItems(FXCollections.observableArrayList(TipoReporte.values()));
        LocalDate hoy = LocalDate.now();
        dpDesde.setValue(hoy.minusDays(29));
        dpHasta.setValue(hoy);
        tablaDatos.setPlaceholder(new Label("No se encontraron registros en el periodo"));
        tablaDatos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        listaTipos.getSelectionModel().selectedItemProperty().addListener((obs, anterior, nuevo) -> {
            if (nuevo != null) generar();
        });
        listaTipos.getSelectionModel().selectFirst();
    }

    @FXML
    private void onGenerar() {
        generar();
    }

    @FXML private void onUltimos7() { usarUltimosDias(7); }
    @FXML private void onUltimos30() { usarUltimosDias(30); }
    @FXML private void onUltimos90() { usarUltimosDias(90); }

    private void usarUltimosDias(int dias) {
        LocalDate hoy = LocalDate.now();
        dpDesde.setValue(hoy.minusDays(dias - 1L));
        dpHasta.setValue(hoy);
        generar();
    }

    private void generar() {
        try {
            datosActuales = facade.generarReporte(listaTipos.getSelectionModel().getSelectedItem(),
                    dpDesde.getValue(), dpHasta.getValue());
            mostrar(datosActuales);
        } catch (CinemaUQException e) {
            Alertas.error("No se pudo generar el reporte", e.getMessage());
        }
    }

    private void mostrar(DatosReporte datos) {
        lblTitulo.setText(datos.getTitulo());
        lblDetalle.setText("Periodo: " + Formato.periodo(datos.getDesde(), datos.getHasta())
                + "   ·   Generado: " + Formato.fechaHora(datos.getFechaGeneracion()));
        lblRegistros.setText("REGISTROS ENCONTRADOS: " + datos.getRegistros());
        panelGrafico.getChildren().setAll(crearGrafico(datos));
        llenarTabla(datos);
        llenarTotales(datos);
    }

    // ---------------- Gráficos ----------------

    private Node crearGrafico(DatosReporte datos) {
        if (!hayValores(datos)) {
            Label vacio = new Label("No hay datos para graficar en este periodo");
            vacio.getStyleClass().add("texto-suave");
            return vacio;
        }
        Chart grafico = switch (datos.getTipoGrafico()) {
            case PASTEL -> crearPastel(datos);
            case BARRAS -> llenarXY(new BarChart<>(new CategoryAxis(), ejeValores(datos)), datos);
            case LINEAS -> llenarXY(new LineChart<>(new CategoryAxis(), ejeValores(datos)), datos);
        };
        grafico.setTitle(datos.getSeries().get(0).nombre());
        grafico.setAnimated(false);
        return grafico;
    }

    private PieChart crearPastel(DatosReporte datos) {
        PieChart pastel = new PieChart();
        for (Map.Entry<String, Double> e : datos.getSeries().get(0).valores().entrySet()) {
            if (e.getValue() > 0) {
                pastel.getData().add(new PieChart.Data(e.getKey() + " (" + Formato.numero(Math.round(e.getValue())) + ")",
                        e.getValue()));
            }
        }
        pastel.setLegendVisible(true);
        pastel.setLabelsVisible(true);
        return pastel;
    }

    private <C extends XYChart<String, Number>> C llenarXY(C grafico, DatosReporte datos) {
        for (SerieGrafico serie : datos.getSeries()) {
            XYChart.Series<String, Number> datosSerie = new XYChart.Series<>();
            datosSerie.setName(serie.nombre());
            for (Map.Entry<String, Double> e : serie.valores().entrySet()) {
                datosSerie.getData().add(new XYChart.Data<>(e.getKey(), e.getValue()));
            }
            grafico.getData().add(datosSerie);
        }
        grafico.setLegendVisible(datos.getSeries().size() > 1);
        return grafico;
    }

    private NumberAxis ejeValores(DatosReporte datos) {
        NumberAxis eje = new NumberAxis();
        eje.setLabel(datos.getEtiquetaValores());
        eje.setForceZeroInRange(true);
        return eje;
    }

    private boolean hayValores(DatosReporte datos) {
        for (SerieGrafico serie : datos.getSeries()) {
            if (serie.getTotal() > 0) return true;
        }
        return false;
    }

    // ---------------- Tabla y totales ----------------

    private void llenarTabla(DatosReporte datos) {
        tablaDatos.getColumns().clear();
        List<String> columnas = datos.getColumnas();
        for (int i = 0; i < columnas.size(); i++) {
            final int indice = i;
            TableColumn<List<String>, String> columna = new TableColumn<>(columnas.get(i));
            columna.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().get(indice)));
            columna.setPrefWidth(Math.max(90, columnas.get(i).length() * 9));
            tablaDatos.getColumns().add(columna);
        }
        tablaDatos.setItems(FXCollections.observableArrayList(datos.getFilas()));
    }

    private void llenarTotales(DatosReporte datos) {
        gridTotales.getChildren().clear();
        int fila = 0;
        for (Map.Entry<String, String> total : datos.getTotales().entrySet()) {
            Label etiqueta = new Label(total.getKey().strip());
            etiqueta.getStyleClass().add(total.getKey().startsWith(" ") ? "texto-suave" : "total-etiqueta");
            etiqueta.setWrapText(true);
            Label valor = new Label(total.getValue());
            valor.getStyleClass().add("precio");
            gridTotales.addRow(fila++, etiqueta, valor);
        }
    }

    // ---------------- Exportación ----------------

    @FXML
    private void onExportarTxt() {
        exportar(FormatoReporte.TXT);
    }

    @FXML
    private void onExportarPdf() {
        exportar(FormatoReporte.PDF);
    }

    private void exportar(FormatoReporte formato) {
        if (datosActuales == null) {
            Alertas.error("No hay reporte", "Primero genera el reporte que quieres exportar.");
            return;
        }
        FileChooser selector = new FileChooser();
        selector.setTitle("Guardar reporte");
        selector.setInitialFileName(facade.nombreSugeridoReporte(datosActuales, formato));
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter(formato.getDescripcion(),
                "*." + formato.getExtension()));
        File archivo = selector.showSaveDialog(panelGrafico.getScene().getWindow());
        if (archivo == null) return;   // el usuario canceló
        try {
            Path guardado = facade.exportarReporte(datosActuales, formato, archivo.toPath());
            Alertas.info("Reporte exportado", "El reporte se guardó en:\n" + guardado);
        } catch (CinemaUQException e) {
            Alertas.error("No se pudo exportar", e.getMessage());
        }
    }

    @FXML
    private void onVolver() {
        Navegador.ir("admin");
    }
}
