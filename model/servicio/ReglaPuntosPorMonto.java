package co.edu.uniquindio.poo.proyectofinalcinemauq.model.servicio;

//sistema de puntos con una interfaz sencilla. Por cada $1000 gastados, gana un punto
public class ReglaPuntosPorMonto implements ReglaAcumulacionPuntos {

    private static final double VALOR_POR_PUNTO = 1000;
    @Override
    public int calcular(double total) {

        return (int) (total / VALOR_POR_PUNTO);
    }


}
