package co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportePdf extends Reporte {

    private static final float MARGEN = 50;
    private static final float TAMANO_FUENTE = 10;
    private static final float INTERLINEADO = 15;
    private static final int MAX_CARACTERES = 80;   // ancho útil de A4 con Courier 10

    public ReportePdf(String titulo, LocalDate desde, LocalDate hasta,
                      List<String> lineas, double total) {
        super(titulo, desde, hasta, lineas, total);
    }

    @Override
    public void exportar(String ruta) throws IOException {
        List<String> lineasPdf = prepararLineas(construirTexto());

        try (PDDocument documento = new PDDocument()) {
            PDType1Font fuente = new PDType1Font(Standard14Fonts.FontName.COURIER);
            PDPageContentStream contenido = null;
            float y = 0;

            for (String linea : lineasPdf) {
                // Página nueva al inicio o cuando se acaba el espacio
                if (contenido == null || y < MARGEN) {
                    if (contenido != null) contenido.close();
                    PDPage pagina = new PDPage(PDRectangle.A4);
                    documento.addPage(pagina);
                    contenido = new PDPageContentStream(documento, pagina);
                    y = pagina.getMediaBox().getHeight() - MARGEN;
                }
                contenido.beginText();
                contenido.setFont(fuente, TAMANO_FUENTE);
                contenido.newLineAtOffset(MARGEN, y);
                contenido.showText(linea);
                contenido.endText();
                y -= INTERLINEADO;
            }

            if (contenido != null) contenido.close();
            documento.save(ruta);
        }
    }

    // PDFBox no acepta saltos de línea ni tabulaciones dentro de showText
    // entoncse se separa el texto en líneas y se parten las muy largas
    private List<String> prepararLineas(String texto) {
        List<String> resultado = new ArrayList<>();
        for (String linea : texto.replace("\r", "").replace("\t", "    ").split("\n", -1)) {
            while (linea.length() > MAX_CARACTERES) {
                resultado.add(linea.substring(0, MAX_CARACTERES));
                linea = linea.substring(MAX_CARACTERES);
            }
            resultado.add(linea);
        }
        return resultado;
    }
}