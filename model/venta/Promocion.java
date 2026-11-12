package co.edu.uniquindio.poo.proyectofinalcinemauq.model.venta;

import java.time.LocalDate;

public interface Promocion {

    String getNombre();

    // Una promoción solo se puede usar dentro de su periodo de vigencia
    boolean estaVigente(LocalDate fecha);

    // Devuelve el valor del descuento
    double aplicar(double subtotal);
}
