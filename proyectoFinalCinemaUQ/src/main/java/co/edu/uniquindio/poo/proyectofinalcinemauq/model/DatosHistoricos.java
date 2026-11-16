package co.edu.uniquindio.poo.proyectofinalcinemauq.model;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Asiento;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.EstadoFuncion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Funcion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Pelicula;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Sala;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.eventos.PublicadorEventos;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Cliente;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.promocion.Promocion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.recompensa.Canje;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.recompensa.Recompensa;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.recompensa.ReglaCostoPorValor;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.recompensa.TipoRecompensa;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.reembolso_Strategy.PoliticaReembolso;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.servicio.ReglaAcumulacionPuntos;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.servicio.ReglaPuntosPorMonto;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.servicio.ServicioReembolso;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.Movimiento;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.Pago;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.Reembolso;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.TarjetaVirtual;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Compra;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Entrada;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.ItemConfiteria;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.LineaCompra;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.TipoEntrada;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

// Historial de los últimos meses (funciones ya pasadas, compras, recargas, canjes y
// cancelaciones) para que los reportes y gráficos tengan datos desde el primer arranque.
// Usa una semilla fija: cada vez que se abre la aplicación se genera el mismo historial.
// Los movimientos se ejecutan en orden cronológico para que los saldos de las tarjetas
// y los puntos queden coherentes.
public final class DatosHistoricos {

    public static final int DIAS_DE_HISTORIAL = 90;
    private static final long SEMILLA = 20251115L;
    // Una sala tiene como máximo una función temprano y una tarde: así nunca se cruzan
    // (la película más larga dura 192 minutos y 15:30 + 192 min termina antes de las 19:00)
    private static final LocalTime[] HORARIOS_TEMPRANO = {LocalTime.of(14, 0), LocalTime.of(15, 30)};
    private static final LocalTime[] HORARIOS_TARDE = {LocalTime.of(19, 0), LocalTime.of(21, 30)};

    private final Cine cine;
    private final List<Cliente> clientes;
    private final LocalDateTime ahora;
    private final Random azar = new Random(SEMILLA);
    private final ReglaAcumulacionPuntos reglaPuntos = new ReglaPuntosPorMonto();
    // Se reutilizan las políticas reales (Strategy) para decidir el porcentaje de cada reembolso histórico
    private final ServicioReembolso politicas = new ServicioReembolso(new PublicadorEventos());
    // Cola ordenada por fecha: una acción puede programar otras posteriores (p. ej. una compra que luego se cancela)
    private final PriorityQueue<Accion> acciones = new PriorityQueue<>(Comparator.comparing(Accion::fecha));
    private int consecutivoCompra = 1;
    private int consecutivoReembolso = 1;

    private DatosHistoricos(Cine cine, List<Cliente> clientes, LocalDateTime ahora) {
        this.cine = cine;
        this.clientes = clientes;
        this.ahora = ahora;
    }

    public static void cargar(Cine cine, List<Cliente> clientes, LocalDateTime ahora) throws CinemaUQException {
        if (clientes == null || clientes.isEmpty()) {
            throw new IllegalArgumentException("El historial necesita al menos un cliente");
        }
        new DatosHistoricos(cine, clientes, ahora).generar();
    }

    private void generar() throws CinemaUQException {
        List<Funcion> funciones = crearFuncionesPasadas();
        for (Funcion funcion : funciones) planearComprasDe(funcion);
        planearCancelacionDelCine(funciones);
        planearCanjes();

        // Todo se ejecuta en orden de fecha, como habría pasado en la realidad
        while (!acciones.isEmpty()) acciones.poll().ejecutar();
    }

    // ---------------- Funciones pasadas ----------------

    private List<Funcion> crearFuncionesPasadas() {
        List<Funcion> funciones = new ArrayList<>();
        List<Pelicula> peliculas = cine.getPeliculas();
        List<Sala> salas = cine.getSalas();
        LocalDate inicio = ahora.toLocalDate().minusDays(DIAS_DE_HISTORIAL);
        int consecutivo = 1;
        for (LocalDate dia = inicio; dia.isBefore(ahora.toLocalDate()); dia = dia.plusDays(1)) {
            // Fines de semana hay más funciones
            int cantidad = dia.getDayOfWeek().getValue() >= 5 ? 2 + azar.nextInt(2) : 1 + azar.nextInt(2);
            boolean[] primeraEsTarde = new boolean[salas.size()];
            for (int i = 0; i < cantidad; i++) {
                Pelicula pelicula = peliculas.get(azar.nextInt(peliculas.size()));
                int indiceSala = (i + dia.getDayOfMonth()) % salas.size();   // se alternan las salas
                Sala sala = salas.get(indiceSala);
                boolean tarde;
                if (i < salas.size()) {
                    tarde = azar.nextBoolean();
                    primeraEsTarde[indiceSala] = tarde;
                } else {
                    tarde = !primeraEsTarde[indiceSala];   // segunda función de la sala en la otra franja
                }
                LocalTime[] franja = tarde ? HORARIOS_TARDE : HORARIOS_TEMPRANO;
                LocalDateTime fecha = dia.atTime(franja[azar.nextInt(franja.length)]);
                double precio = fecha.getHour() >= 19 ? 16000 : 13000;
                Funcion funcion = new Funcion(String.format("H%03d", consecutivo++), pelicula, sala, fecha, precio,
                        EstadoFuncion.FINALIZADA);
                cine.getFunciones().add(funcion);
                funciones.add(funcion);
            }
        }
        return funciones;
    }

    // ---------------- Compras ----------------

    private void planearComprasDe(Funcion funcion) {
        int capacidad = funcion.getSala().getCapacidad();
        boolean finDeSemana = funcion.getFechaFuncion().getDayOfWeek().getValue() >= 5;
        double ocupacionObjetivo = finDeSemana ? 0.20 + azar.nextDouble() * 0.35 : 0.06 + azar.nextDouble() * 0.24;
        int asientosObjetivo = (int) (capacidad * ocupacionObjetivo);

        int asientosPlaneados = 0;
        while (asientosPlaneados < asientosObjetivo) {
            int entradas = Math.min(1 + azar.nextInt(4), asientosObjetivo - asientosPlaneados + 1);
            Cliente cliente = clientes.get(azar.nextInt(clientes.size()));
            // Se compra entre 10 minutos y 3 días antes de la función, en horario de 9 a. m. a 10 p. m.
            LocalDateTime fecha = funcion.getFechaFuncion().minusMinutes(10 + azar.nextInt(3 * 24 * 60));
            if (fecha.getHour() < 9 || fecha.getHour() >= 22) {
                fecha = fecha.toLocalDate().atTime(9 + azar.nextInt(4), azar.nextInt(60));
            }
            if (fecha.isAfter(funcion.getFechaFuncion())) fecha = funcion.getFechaFuncion().minusMinutes(30);
            TipoEntrada tipo = elegirTipoEntrada();
            List<LineaCompra> lineas = elegirConfiteria();
            boolean seCancela = azar.nextInt(100) < 6;   // ~6 % de las compras las cancela el cliente
            LocalDateTime fechaCompra = fecha;
            acciones.add(new Accion(fechaCompra, () -> comprar(funcion, cliente, fechaCompra, entradas, tipo, lineas, seCancela)));
            asientosPlaneados += entradas;
        }
    }

    private void comprar(Funcion funcion, Cliente cliente, LocalDateTime fecha, int cantidad, TipoEntrada tipo,
                         List<LineaCompra> lineas, boolean seCancela) throws CinemaUQException {
        if (funcion.getEstadoFuncion() == EstadoFuncion.CANCELADA) return;   // el cine ya la canceló
        List<Asiento> asientos = elegirAsientosLibres(funcion, cantidad);
        if (asientos.isEmpty()) return;

        String id = String.format("CMP-H%05d", consecutivoCompra++);
        Compra.CompraBuilder builder = new Compra.CompraBuilder()
                .conId(id)
                .conCliente(cliente)
                .conFecha(fecha);
        double precio = funcion.getPrecioBase() * tipo.getFactorPrecio();
        for (Asiento asiento : asientos) {
            builder.agregarEntrada(new Entrada("ENT-" + id + "-" + asiento.getReferencia(), funcion, asiento, tipo, precio));
        }
        for (LineaCompra linea : lineas) builder.agregarLinea(linea);
        for (Promocion promocion : cine.getPromociones()) builder.agregarPromocion(promocion);
        Compra compra = builder.build();

        // Si no alcanza el saldo, el cliente recarga un rato antes de comprar
        TarjetaVirtual tarjeta = cliente.getTarjetaVirtual();
        if (tarjeta.getSaldo() < compra.getTotal()) {
            double faltante = compra.getTotal() - tarjeta.getSaldo();
            double recarga = Math.ceil(faltante / 10000) * 10000 + 10000 * azar.nextInt(6);
            LocalDateTime fechaRecarga = fecha.minusMinutes(5 + azar.nextInt(120));
            List<Movimiento> movimientos = tarjeta.getListaMovimientos();
            if (!movimientos.isEmpty()) {
                LocalDateTime ultimo = movimientos.get(movimientos.size() - 1).getFechaMovimiento();
                if (fechaRecarga.isBefore(ultimo)) fechaRecarga = ultimo;   // la tarjeta guarda los movimientos en orden
            }
            tarjeta.recargar(recarga, fechaRecarga);
        }
        for (Asiento asiento : asientos) funcion.reservarAsiento(asiento);
        if (compra.getTotal() > 0) tarjeta.debitar(compra.getTotal(), "Compra " + compra.getId(), fecha);

        int puntos = reglaPuntos.calcular(compra.getTotal());
        compra.registrarPago(new Pago(compra, tarjeta, fecha), puntos);
        cliente.getPuntos().acumular(puntos, fecha.toLocalDate());
        cliente.agregarCompra(compra);
        cine.getCompras().add(compra);

        if (seCancela) {
            // El cliente cancela entre la compra y 3 horas antes de la función
            long minutosDisponibles = Duration.between(fecha, funcion.getFechaFuncion().minusHours(3)).toMinutes();
            if (minutosDisponibles > 30) {
                LocalDateTime fechaCancelacion = fecha.plusMinutes(30 + azar.nextInt((int) minutosDisponibles - 29));
                if (fechaCancelacion.isBefore(ahora)) {
                    acciones.add(new Accion(fechaCancelacion, () -> reembolsar(compra, fechaCancelacion)));
                }
            }
        }
    }

    // ---------------- Cancelaciones y reembolsos ----------------

    // Una función de hace unas semanas la canceló el cine: todas sus compras se devuelven al 100 %
    private void planearCancelacionDelCine(List<Funcion> funciones) {
        LocalDate objetivo = ahora.toLocalDate().minusDays(18);
        Funcion cancelada = null;
        for (Funcion f : funciones) {
            if (!f.getFechaFuncion().toLocalDate().isBefore(objetivo)) {
                cancelada = f;
                break;
            }
        }
        if (cancelada == null) return;
        Funcion funcion = cancelada;
        LocalDateTime fecha = funcion.getFechaFuncion().minusHours(4);
        acciones.add(new Accion(fecha, () -> {
            funcion.setEstadoFuncion(EstadoFuncion.CANCELADA);
            for (Compra compra : new ArrayList<>(cine.getCompras())) {
                if (compra.admiteReembolso() && compra.getListaEntradas().get(0).getFuncion() == funcion) {
                    reembolsar(compra, fecha);
                }
            }
        }));
    }

    private void reembolsar(Compra compra, LocalDateTime fecha) throws CinemaUQException {
        if (!compra.admiteReembolso()) return;
        Funcion funcion = compra.getListaEntradas().get(0).getFuncion();
        PoliticaReembolso politica = politicas.seleccionarPolitica(funcion, fecha);
        if (politica.getPorcentaje() == 0) return;

        Cliente cliente = compra.getCliente();
        double cantidad = compra.getTotal() * politica.getPorcentaje() / 100;
        if (cantidad > 0) {
            cliente.getTarjetaVirtual().acreditar(cantidad, "Reembolso compra " + compra.getId(), fecha);
        }
        LocalDate dia = fecha.toLocalDate();
        cliente.getPuntos().revertir(Math.min(compra.getPuntosGanados(), cliente.getPuntos().getPuntosDisponibles(dia)), dia);
        for (Entrada entrada : compra.getListaEntradas()) funcion.liberarAsiento(entrada.getAsiento());
        compra.registrarReembolso(new Reembolso(compra, String.format("REE-H%05d", consecutivoReembolso++),
                politica.getPorcentaje(), cantidad, fecha, politica.getNombre()));
    }

    // ---------------- Canjes de puntos ----------------

    private void planearCanjes() {
        List<Recompensa> catalogo = new ArrayList<>();
        for (ItemConfiteria item : cine.getConfiteria()) {
            if (item.getPuntosRedencion() > 0) catalogo.add(Recompensa.deConfiteria(item));
        }
        catalogo.add(Recompensa.deEntrada("R-ENT-15000", "Entrada hasta $15.000", 15000, new ReglaCostoPorValor()));

        LocalDate inicio = ahora.toLocalDate().minusDays(DIAS_DE_HISTORIAL - 20);
        for (Cliente cliente : clientes) {
            for (LocalDate dia = inicio.plusDays(azar.nextInt(10)); dia.isBefore(ahora.toLocalDate());
                 dia = dia.plusDays(3 + azar.nextInt(5))) {
                Recompensa recompensa = catalogo.get(azar.nextInt(catalogo.size()));
                LocalDateTime fecha = dia.atTime(12 + azar.nextInt(8), azar.nextInt(60));
                acciones.add(new Accion(fecha, () -> canjear(cliente, recompensa, fecha)));
            }
        }
    }

    private void canjear(Cliente cliente, Recompensa recompensa, LocalDateTime fecha) throws CinemaUQException {
        LocalDate dia = fecha.toLocalDate();
        if (cliente.getPuntos().getPuntosDisponibles(dia) < recompensa.getPuntosRequeridos()) return;
        cliente.getPuntos().redimir(recompensa.getPuntosRequeridos(), dia);
        Canje canje = new Canje(cliente, recompensa, fecha);
        cliente.agregarCanje(canje);
        // Los vales de confitería de los últimos días siguen pendientes de entregar (para el panel del administrador)
        boolean valeReciente = recompensa.getTipo() == TipoRecompensa.CONFITERIA && fecha.isAfter(ahora.minusDays(4));
        if (!valeReciente) canje.marcarUsado();
    }

    // ---------------- Utilidades ----------------

    private List<Asiento> elegirAsientosLibres(Funcion funcion, int cantidad) {
        List<Asiento> libres = new ArrayList<>();
        for (Asiento a : funcion.getSala().getListaAsientos()) {
            if (funcion.estaDisponible(a)) libres.add(a);
        }
        Collections.shuffle(libres, azar);
        return new ArrayList<>(libres.subList(0, Math.min(cantidad, libres.size())));
    }

    private TipoEntrada elegirTipoEntrada() {
        int n = azar.nextInt(100);
        if (n < 70) return TipoEntrada.GENERAL;
        if (n < 88) return TipoEntrada.NINO;
        return TipoEntrada.ADULTO_MAYOR;
    }

    private List<LineaCompra> elegirConfiteria() {
        List<LineaCompra> lineas = new ArrayList<>();
        List<ItemConfiteria> menu = cine.getConfiteria();
        int productos = azar.nextInt(100) < 55 ? 1 + azar.nextInt(2) : 0;   // un poco más de la mitad compra confitería
        for (int i = 0; i < productos; i++) {
            lineas.add(new LineaCompra(menu.get(azar.nextInt(menu.size())), 1 + azar.nextInt(2)));
        }
        return lineas;
    }

    @FunctionalInterface
    private interface Operacion {
        void ejecutar() throws CinemaUQException;
    }

    private record Accion(LocalDateTime fecha, Operacion operacion) {
        void ejecutar() throws CinemaUQException {
            operacion.ejecutar();
        }
    }
}
