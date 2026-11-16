package co.edu.uniquindio.poo.proyectofinalcinemauq.model.servicio;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.Cine;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.EstadoFuncion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Funcion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Sala;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.ReporteInvalidoException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.AcumulacionPuntos;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Cliente;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Usuario;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.recompensa.Canje;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte.DatosReporte;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte.SerieGrafico;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte.TipoReporte;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.Movimiento;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.Reembolso;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.TipoMovimiento;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Combo;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Compra;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Entrada;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.EstadoCompra;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.ItemConfiteria;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.LineaCompra;
import co.edu.uniquindio.poo.proyectofinalcinemauq.util.Formato;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

// Calcula los datos de cada reporte a partir de lo que hay en el cine.
// El mismo resultado (DatosReporte) se usa para el gráfico y para el archivo exportado.
public class ServicioEstadisticas {

    // Hasta este número de días el gráfico agrupa por día; con periodos más largos agrupa por mes
    static final int MAX_DIAS_AGRUPADOS_POR_DIA = 62;

    private final Cine cine;

    public ServicioEstadisticas(Cine cine) {
        if (cine == null) {
            throw new IllegalArgumentException("Las estadísticas necesitan los datos del cine");
        }
        this.cine = cine;
    }

    public DatosReporte generar(TipoReporte tipo, LocalDate desde, LocalDate hasta) throws ReporteInvalidoException {
        validar(tipo, desde, hasta);
        return switch (tipo) {
            case VENTAS -> ventas(desde, hasta);
            case ENTRADAS -> entradas(desde, hasta);
            case OCUPACION -> ocupacion(desde, hasta);
            case CONFITERIA -> confiteria(desde, hasta);
            case RECARGAS -> recargas(desde, hasta);
            case CANCELACIONES -> cancelaciones(desde, hasta);
            case PUNTOS -> puntos(desde, hasta);
        };
    }

    public static void validar(TipoReporte tipo, LocalDate desde, LocalDate hasta) throws ReporteInvalidoException {
        if (tipo == null) {
            throw new ReporteInvalidoException("Selecciona el tipo de reporte");
        }
        if (desde == null || hasta == null) {
            throw new ReporteInvalidoException("Indica la fecha inicial y la fecha final del periodo");
        }
        if (desde.isAfter(hasta)) {
            throw new ReporteInvalidoException("La fecha inicial no puede ser posterior a la fecha final");
        }
    }

    // ---------------- Ventas por periodo ----------------

    private DatosReporte ventas(LocalDate desde, LocalDate hasta) {
        List<Compra> compras = compras(desde, hasta, Compra::fueCobrada);
        Map<String, Double> porPeriodo = periodosVacios(desde, hasta);
        List<List<String>> filas = new ArrayList<>();
        double vendido = 0;
        double reembolsado = 0;
        int entradas = 0;
        for (Compra c : compras) {
            vendido += c.getTotal();
            entradas += c.getListaEntradas().size();
            if (c.getReembolso() != null) reembolsado += c.getReembolso().getCantidad();
            porPeriodo.merge(periodo(c.getFecha().toLocalDate(), desde, hasta), c.getTotal(), Double::sum);
            filas.add(List.of(Formato.fechaHora(c.getFecha()), c.getId(), c.getCliente().getNombre(),
                    funcionDe(c).getPelicula().getTitulo(), String.valueOf(c.getListaEntradas().size()),
                    String.valueOf(unidadesConfiteria(c)), Formato.moneda(c.getTotal()), c.getEstado().getEtiqueta()));
        }
        Map<String, String> totales = new LinkedHashMap<>();
        totales.put("Compras", Formato.numero(compras.size()));
        totales.put("Entradas vendidas", Formato.numero(entradas));
        totales.put("Total vendido", Formato.moneda(vendido));
        totales.put("Total reembolsado de estas compras", Formato.moneda(reembolsado));
        totales.put("Ingreso neto", Formato.moneda(vendido - reembolsado));
        totales.put("Ticket promedio", Formato.moneda(compras.isEmpty() ? 0 : vendido / compras.size()));
        return new DatosReporte(TipoReporte.VENTAS, desde, hasta,
                List.of("Fecha", "Compra", "Cliente", "Película", "Entradas", "Confitería", "Total", "Estado"),
                filas, totales, List.of(new SerieGrafico("Ventas", porPeriodo)), "Pesos ($)");
    }

    // ---------------- Entradas por película y función ----------------

    private DatosReporte entradas(LocalDate desde, LocalDate hasta) {
        // Las entradas reembolsadas se devolvieron: no cuentan como vendidas
        List<Compra> compras = compras(desde, hasta, c -> c.getEstado() == EstadoCompra.PAGADA);
        Map<Funcion, int[]> porFuncion = new LinkedHashMap<>();
        Map<Funcion, Double> valorPorFuncion = new LinkedHashMap<>();
        Map<String, Double> porPelicula = new LinkedHashMap<>();
        for (Compra c : compras) {
            for (Entrada e : c.getListaEntradas()) {
                porFuncion.computeIfAbsent(e.getFuncion(), f -> new int[1])[0]++;
                valorPorFuncion.merge(e.getFuncion(), e.getPrecio(), Double::sum);
                porPelicula.merge(e.getFuncion().getPelicula().getTitulo(), 1.0, Double::sum);
            }
        }
        List<Funcion> funciones = new ArrayList<>(porFuncion.keySet());
        funciones.sort(Comparator.comparing((Funcion f) -> f.getPelicula().getTitulo()).thenComparing(Funcion::getFechaFuncion));
        List<List<String>> filas = new ArrayList<>();
        int totalEntradas = 0;
        double totalValor = 0;
        for (Funcion f : funciones) {
            int cantidad = porFuncion.get(f)[0];
            totalEntradas += cantidad;
            totalValor += valorPorFuncion.get(f);
            filas.add(List.of(f.getPelicula().getTitulo(), f.getReferencia(), f.getSala().getNombre(),
                    Formato.fechaHora(f.getFechaFuncion()), String.valueOf(cantidad), Formato.moneda(valorPorFuncion.get(f))));
        }
        Map<String, String> totales = new LinkedHashMap<>();
        totales.put("Películas con ventas", Formato.numero(porPelicula.size()));
        totales.put("Funciones con ventas", Formato.numero(funciones.size()));
        totales.put("Entradas vendidas", Formato.numero(totalEntradas));
        totales.put("Valor de las entradas (antes de descuentos)", Formato.moneda(totalValor));
        return new DatosReporte(TipoReporte.ENTRADAS, desde, hasta,
                List.of("Película", "Función", "Sala", "Fecha función", "Entradas", "Valor"),
                filas, totales, List.of(new SerieGrafico("Entradas por película", ordenarDesc(porPelicula))), "Entradas");
    }

    // ---------------- Ocupación de salas ----------------

    private DatosReporte ocupacion(LocalDate desde, LocalDate hasta) {
        List<Funcion> funciones = new ArrayList<>();
        for (Funcion f : cine.getFunciones()) {
            LocalDate dia = f.getFechaFuncion().toLocalDate();
            if (f.getEstadoFuncion() != EstadoFuncion.CANCELADA && enPeriodo(dia, desde, hasta)) funciones.add(f);
        }
        funciones.sort(Comparator.comparing(Funcion::getFechaFuncion));

        Map<Sala, int[]> porSala = new LinkedHashMap<>();   // [ocupados, capacidad]
        for (Sala s : cine.getSalas()) porSala.put(s, new int[2]);
        List<List<String>> filas = new ArrayList<>();
        int ocupadosTotal = 0;
        int capacidadTotal = 0;
        for (Funcion f : funciones) {
            int capacidad = f.getSala().getCapacidad();
            int ocupados = capacidad - f.getCantidadDisponibles();
            int[] acumulado = porSala.computeIfAbsent(f.getSala(), s -> new int[2]);
            acumulado[0] += ocupados;
            acumulado[1] += capacidad;
            ocupadosTotal += ocupados;
            capacidadTotal += capacidad;
            filas.add(List.of(f.getReferencia(), f.getPelicula().getTitulo(), f.getSala().getNombre(),
                    Formato.fechaHora(f.getFechaFuncion()), String.valueOf(ocupados), String.valueOf(capacidad),
                    Formato.porcentaje(porcentaje(ocupados, capacidad))));
        }
        Map<String, Double> serie = new LinkedHashMap<>();
        for (Map.Entry<Sala, int[]> e : porSala.entrySet()) {
            serie.put(e.getKey().getNombre(), porcentaje(e.getValue()[0], e.getValue()[1]));
        }
        Map<String, String> totales = new LinkedHashMap<>();
        totales.put("Funciones", Formato.numero(funciones.size()));
        totales.put("Asientos ocupados", Formato.numero(ocupadosTotal));
        totales.put("Capacidad ofrecida", Formato.numero(capacidadTotal));
        totales.put("Ocupación promedio", Formato.porcentaje(porcentaje(ocupadosTotal, capacidadTotal)));
        return new DatosReporte(TipoReporte.OCUPACION, desde, hasta,
                List.of("Función", "Película", "Sala", "Fecha", "Ocupados", "Capacidad", "Ocupación"),
                filas, totales, List.of(new SerieGrafico("Ocupación por sala (%)", serie)), "Ocupación (%)");
    }

    // ---------------- Productos y combos ----------------

    private DatosReporte confiteria(LocalDate desde, LocalDate hasta) {
        List<Compra> compras = compras(desde, hasta, c -> c.getEstado() == EstadoCompra.PAGADA);
        Map<String, ItemConfiteria> items = new LinkedHashMap<>();
        Map<String, Integer> unidades = new LinkedHashMap<>();
        Map<String, Double> valores = new LinkedHashMap<>();
        for (Compra c : compras) {
            for (LineaCompra l : c.getListaLineas()) {
                String ref = l.getItem().getReferencia();
                items.putIfAbsent(ref, l.getItem());
                unidades.merge(ref, l.getCantidad(), Integer::sum);
                valores.merge(ref, l.getSubtotal(), Double::sum);
            }
        }
        List<String> refs = new ArrayList<>(items.keySet());
        refs.sort(Comparator.comparing((String r) -> unidades.get(r)).reversed().thenComparing(r -> items.get(r).getNombre()));

        List<List<String>> filas = new ArrayList<>();
        Map<String, Double> serie = new LinkedHashMap<>();
        int totalUnidades = 0;
        int unidadesCombos = 0;
        double totalValor = 0;
        for (String ref : refs) {
            ItemConfiteria item = items.get(ref);
            boolean esCombo = item instanceof Combo;
            int cantidad = unidades.get(ref);
            totalUnidades += cantidad;
            if (esCombo) unidadesCombos += cantidad;
            totalValor += valores.get(ref);
            serie.put(item.getNombre(), (double) cantidad);
            filas.add(List.of(item.getNombre(), esCombo ? "Combo" : "Producto", String.valueOf(cantidad),
                    Formato.moneda(valores.get(ref))));
        }
        Map<String, String> totales = new LinkedHashMap<>();
        totales.put("Ítems distintos vendidos", Formato.numero(refs.size()));
        totales.put("Unidades vendidas", Formato.numero(totalUnidades));
        totales.put("Unidades de productos sueltos", Formato.numero(totalUnidades - unidadesCombos));
        totales.put("Unidades de combos", Formato.numero(unidadesCombos));
        totales.put("Total confitería", Formato.moneda(totalValor));
        return new DatosReporte(TipoReporte.CONFITERIA, desde, hasta,
                List.of("Ítem", "Tipo", "Unidades", "Total"),
                filas, totales, List.of(new SerieGrafico("Unidades vendidas", serie)), "Unidades");
    }

    // ---------------- Recargas ----------------

    private DatosReporte recargas(LocalDate desde, LocalDate hasta) {
        List<FilaConFecha> registros = new ArrayList<>();
        Map<String, Double> porCliente = new LinkedHashMap<>();
        double total = 0;
        for (Cliente cliente : clientes()) {
            for (Movimiento m : cliente.getTarjetaVirtual().getListaMovimientos()) {
                if (m.getTipoMovimiento() != TipoMovimiento.RECARGA) continue;
                if (!enPeriodo(m.getFechaMovimiento().toLocalDate(), desde, hasta)) continue;
                total += m.getCantidad();
                porCliente.merge(cliente.getNombre(), m.getCantidad(), Double::sum);
                registros.add(new FilaConFecha(m.getFechaMovimiento(), List.of(Formato.fechaHora(m.getFechaMovimiento()),
                        cliente.getNombre(), cliente.getTarjetaVirtual().getNumeroTarjeta(), m.getReferencia(),
                        Formato.moneda(m.getCantidad()), Formato.moneda(m.getSaldoResultante()))));
            }
        }
        Map<String, String> totales = new LinkedHashMap<>();
        totales.put("Recargas", Formato.numero(registros.size()));
        totales.put("Clientes que recargaron", Formato.numero(porCliente.size()));
        totales.put("Total recargado", Formato.moneda(total));
        totales.put("Recarga promedio", Formato.moneda(registros.isEmpty() ? 0 : total / registros.size()));
        return new DatosReporte(TipoReporte.RECARGAS, desde, hasta,
                List.of("Fecha", "Cliente", "Tarjeta", "Referencia", "Monto", "Saldo resultante"),
                ordenarPorFecha(registros), totales,
                List.of(new SerieGrafico("Total recargado por cliente", ordenarDesc(porCliente))), "Pesos ($)");
    }

    // ---------------- Cancelaciones y reembolsos ----------------

    private DatosReporte cancelaciones(LocalDate desde, LocalDate hasta) {
        List<FilaConFecha> registros = new ArrayList<>();
        Map<String, Double> porMotivo = new LinkedHashMap<>();
        double pagado = 0;
        double devuelto = 0;
        for (Compra c : cine.getCompras()) {
            Reembolso r = c.getReembolso();
            if (r == null || !enPeriodo(r.getFechaReembolso().toLocalDate(), desde, hasta)) continue;
            pagado += c.getTotal();
            devuelto += r.getCantidad();
            porMotivo.merge(r.getDescripcion(), 1.0, Double::sum);
            registros.add(new FilaConFecha(r.getFechaReembolso(), List.of(Formato.fechaHora(r.getFechaReembolso()),
                    c.getId(), c.getCliente().getNombre(), funcionDe(c).getPelicula().getTitulo(),
                    Formato.moneda(c.getTotal()), Formato.porcentaje(r.getPorcentaje()),
                    Formato.moneda(r.getCantidad()), r.getDescripcion())));
        }
        Map<String, String> totales = new LinkedHashMap<>();
        totales.put("Compras canceladas", Formato.numero(registros.size()));
        for (Map.Entry<String, Double> e : porMotivo.entrySet()) {
            totales.put("  " + e.getKey(), Formato.numero(e.getValue().longValue()));
        }
        totales.put("Valor pagado de esas compras", Formato.moneda(pagado));
        totales.put("Total reembolsado", Formato.moneda(devuelto));
        totales.put("Retenido por el cine", Formato.moneda(pagado - devuelto));
        return new DatosReporte(TipoReporte.CANCELACIONES, desde, hasta,
                List.of("Fecha", "Compra", "Cliente", "Película", "Pagado", "Devuelto %", "Reembolso", "Motivo"),
                ordenarPorFecha(registros), totales,
                List.of(new SerieGrafico("Cancelaciones por motivo", porMotivo)), "Cancelaciones");
    }

    // ---------------- Puntos ----------------

    private DatosReporte puntos(LocalDate desde, LocalDate hasta) {
        List<FilaConFecha> registros = new ArrayList<>();
        Map<String, Double> acumuladosPorPeriodo = periodosVacios(desde, hasta);
        Map<String, Double> redimidosPorPeriodo = periodosVacios(desde, hasta);
        long acumulados = 0;
        long redimidos = 0;
        long vigentes = 0;
        LocalDate hoy = LocalDate.now();
        for (Cliente cliente : clientes()) {
            vigentes += cliente.getPuntos().getPuntosDisponibles(hoy);
            for (AcumulacionPuntos a : cliente.getPuntos().getListaAcumulaciones()) {
                if (!enPeriodo(a.getFechaObtencion(), desde, hasta)) continue;
                acumulados += a.getPuntosObtenidos();
                acumuladosPorPeriodo.merge(periodo(a.getFechaObtencion(), desde, hasta), (double) a.getPuntosObtenidos(), Double::sum);
                registros.add(new FilaConFecha(a.getFechaObtencion().atStartOfDay(), List.of(Formato.fecha(a.getFechaObtencion()),
                        cliente.getNombre(), "Acumulados", Formato.numero(a.getPuntosObtenidos()),
                        "Vencen el " + Formato.fecha(a.getFechaVencimiento()))));
            }
            for (Canje canje : cliente.getListaCanjes()) {
                LocalDate dia = canje.getFecha().toLocalDate();
                if (!enPeriodo(dia, desde, hasta)) continue;
                redimidos += canje.getPuntosUsados();
                redimidosPorPeriodo.merge(periodo(dia, desde, hasta), (double) canje.getPuntosUsados(), Double::sum);
                registros.add(new FilaConFecha(canje.getFecha(), List.of(Formato.fecha(dia), cliente.getNombre(),
                        "Redimidos", Formato.numero(canje.getPuntosUsados()), canje.getRecompensa().getNombre())));
            }
        }
        Map<String, String> totales = new LinkedHashMap<>();
        totales.put("Puntos acumulados", Formato.numero(acumulados));
        totales.put("Puntos redimidos", Formato.numero(redimidos));
        totales.put("Diferencia del periodo", Formato.numero(acumulados - redimidos));
        totales.put("Puntos vigentes hoy (todos los clientes)", Formato.numero(vigentes));
        return new DatosReporte(TipoReporte.PUNTOS, desde, hasta,
                List.of("Fecha", "Cliente", "Movimiento", "Puntos", "Detalle"),
                ordenarPorFecha(registros), totales,
                List.of(new SerieGrafico("Acumulados", acumuladosPorPeriodo),
                        new SerieGrafico("Redimidos", redimidosPorPeriodo)), "Puntos");
    }

    // ---------------- Utilidades ----------------

    private List<Compra> compras(LocalDate desde, LocalDate hasta, Predicate<Compra> condicion) {
        List<Compra> resultado = new ArrayList<>();
        for (Compra c : cine.getCompras()) {
            if (condicion.test(c) && enPeriodo(c.getFecha().toLocalDate(), desde, hasta)) resultado.add(c);
        }
        resultado.sort(Comparator.comparing(Compra::getFecha));
        return resultado;
    }

    private List<Cliente> clientes() {
        List<Cliente> resultado = new ArrayList<>();
        for (Usuario u : cine.getUsuarios()) {
            if (u instanceof Cliente c) resultado.add(c);
        }
        return resultado;
    }

    private static boolean enPeriodo(LocalDate dia, LocalDate desde, LocalDate hasta) {
        return !dia.isBefore(desde) && !dia.isAfter(hasta);
    }

    private static Funcion funcionDe(Compra compra) {
        return compra.getListaEntradas().get(0).getFuncion();
    }

    private static int unidadesConfiteria(Compra compra) {
        int total = 0;
        for (LineaCompra l : compra.getListaLineas()) total += l.getCantidad();
        return total;
    }

    private static double porcentaje(int parte, int total) {
        return total == 0 ? 0 : parte * 100.0 / total;
    }

    private static boolean agrupaPorDia(LocalDate desde, LocalDate hasta) {
        return ChronoUnit.DAYS.between(desde, hasta) < MAX_DIAS_AGRUPADOS_POR_DIA;
    }

    private static String periodo(LocalDate dia, LocalDate desde, LocalDate hasta) {
        return agrupaPorDia(desde, hasta) ? Formato.fechaCorta(dia) : Formato.mes(YearMonth.from(dia));
    }

    // Todos los días (o meses) del periodo en cero, para que la línea del gráfico no salte fechas
    private static Map<String, Double> periodosVacios(LocalDate desde, LocalDate hasta) {
        Map<String, Double> resultado = new LinkedHashMap<>();
        if (agrupaPorDia(desde, hasta)) {
            for (LocalDate d = desde; !d.isAfter(hasta); d = d.plusDays(1)) resultado.put(Formato.fechaCorta(d), 0.0);
        } else {
            for (YearMonth m = YearMonth.from(desde); !m.isAfter(YearMonth.from(hasta)); m = m.plusMonths(1)) {
                resultado.put(Formato.mes(m), 0.0);
            }
        }
        return resultado;
    }

    private static Map<String, Double> ordenarDesc(Map<String, Double> valores) {
        List<Map.Entry<String, Double>> entradas = new ArrayList<>(valores.entrySet());
        entradas.sort(Map.Entry.<String, Double>comparingByValue().reversed());
        Map<String, Double> resultado = new LinkedHashMap<>();
        for (Map.Entry<String, Double> e : entradas) resultado.put(e.getKey(), e.getValue());
        return resultado;
    }

    private static List<List<String>> ordenarPorFecha(List<FilaConFecha> registros) {
        registros.sort(Comparator.comparing(FilaConFecha::fecha));
        List<List<String>> filas = new ArrayList<>();
        for (FilaConFecha r : registros) filas.add(r.celdas());
        return filas;
    }

    private record FilaConFecha(LocalDateTime fecha, List<String> celdas) { }
}
