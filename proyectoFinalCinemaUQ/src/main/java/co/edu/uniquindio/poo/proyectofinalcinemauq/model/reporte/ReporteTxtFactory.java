package co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte;

public class ReporteTxtFactory implements ReporteFactory {

    @Override
    public Reporte crearReporte(DatosReporte datos) {
        return new ReporteTxt(datos);
    }
}
