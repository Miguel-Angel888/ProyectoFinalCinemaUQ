package co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

// Una serie de datos para un gráfico: categoría (eje X o porción) -> valor.
// Se conserva el orden de inserción para que las fechas salgan en orden.
public record SerieGrafico(String nombre, Map<String, Double> valores) {

    public SerieGrafico {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("La serie debe tener nombre");
        }
        valores = Collections.unmodifiableMap(new LinkedHashMap<>(valores == null ? Map.of() : valores));
    }

    public double getTotal() {
        double total = 0;
        for (double v : valores.values()) total += v;
        return total;
    }
}
