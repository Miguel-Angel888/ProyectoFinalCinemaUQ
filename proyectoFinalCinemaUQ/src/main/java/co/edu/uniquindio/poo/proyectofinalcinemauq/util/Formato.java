package co.edu.uniquindio.poo.proyectofinalcinemauq.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class Formato {

    private static final Locale CO = Locale.forLanguageTag("es-CO");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy", CO);
    private static final DateTimeFormatter FECHA_CORTA = DateTimeFormatter.ofPattern("dd/MM", CO);
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", CO);
    private static final DateTimeFormatter FUNCION = DateTimeFormatter.ofPattern("EEE dd MMM · h:mm a", CO);
    private static final DateTimeFormatter MES = DateTimeFormatter.ofPattern("MMM yyyy", CO);

    private Formato() { }

    public static String moneda(double valor) {
        return String.format(CO, "$%,.0f", valor);
    }

    public static String numero(long valor) {
        return String.format(CO, "%,d", valor);
    }

    public static String porcentaje(double valor) {
        return String.format(CO, "%.1f %%", valor);
    }

    public static String fecha(LocalDate fecha) {
        return fecha.format(FECHA);
    }

    public static String fechaCorta(LocalDate fecha) {
        return fecha.format(FECHA_CORTA);
    }

    public static String fechaHora(LocalDateTime fecha) {
        return fecha.format(FECHA_HORA);
    }

    public static String fechaFuncion(LocalDateTime fecha) {
        return fecha.format(FUNCION);
    }

    public static String mes(YearMonth mes) {
        return mes.format(MES);
    }

    public static String periodo(LocalDate desde, LocalDate hasta) {
        return fecha(desde) + " al " + fecha(hasta);
    }
}
