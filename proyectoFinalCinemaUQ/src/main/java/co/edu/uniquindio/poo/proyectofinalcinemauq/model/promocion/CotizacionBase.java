package co.edu.uniquindio.poo.proyectofinalcinemauq.model.promocion;

import java.util.ArrayList;
import java.util.List;

// PATRÓN DECORATOR - Componente concreto: el subtotal sin descuentos
public class CotizacionBase implements Cotizacion {

    private final double subtotal;

    public CotizacionBase(double subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public double calcularTotal() {
        return subtotal;
    }

    @Override
    public List<DescuentoAplicado> getDescuentosAplicados() {
        return new ArrayList<>();
    }
}
