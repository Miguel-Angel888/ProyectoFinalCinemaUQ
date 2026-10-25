package co.edu.uniquindio.poo.proyectofinalcinemauq.model.facade;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.Cine;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.DatosIniciales;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Asiento;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Funcion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Pelicula;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException.*;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Administrador;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Cliente;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Usuario;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.promocion.Promocion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.servicio.ReglaPuntosPorMonto;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.servicio.ServicioPago;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.EstadoSolicitud;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.Pago;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.SolicitudRecarga;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.TarjetaProxy;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.TarjetaVirtual;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Compra;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.ItemConfiteria;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.LineaCompra;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.TipoEntrada;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

// PATRÓN FACADE
// Punto de entrada único del modelo para los controladores de JavaFX (MVC):
// sesión, cartelera, confitería, tarjeta y recargas. El proceso de compra
// lo delega en CompraFacade. Es Singleton para que todas las pantallas
// compartan el mismo estado.
public class CinemaFacade {

    public static final int MAX_ENTRADAS_POR_COMPRA = CompraFacade.MAX_ENTRADAS_POR_COMPRA;

    private static CinemaFacade instancia;

    private final Cine cine;
    private final ServicioPago servicioPago;
    private final CompraFacade compraFacade;
    private Usuario usuarioActual;

    private CinemaFacade() {
        this.cine = new Cine("CinemaUQ");
        DatosIniciales.cargar(cine);
        this.servicioPago = new ServicioPago(new ReglaPuntosPorMonto());
        this.compraFacade = new CompraFacade(servicioPago);
    }

    public static CinemaFacade getInstance() {
        if (instancia == null) {
            instancia = new CinemaFacade();
        }
        return instancia;
    }

    // ---------------- Sesión ----------------

    public Usuario iniciarSesion(String correo, String contrasenia) throws AutenticacionException {
        if (correo == null || correo.isBlank() || contrasenia == null || contrasenia.isBlank()) {
            throw new AutenticacionException("Ingresa el correo y la contraseña");
        }
        for (Usuario u : cine.getUsuarios()) {
            if (u.getCorreo().equalsIgnoreCase(correo.trim()) && u.getContrasenia().equals(contrasenia)) {
                usuarioActual = u;
                return u;
            }
        }
        throw new AutenticacionException("Correo o contraseña incorrectos");
    }

    public Cliente registrarCliente(String nombre, String correo, String contrasenia) throws AutenticacionException {
        if (nombre == null || nombre.isBlank() || correo == null || correo.isBlank()
                || contrasenia == null || contrasenia.isBlank()) {
            throw new AutenticacionException("Todos los campos son obligatorios");
        }
        if (!correo.contains("@")) {
            throw new AutenticacionException("El correo no es válido");
        }
        for (Usuario u : cine.getUsuarios()) {
            if (u.getCorreo().equalsIgnoreCase(correo.trim())) {
                throw new AutenticacionException("Ya existe una cuenta con ese correo");
            }
        }
        String id = String.valueOf(1000 + cine.getUsuarios().size() + 1);
        Cliente cliente = new Cliente(id, nombre.trim(), correo.trim(), contrasenia, LocalDate.now(),
                new TarjetaVirtual("CUQ-" + id));
        cine.getUsuarios().add(cliente);
        usuarioActual = cliente;
        return cliente;
    }

    public void cerrarSesion() {
        usuarioActual = null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public Cliente getClienteActual() throws AutenticacionException {
        if (usuarioActual instanceof Cliente cliente) return cliente;
        throw new AutenticacionException("Debes iniciar sesión como cliente");
    }

    // ---------------- Cartelera ----------------

    public List<Pelicula> getPeliculasEnCartelera() {
        List<Pelicula> resultado = new ArrayList<>();
        for (Pelicula p : cine.getPeliculas()) {
            if (p.isEnCartelera()) resultado.add(p);
        }
        return resultado;
    }

    public List<Funcion> getFuncionesDisponibles(Pelicula pelicula) {
        List<Funcion> resultado = new ArrayList<>();
        for (Funcion f : cine.getFunciones()) {
            if (f.getPelicula() == pelicula && f.estaProgramada()) resultado.add(f);
        }
        resultado.sort(Comparator.comparing(Funcion::getFechaFuncion));
        return resultado;
    }

    public double calcularPrecioEntrada(Funcion funcion, TipoEntrada tipo) {
        return compraFacade.calcularPrecioEntrada(funcion, tipo);
    }

    // ---------------- Confitería y promociones ----------------

    public List<ItemConfiteria> getConfiteria() {
        return new ArrayList<>(cine.getConfiteria());
    }

    public List<Promocion> getPromociones() {
        return new ArrayList<>(cine.getPromociones());
    }

    // ---------------- Compra (delegada en CompraFacade) ----------------

    public Compra prepararCompra(Funcion funcion, List<Asiento> asientos, TipoEntrada tipo,
                                 Map<ItemConfiteria, Integer> confiteria) throws CinemaUQException {
        List<LineaCompra> productos = new ArrayList<>();
        for (Map.Entry<ItemConfiteria, Integer> e : confiteria.entrySet()) {
            productos.add(new LineaCompra(e.getKey(), e.getValue()));
        }
        return compraFacade.cotizar(getClienteActual(), funcion, asientos, tipo, productos, cine.getPromociones());
    }

    public Pago pagarCompra(Compra compra) throws CinemaUQException {
        if (compra.getCliente() != getClienteActual()) {
            throw new CompraInvalidaException("La compra no pertenece al cliente en sesión");
        }
        Pago pago = compraFacade.confirmar(compra);
        cine.getCompras().add(compra);
        return pago;
    }

    public int calcularPuntos(double total) {
        return servicioPago.calcularPuntos(total);
    }

    // ---------------- Tarjeta y puntos ----------------

    public TarjetaProxy getMedioPago(Cliente cliente) {
        return compraFacade.getMedioPago(cliente);
    }

    public int getPuntosDisponibles(Cliente cliente) {
        return cliente.getPuntos().getPuntosDisponibles(LocalDate.now());
    }

    public SolicitudRecarga solicitarRecarga(double monto) throws CinemaUQException {
        SolicitudRecarga solicitud = new SolicitudRecarga(getClienteActual(), monto);
        cine.getSolicitudesRecarga().add(solicitud);
        return solicitud;
    }

    public List<SolicitudRecarga> getSolicitudesCliente(Cliente cliente) {
        List<SolicitudRecarga> resultado = new ArrayList<>();
        for (SolicitudRecarga s : cine.getSolicitudesRecarga()) {
            if (s.getCliente() == cliente) resultado.add(s);
        }
        return resultado;
    }

    // ---------------- Administrador ----------------

    public List<SolicitudRecarga> getSolicitudesPendientes() {
        List<SolicitudRecarga> resultado = new ArrayList<>();
        for (SolicitudRecarga s : cine.getSolicitudesRecarga()) {
            if (s.getEstadoSolicitud() == EstadoSolicitud.PENDIENTE) resultado.add(s);
        }
        return resultado;
    }

    // Solo el administrador puede recargar; la recarga queda en los movimientos de la tarjeta
    public void aprobarRecarga(SolicitudRecarga solicitud) throws CinemaUQException {
        validarAdministrador();
        if (solicitud.getEstadoSolicitud() != EstadoSolicitud.PENDIENTE) {
            throw new SolicitudInvalidaException("La solicitud ya fue procesada");
        }
        // Primero se recarga: si la tarjeta está inactiva la solicitud sigue pendiente
        solicitud.getCliente().getTarjetaVirtual().recargar(solicitud.getCantidad());
        solicitud.aprobar();
    }

    public void rechazarRecarga(SolicitudRecarga solicitud) throws CinemaUQException {
        validarAdministrador();
        solicitud.rechazar();
    }

    public List<Cliente> getClientes() {
        List<Cliente> resultado = new ArrayList<>();
        for (Usuario u : cine.getUsuarios()) {
            if (u instanceof Cliente c) resultado.add(c);
        }
        return resultado;
    }

    private void validarAdministrador() throws AutenticacionException {
        if (!(usuarioActual instanceof Administrador)) {
            throw new AutenticacionException("Solo el administrador puede realizar esta operación");
        }
    }
}
