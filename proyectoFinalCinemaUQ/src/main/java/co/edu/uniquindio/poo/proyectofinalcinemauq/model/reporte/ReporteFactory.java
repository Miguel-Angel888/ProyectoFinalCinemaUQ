package co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte;

// PATRÓN FACTORY METHOD
// Quien exporta solo conoce esta interfaz; cada fábrica concreta decide
// qué tipo de Reporte (TXT o PDF) se crea.
public interface ReporteFactory {
    Reporte crearReporte(DatosReporte datos);
}
