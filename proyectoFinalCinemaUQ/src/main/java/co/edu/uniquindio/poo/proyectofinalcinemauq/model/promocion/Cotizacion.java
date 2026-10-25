package co.edu.uniquindio.poo.proyectofinalcinemauq.model.promocion;

import java.util.List;

// PATRÓN DECORATOR - Componente
// Representa el valor a pagar de una compra. Los decoradores lo envuelven
// para ir aplicando descuentos uno encima del otro.
public interface Cotizacion {

    double calcularTotal();

    // Cada descuento aplicado, en el orden en que se aplicaron
    List<DescuentoAplicado> getDescuentosAplicados();
}
