package co.edu.uniquindio.poo.proyectofinalcinemauq.model;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Asiento;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.EstadoFuncion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Funcion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Pelicula;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Sala;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.TipoAsiento;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Administrador;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Cliente;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.promocion.PromocionDiaSemana;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.promocion.PromocionPorCantidad;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.TarjetaVirtual;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.CategoriaProducto;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Combo;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Producto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Set;

// Datos de prueba para demostrar el funcionamiento de la aplicación
public class DatosIniciales {

    private DatosIniciales() { }

    public static void cargar(Cine cine) {
        try {
            cargarUsuarios(cine);
            cargarCartelera(cine);
            cargarConfiteria(cine);
            cargarPromociones(cine);
        } catch (CinemaUQException e) {
            throw new IllegalStateException("Error cargando los datos iniciales: " + e.getMessage(), e);
        }
    }

    private static void cargarUsuarios(Cine cine) throws CinemaUQException {
        LocalDate hoy = LocalDate.now();

        TarjetaVirtual tarjetaAna = new TarjetaVirtual("CUQ-0001");
        tarjetaAna.recargar(150000);
        Cliente ana = new Cliente("1001", "Ana Gómez", "ana@cinemauq.com", "1234", hoy.minusYears(1), tarjetaAna);
        ana.getPuntos().acumular(120, hoy.minusMonths(13));   // ya vencidos
        ana.getPuntos().acumular(300, hoy.minusMonths(10));
        ana.getPuntos().acumular(500, hoy.minusMonths(2));
        cine.getUsuarios().add(ana);

        TarjetaVirtual tarjetaLuis = new TarjetaVirtual("CUQ-0002");
        tarjetaLuis.recargar(10000);   // poco saldo para probar el rechazo del pago
        cine.getUsuarios().add(new Cliente("1002", "Luis Restrepo", "luis@cinemauq.com", "1234",
                hoy.minusMonths(3), tarjetaLuis));

        cine.getUsuarios().add(new Administrador("9001", "Administrador CinemaUQ", "admin@cinemauq.com",
                "admin", hoy.minusYears(2), "EMP-001"));
    }

    private static void cargarCartelera(Cine cine) throws CinemaUQException {
        Sala sala1 = crearSala("S1", "Sala 1", 8, 12, Set.of("G6", "G7", "H6", "H7"));
        Sala sala2 = crearSala("S2", "Sala 2", 6, 10, Set.of("F1", "F2"));
        cine.getSalas().add(sala1);
        cine.getSalas().add(sala2);

        Pelicula interestelar = new Pelicula("P1", "Interestelar", "Ciencia ficción", 169, "+12");
        Pelicula dune = new Pelicula("P2", "Dune: Parte Dos", "Ciencia ficción", 166, "+13");
        Pelicula intensamente = new Pelicula("P3", "Intensamente 2", "Animación", 96, "Todos");
        Pelicula avatar = new Pelicula("P4", "Avatar: El camino del agua", "Aventura", 192, "+12");
        cine.getPeliculas().add(interestelar);
        cine.getPeliculas().add(dune);
        cine.getPeliculas().add(intensamente);
        cine.getPeliculas().add(avatar);

        LocalDate hoy = LocalDate.now();
        LocalDate proximoMartes = hoy.with(TemporalAdjusters.next(DayOfWeek.TUESDAY));

        agregarFuncion(cine, "F1", interestelar, sala1, hoy.plusDays(1).atTime(19, 15), 16000, "A1", "A2", "D5", "D6", "E7");
        agregarFuncion(cine, "F2", interestelar, sala2, proximoMartes.atTime(16, 0), 14000, "C3", "C4");
        agregarFuncion(cine, "F3", dune, sala1, hoy.plusDays(2).atTime(20, 30), 18000, "B4", "B5", "B6", "F8");
        agregarFuncion(cine, "F4", dune, sala2, hoy.plusDays(4).atTime(22, 0), 15000);
        agregarFuncion(cine, "F5", intensamente, sala2, hoy.plusDays(1).atTime(15, 0), 12000, "A5", "A6", "A7");
        agregarFuncion(cine, "F6", intensamente, sala1, proximoMartes.atTime(11, 0), 12000);
        agregarFuncion(cine, "F7", avatar, sala1, hoy.plusDays(3).atTime(18, 0), 17000, "H1", "H2", "H3");
    }

    // Crea los asientos de la sala por filas (A, B, C...) y números
    private static Sala crearSala(String referencia, String nombre, int filas, int columnas, Set<String> accesibles) {
        Sala sala = new Sala(referencia, nombre);
        for (int f = 0; f < filas; f++) {
            char fila = (char) ('A' + f);
            for (int n = 1; n <= columnas; n++) {
                String ref = "" + fila + n;
                TipoAsiento tipo = accesibles.contains(ref) ? TipoAsiento.ACCESIBLE : TipoAsiento.NORMAL;
                sala.agregarAsiento(new Asiento(ref, n, fila, tipo));
            }
        }
        return sala;
    }

    private static void agregarFuncion(Cine cine, String ref, Pelicula pelicula, Sala sala, LocalDateTime fecha,
                                       double precio, String... ocupados) throws CinemaUQException {
        Funcion funcion = new Funcion(ref, pelicula, sala, fecha, precio, EstadoFuncion.PROGRAMADA);
        for (String asiento : ocupados) {
            funcion.reservarAsiento(sala.buscarAsiento(asiento));
        }
        cine.getFunciones().add(funcion);
    }

    private static void cargarConfiteria(Cine cine) {
        Producto crispetas = new Producto("C1", "Crispetas grandes", CategoriaProducto.COMIDA, 15000, 50, 150);
        Producto gaseosa = new Producto("C2", "Gaseosa 22 oz", CategoriaProducto.BEBIDA, 8000, 80, 80);
        Producto perro = new Producto("C3", "Perro caliente", CategoriaProducto.COMIDA, 12000, 30, 120);
        Producto nachos = new Producto("C4", "Nachos con queso", CategoriaProducto.COMIDA, 14000, 25, 140);
        Producto agua = new Producto("C5", "Agua 600 ml", CategoriaProducto.BEBIDA, 5000, 60, 50);
        Producto chocolatina = new Producto("C6", "Chocolatina", CategoriaProducto.DULCE, 5000, 40, 50);

        Combo comboPareja = new Combo("K1", "Combo Pareja", 10)
                .agregar(crispetas).agregar(gaseosa).agregar(gaseosa);
        Combo comboPerro = new Combo("K2", "Combo Perro", 10)
                .agregar(perro).agregar(gaseosa);
        // Un combo que contiene otro combo (composición recursiva)
        Combo comboFamiliar = new Combo("K3", "Combo Familiar", 15)
                .agregar(comboPareja).agregar(nachos).agregar(chocolatina).agregar(chocolatina);

        cine.getConfiteria().add(crispetas);
        cine.getConfiteria().add(gaseosa);
        cine.getConfiteria().add(perro);
        cine.getConfiteria().add(nachos);
        cine.getConfiteria().add(agua);
        cine.getConfiteria().add(chocolatina);
        cine.getConfiteria().add(comboPareja);
        cine.getConfiteria().add(comboPerro);
        cine.getConfiteria().add(comboFamiliar);
    }

    private static void cargarPromociones(Cine cine) {
        cine.getPromociones().add(new PromocionDiaSemana("Martes de cine (20%)", DayOfWeek.TUESDAY, 20));
        cine.getPromociones().add(new PromocionPorCantidad("Plan grupal (4+ entradas)", 4, 10000));
    }
}
