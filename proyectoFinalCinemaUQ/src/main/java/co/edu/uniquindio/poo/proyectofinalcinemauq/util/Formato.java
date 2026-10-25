package co.edu.uniquindio.poo.proyectofinalcinemauq.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class Formato {

    private static final Locale CO = Locale.forLanguageTag("es-CO");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy", CO);
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", CO);
    private static final DateTimeFormatter FUNCION = DateTimeFormatter.ofPattern("EEE dd MMM · h:mm a", CO);

    private Formato() { }

    public static String moneda(double valor) {
        return String.format(CO, "$%,.0f", valor);
    }

    public static String fecha(LocalDate fecha) {
        return fecha.format(FECHA);
    }

    public static String fechaHora(LocalDateTime fecha) {
        return fecha.format(FECHA_HORA);
    }

    public static String fechaFuncion(LocalDateTime fecha) {
        return fecha.format(FUNCION);
    }
}
