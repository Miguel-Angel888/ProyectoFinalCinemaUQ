package co.edu.uniquindio.poo.proyectofinalcinemauq.model.reembolso_Strategy;
import co.edu.uniquindio.poo.proyectofinalcinemauq.model.cartelera.Funcion;

import java.time.LocalDateTime;
public class PoliticaSinReembolso implements PoliticaReembolso{
    @Override
    public boolean aplica(Funcion funcion, LocalDateTime ahora) {
        return true;
    }

    @Override
    public double getPorcentaje() { return 0; }

    @Override
    public String getNombre() { return "Sin reembolso"; }
}
