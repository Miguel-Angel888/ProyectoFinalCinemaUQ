package co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte;

// Cada formato de exportación sabe qué fábrica usar para crear su reporte
public enum FormatoReporte {
    TXT("txt", "Texto plano (*.txt)", new ReporteTxtFactory()),
    PDF("pdf", "Documento PDF (*.pdf)", new ReportePdfFactory());

    private final String extension;
    private final String descripcion;
    private final ReporteFactory fabrica;

    FormatoReporte(String extension, String descripcion, ReporteFactory fabrica) {
        this.extension = extension;
        this.descripcion = descripcion;
        this.fabrica = fabrica;
    }

    public String getExtension() { return extension; }
    public String getDescripcion() { return descripcion; }
    public ReporteFactory getFabrica() { return fabrica; }
}
