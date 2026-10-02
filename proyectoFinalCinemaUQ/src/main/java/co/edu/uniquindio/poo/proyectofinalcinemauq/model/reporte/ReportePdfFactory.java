package co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte;

public class ReportePdfFactory implements ReporteFactory{

    @Override
    public String generarReporte() {
        return "Reporte de pdfs";
    }
}
