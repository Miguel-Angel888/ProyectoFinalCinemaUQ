package co.edu.uniquindio.poo.proyectofinalcinemauq.model.reporte;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ReportePdf extends Reporte {

    private static final float MARGEN = 40;
    private static final float TAMANO_TITULO = 12;
    private static final float TAMANO_FUENTE = 8;
    private static final float INTERLINEADO = 11;
    private static final int MAX_CARACTERES = 105;   // ancho útil de A4 con Courier 8

    public ReportePdf(DatosReporte datos) {
        super(datos);
    }

    @Override
    public void exportar(Path archivo) throws IOException {
        List<String> lineasPdf = prepararLineas(construirLineas());

        try (PDDocument documento = new PDDocument()) {
            PDType1Font fuente = new PDType1Font(Standard14Fonts.FontName.COURIER);
            PDType1Font fuenteTitulo = new PDType1Font(Standard14Fonts.FontName.COURIER_BOLD);
            PDPageContentStream contenido = null;
            float y = 0;

            for (int i = 0; i < lineasPdf.size(); i++) {
                // Página nueva al inicio o cuando se acaba el espacio
                if (contenido == null || y < MARGEN) {
                    if (contenido != null) contenido.close();
                    PDPage pagina = new PDPage(PDRectangle.A4);
                    documento.addPage(pagina);
                    contenido = new PDPageContentStream(documento, pagina);
                    y = pagina.getMediaBox().getHeight() - MARGEN;
                }
                boolean esTitulo = i == 0;
                contenido.beginText();
                contenido.setFont(esTitulo ? fuenteTitulo : fuente, esTitulo ? TAMANO_TITULO : TAMANO_FUENTE);
                contenido.newLineAtOffset(MARGEN, y);
                contenido.showText(lineasPdf.get(i));
                contenido.endText();
                y -= esTitulo ? INTERLINEADO * 1.6f : INTERLINEADO;
            }

            if (contenido != null) contenido.close();
            documento.save(archivo.toFile());
        }
    }

    // PDFBox no acepta saltos de línea ni tabulaciones dentro de showText,
    // y la fuente estándar solo tiene caracteres latinos: se limpia y se parten las líneas largas
    private List<String> prepararLineas(List<String> lineas) {
        List<String> resultado = new ArrayList<>();
        for (String original : lineas) {
            String linea = limpiar(original);
            while (linea.length() > MAX_CARACTERES) {
                resultado.add(linea.substring(0, MAX_CARACTERES));
                linea = linea.substring(MAX_CARACTERES);
            }
            resultado.add(linea);
        }
        return resultado;
    }

    private static String limpiar(String texto) {
        StringBuilder sb = new StringBuilder(texto.length());
        for (char c : texto.toCharArray()) {
            if (c == '\t') sb.append("    ");
            else if (Character.isWhitespace(c) || Character.isSpaceChar(c)) sb.append(' ');   // incluye espacios no separables
            else if ((c >= 0x20 && c <= 0x7E) || (c >= 0xA1 && c <= 0xFF)) sb.append(c);
            else sb.append('?');
        }
        return sb.toString();
    }
}
