package co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte;

public class ReportePdfFactory implements ReporteFactory {

    @Override
    public Reporte crearReporte(DatosReporte datos) {
        return new ReportePdf(datos);
    }
}
