package co.edu.uniquindio.poo.proyectofinalcinemauq.model.recompensa;

// Un punto por cada $100 de valor: entre más caro el premio, más puntos cuesta
public class ReglaCostoPorValor implements ReglaCostoRedencion {

    private static final double VALOR_POR_PUNTO = 100;

    @Override
    public int calcular(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("El valor a canjear debe ser mayor a cero");
        }
        return (int) Math.ceil(valor / VALOR_POR_PUNTO);
    }
}