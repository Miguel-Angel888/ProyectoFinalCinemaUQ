package co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Resultado de una consulta: lo mismo alimenta el archivo exportado (TXT/PDF)
// y el gráfico de JavaFX, así ambos muestran siempre los mismos números.
public class DatosReporte {

    private final TipoReporte tipo;
    private final LocalDate desde;
    private final LocalDate hasta;
    private final LocalDateTime fechaGeneracion;
    private final List<String> columnas;
    private final List<List<String>> filas;
    private final Map<String, String> totales;
    private final List<SerieGrafico> series;
    private final String etiquetaValores;   // nombre del eje de valores en el gráfico

    public DatosReporte(TipoReporte tipo, LocalDate desde, LocalDate hasta, List<String> columnas,
                        List<List<String>> filas, Map<String, String> totales,
                        List<SerieGrafico> series, String etiquetaValores) {
        if (tipo == null || desde == null || hasta == null) {
            throw new IllegalArgumentException("El reporte requiere tipo y periodo");
        }
        if (columnas == null || columnas.isEmpty()) {
            throw new IllegalArgumentException("El reporte requiere al menos una columna");
        }
        List<List<String>> copiaFilas = new ArrayList<>();
        for (List<String> fila : filas) {
            if (fila.size() != columnas.size()) {
                throw new IllegalArgumentException("Cada fila debe tener " + columnas.size() + " columnas");
            }
            copiaFilas.add(List.copyOf(fila));
        }
        this.tipo = tipo;
        this.desde = desde;
        this.hasta = hasta;
        this.fechaGeneracion = LocalDateTime.now();
        this.columnas = List.copyOf(columnas);
        this.filas = Collections.unmodifiableList(copiaFilas);
        this.totales = Collections.unmodifiableMap(new LinkedHashMap<>(totales));
        this.series = List.copyOf(series);
        this.etiquetaValores = etiquetaValores;
    }

    public String getTitulo() { return tipo.getTitulo(); }
    public TipoReporte getTipo() { return tipo; }
    public TipoGrafico getTipoGrafico() { return tipo.getTipoGrafico(); }
    public LocalDate getDesde() { return desde; }
    public LocalDate getHasta() { return hasta; }
    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public List<String> getColumnas() { return columnas; }
    public List<List<String>> getFilas() { return filas; }
    public int getRegistros() { return filas.size(); }
    public Map<String, String> getTotales() { return totales; }
    public List<SerieGrafico> getSeries() { return series; }
    public String getEtiquetaValores() { return etiquetaValores; }
}
