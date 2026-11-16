package co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte;

// Reportes que el administrador puede consultar, exportar y ver como gráfico
public enum TipoReporte {
    VENTAS("Ventas por periodo", TipoGrafico.LINEAS),
    ENTRADAS("Entradas vendidas por película y función", TipoGrafico.PASTEL),
    OCUPACION("Ocupación de salas", TipoGrafico.BARRAS),
    CONFITERIA("Productos y combos vendidos", TipoGrafico.BARRAS),
    RECARGAS("Recargas realizadas", TipoGrafico.BARRAS),
    CANCELACIONES("Cancelaciones y reembolsos", TipoGrafico.PASTEL),
    PUNTOS("Puntos acumulados y redimidos", TipoGrafico.BARRAS);

    private final String titulo;
    private final TipoGrafico tipoGrafico;

    TipoReporte(String titulo, TipoGrafico tipoGrafico) {
        this.titulo = titulo;
        this.tipoGrafico = tipoGrafico;
    }

    public String getTitulo() { return titulo; }
    public TipoGrafico getTipoGrafico() { return tipoGrafico; }

    @Override
    public String toString() { return titulo; }
}
