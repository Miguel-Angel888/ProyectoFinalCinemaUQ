package co.edu.uniquindio.poo.proyectofinalcinemauq.model.recompensa;

// Misma idea que ReglaAcumulacionPuntos, pero en sentido contrario:
// dado el valor en pesos de algo, dice cuántos puntos cuesta canjearlo
public interface ReglaCostoRedencion {

    int calcular(double valor);
}
