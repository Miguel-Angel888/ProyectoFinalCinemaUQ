package co.edu.uniquindio.poo.proyectofinalcinemauq.model.facade;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.Cine;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.DatosIniciales;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Asiento;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Funcion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Pelicula;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.eventos.EventoCine;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.eventos.NotificadorCliente;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.eventos.PublicadorEventos;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.eventos.RegistroEventos;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.eventos.TipoEvento;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException.*;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Administrador;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Cliente;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.perfil.Usuario;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.promocion.Promocion;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.promocion.PromocionBonoEntrada;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.recompensa.Canje;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.recompensa.Recompensa;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.recompensa.ReglaCostoPorValor;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.recompensa.TipoRecompensa;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.servicio.ReglaPuntosPorMonto;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.servicio.ServicioCanje;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.servicio.ServicioPago;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.servicio.ServicioReembolso;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.EstadoSolicitud;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.Pago;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.Reembolso;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.SolicitudRecarga;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.TarjetaProxy;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.tarjeta.TarjetaVirtual;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.Compra;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.ItemConfiteria;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.LineaCompra;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.TipoEntrada;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final PublicadorEventos eventos;
    private final RegistroEventos registroEventos;
    private final ServicioPago servicioPago;
    private final ServicioCanje servicioCanje;
    private final ServicioReembolso servicioReembolso;
    private final CompraFacade compraFacade;
    private Usuario usuarioActual;

    private CinemaFacade() {
        this.cine = new Cine("CinemaUQ");
        DatosIniciales.cargar(cine);

        // PATRÓN OBSERVER: un solo publicador para todo el modelo. Cada evento llega
        // al cliente como notificación y queda guardado en el registro del cine.
        this.eventos = new PublicadorEventos();
        this.registroEventos = new RegistroEventos();
        eventos.suscribir(new NotificadorCliente());
        eventos.suscribir(registroEventos);

        this.servicioPago = new ServicioPago(new ReglaPuntosPorMonto());
        this.servicioCanje = new ServicioCanje(new ReglaCostoPorValor(), eventos);
        this.servicioReembolso = new ServicioReembolso(eventos);
        this.compraFacade = new CompraFacade(servicioPago, eventos);
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
        return prepararCompra(funcion, asientos, tipo, confiteria, null);
    }

    // Igual que la anterior, pero el cliente puede usar un bono de entrada que canjeó con puntos
    public Compra prepararCompra(Funcion funcion, List<Asiento> asientos, TipoEntrada tipo,
                                 Map<ItemConfiteria, Integer> confiteria, Canje bono) throws CinemaUQException {
        Cliente cliente = getClienteActual();
        List<LineaCompra> productos = new ArrayList<>();
        for (Map.Entry<ItemConfiteria, Integer> e : confiteria.entrySet()) {
            productos.add(new LineaCompra(e.getKey(), e.getValue()));
        }
        List<Promocion> promociones = new ArrayList<>(cine.getPromociones());
        if (bono != null) {
            if (!cliente.getListaCanjes().contains(bono)
                    || bono.getRecompensa().getTipo() != TipoRecompensa.ENTRADA || !bono.estaDisponible()) {
                throw new CompraInvalidaException("El bono seleccionado no está disponible");
            }
            promociones.add(new PromocionBonoEntrada(bono, calcularPrecioEntrada(funcion, tipo)));
        }
        return compraFacade.cotizar(cliente, funcion, asientos, tipo, productos, promociones);
    }

    // Permite avisar al cliente desde la cartelera, antes de elegir asientos
    public void validarHorarioDisponible(Funcion funcion) throws CinemaUQException {
        compraFacade.validarSinCruceDeHorario(getClienteActual(), funcion);
    }

    public Pago pagarCompra(Compra compra) throws CinemaUQException {
        if (compra.getCliente() != getClienteActual()) {
            throw new CompraInvalidaException("La compra no pertenece al cliente en sesión");
        }
        Pago pago = compraFacade.confirmar(compra);
        // El bono se gasta solo cuando el pago salió bien
        for (Promocion p : compra.getPromociones()) {
            if (p instanceof PromocionBonoEntrada bono) bono.getBono().marcarUsado();
        }
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

    public int getPuntosPorVencer(Cliente cliente, int dias) {
        return cliente.getPuntos().getPuntosPorVencer(LocalDate.now(), dias);
    }

    // ---------------- Recompensas y canje de puntos ----------------

    public List<Recompensa> getCatalogoRecompensas() {
        return servicioCanje.armarCatalogo(cine.getConfiteria());
    }

    public Canje canjearPuntos(Recompensa recompensa) throws CinemaUQException {
        return servicioCanje.canjear(getClienteActual(), recompensa);
    }

    // Bonos de entrada canjeados que todavía no se han gastado
    public List<Canje> getBonosEntradaDisponibles(Cliente cliente) {
        List<Canje> resultado = new ArrayList<>();
        for (Canje c : cliente.getListaCanjes()) {
            if (c.getRecompensa().getTipo() == TipoRecompensa.ENTRADA && c.estaDisponible()) resultado.add(c);
        }
        return resultado;
    }

    // Vales de confitería canjeados que todavía no se han entregado (los ve el administrador)
    public List<Canje> getValesConfiteriaPendientes() {
        List<Canje> resultado = new ArrayList<>();
        for (Cliente c : getClientes()) {
            for (Canje canje : c.getListaCanjes()) {
                if (canje.getRecompensa().getTipo() == TipoRecompensa.CONFITERIA && canje.estaDisponible()) {
                    resultado.add(canje);
                }
            }
        }
        return resultado;
    }

    // El personal de confitería marca el vale como entregado
    public void marcarCanjeUsado(Canje canje) throws CinemaUQException {
        validarAdministrador();
        canje.marcarUsado();
    }

    // ---------------- Cancelación y reembolso ----------------

    // 0 significa que la compra ya no se puede cancelar
    public double getPorcentajeReembolso(Compra compra) {
        return servicioReembolso.porcentajeAplicable(compra, LocalDateTime.now());
    }

    public Reembolso cancelarCompra(Compra compra) throws CinemaUQException {
        if (compra.getCliente() != getClienteActual()) {
            throw new CompraInvalidaException("La compra no pertenece al cliente en sesión");
        }
        return servicioReembolso.reembolsar(compra, compraFacade.getMedioPago(compra.getCliente()), LocalDateTime.now());
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
        eventos.publicar(new EventoCine(TipoEvento.RECARGA_APROBADA, solicitud.getCliente(),
                "Tu recarga " + solicitud.getReferencia() + " por $" + String.format("%,.0f", solicitud.getCantidad())
                        + " fue aprobada."));
    }

    public void rechazarRecarga(SolicitudRecarga solicitud) throws CinemaUQException {
        validarAdministrador();
        solicitud.rechazar();
        eventos.publicar(new EventoCine(TipoEvento.RECARGA_RECHAZADA, solicitud.getCliente(),
                "Tu recarga " + solicitud.getReferencia() + " fue rechazada por el administrador."));
    }

    // ---------------- Eventos (Observer) ----------------

    public RegistroEventos getRegistroEventos() {
        return registroEventos;
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
