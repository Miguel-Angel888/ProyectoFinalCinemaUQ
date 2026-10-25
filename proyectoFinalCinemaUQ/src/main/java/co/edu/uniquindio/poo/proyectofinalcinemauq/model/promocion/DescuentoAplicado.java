package co.edu.uniquindio.poo.proyectofinalcinemauq.model.promocion;

// Registro de un descuento que se aplicó a la compra (nombre de la promoción y valor descontado)
public class DescuentoAplicado {

    private final String nombre;
    private final double valor;

    public DescuentoAplicado(String nombre, double valor) {
        this.nombre = nombre;
        this.valor = valor;
    }

    public String getNombre() { return nombre; }
    public double getValor() { return valor; }
}
