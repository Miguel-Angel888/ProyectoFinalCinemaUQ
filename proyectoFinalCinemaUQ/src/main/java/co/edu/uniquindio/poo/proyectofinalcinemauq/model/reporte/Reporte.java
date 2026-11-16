package co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte;

import co.edu.uniquindio.poo.proyectofinalcinemauq.util.Formato;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// PATRÓN FACTORY METHOD - Producto
// Arma el contenido común de cualquier reporte (título, fecha de generación,
// periodo consultado, registros encontrados, tabla y totales).
// Cada subclase solo decide cómo se escribe el archivo.
public abstract class Reporte {

    private static final int ANCHO_MAXIMO_COLUMNA = 28;
    private static final String SEPARADOR_COLUMNAS = "  ";

    private final DatosReporte datos;

    protected Reporte(DatosReporte datos) {
        if (datos == null) {
            throw new IllegalArgumentException("El reporte necesita datos");
        }
        this.datos = datos;
    }

    public abstract void exportar(Path archivo) throws IOException;

    public DatosReporte getDatos() {
        return datos;
    }

    protected List<String> construirLineas() {
        List<String> lineas = new ArrayList<>();
        lineas.add("CinemaUQ - " + datos.getTitulo());
        lineas.add("Fecha de generación: " + Formato.fechaHora(datos.getFechaGeneracion()));
        lineas.add("Periodo consultado:  " + Formato.periodo(datos.getDesde(), datos.getHasta()));
        lineas.add("Registros encontrados: " + datos.getRegistros());
        lineas.add("");

        int[] anchos = calcularAnchos();
        boolean[] numericas = detectarColumnasNumericas();
        String encabezado = formatearFila(datos.getColumnas(), anchos, numericas);
        String linea = "-".repeat(encabezado.length());
        lineas.add(linea);
        lineas.add(encabezado);
        lineas.add(linea);
        if (datos.getFilas().isEmpty()) {
            lineas.add("No se encontraron registros en el periodo consultado.");
        }
        for (List<String> fila : datos.getFilas()) {
            lineas.add(formatearFila(fila, anchos, numericas));
        }
        lineas.add(linea);
        lineas.add("");

        lineas.add("TOTALES");
        int anchoEtiqueta = 0;
        for (String etiqueta : datos.getTotales().keySet()) anchoEtiqueta = Math.max(anchoEtiqueta, etiqueta.length());
        for (Map.Entry<String, String> total : datos.getTotales().entrySet()) {
            lineas.add("  " + rellenar(total.getKey() + ":", anchoEtiqueta + 1, false) + "  " + total.getValue());
        }
        return lineas;
    }

    protected String construirTexto() {
        return String.join(System.lineSeparator(), construirLineas()) + System.lineSeparator();
    }

    private int[] calcularAnchos() {
        List<String> columnas = datos.getColumnas();
        int[] anchos = new int[columnas.size()];
        for (int i = 0; i < columnas.size(); i++) anchos[i] = columnas.get(i).length();
        for (List<String> fila : datos.getFilas()) {
            for (int i = 0; i < fila.size(); i++) anchos[i] = Math.max(anchos[i], fila.get(i).length());
        }
        for (int i = 0; i < anchos.length; i++) anchos[i] = Math.min(anchos[i], ANCHO_MAXIMO_COLUMNA);
        return anchos;
    }

    // Las columnas con montos, cantidades o porcentajes se alinean a la derecha
    private boolean[] detectarColumnasNumericas() {
        boolean[] numericas = new boolean[datos.getColumnas().size()];
        for (int i = 0; i < numericas.length; i++) {
            boolean todas = !datos.getFilas().isEmpty();
            for (List<String> fila : datos.getFilas()) {
                if (!fila.get(i).matches("[$]?-?[\\d.,]+( %)?")) {
                    todas = false;
                    break;
                }
            }
            numericas[i] = todas;
        }
        return numericas;
    }

    private String formatearFila(List<String> celdas, int[] anchos, boolean[] derecha) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < celdas.size(); i++) {
            if (i > 0) sb.append(SEPARADOR_COLUMNAS);
            sb.append(rellenar(recortar(celdas.get(i), anchos[i]), anchos[i], derecha[i]));
        }
        return sb.toString().stripTrailing();
    }

    private static String recortar(String texto, int ancho) {
        return texto.length() <= ancho ? texto : texto.substring(0, ancho - 3) + "...";
    }

    private static String rellenar(String texto, int ancho, boolean derecha) {
        String espacios = " ".repeat(Math.max(0, ancho - texto.length()));
        return derecha ? espacios + texto : texto + espacios;
    }
}
