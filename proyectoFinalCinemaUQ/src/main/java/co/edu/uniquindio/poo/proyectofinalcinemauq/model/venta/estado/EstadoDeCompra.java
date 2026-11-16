package co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.estado;

import co.edu.uniquindio.poo.proyectofinalcinemauq.model.exceptions.CinemaUQException.CompraInvalidaException;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta.EstadoCompra;

// PATRÓN STATE - Estado
// Cada estado decide qué operaciones admite la compra y a qué estado pasa.
// La compra (contexto) no usa if/switch sobre su estado: le delega la decisión.
//
//   PENDIENTE --pagar--> PAGADA --reembolsar--> REEMBOLSADA
//       |
//       +--cancelar--> CANCELADA
public interface EstadoDeCompra {

    // Valor que se muestra en la interfaz y se usa en los reportes
    EstadoCompra getTipo();

    // Cada transición devuelve el siguiente estado o lanza la excepción si no es válida
    EstadoDeCompra pagar() throws CompraInvalidaException;

    EstadoDeCompra reembolsar() throws CompraInvalidaException;

    EstadoDeCompra cancelar() throws CompraInvalidaException;

    // true si el cliente o el cine todavía pueden pedir que se devuelva el dinero
    boolean admiteReembolso();

    // true si en algún momento se le cobró al cliente (cuenta como venta en los reportes)
    boolean fueCobrada();
}
