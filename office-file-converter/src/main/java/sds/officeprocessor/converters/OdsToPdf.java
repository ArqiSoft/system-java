package sds.officeprocessor.converters;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Graphics2D;
import java.io.*;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.jopendocument.model.OpenDocument;
import org.jopendocument.renderer.ODTRenderer;

public class OdsToPdf implements IConvert {

    @Override
    public InputStream Convert(InputStream stream) {

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            File directory = new File(System.getenv("OSDR_TEMP_FILES_FOLDER"));
            
            File tempFile = File.createTempFile("temp", ".ods", directory);
            
            try (FileOutputStream tempOut = new FileOutputStream(tempFile)) {
                IOUtils.copy(stream, tempOut);
            }
            
            OpenDocument doc = new OpenDocument(tempFile);
            
            Document document = new Document(PageSize.A4);
            
            PdfDocument pdf = new PdfDocument();

            document.addDocListener(pdf);

            PdfWriter writer = PdfWriter.getInstance(pdf, out);
            pdf.addWriter(writer);

            document.open();

            Rectangle pageSize = document.getPageSize();
            int w = (int) (pageSize.getWidth() * 0.9);
            int h = (int) (pageSize.getHeight() * 0.95);
            PdfContentByte cb = writer.getDirectContent();
            PdfTemplate tp = cb.createTemplate(w, h);

            Graphics2D g2 = tp.createPrinterGraphics(w, h, null);

            tp.setWidth(w);
            tp.setHeight(h);

            ODTRenderer renderer = new ODTRenderer(doc);
            renderer.setIgnoreMargins(true);
            renderer.setPaintMaxResolution(true);

            renderer.setResizeFactor(renderer.getPrintWidth() / w);
            renderer.paintComponent(g2);
            g2.dispose();

            float offsetX = (pageSize.getWidth() - w) / 2;
            float offsetY = (pageSize.getHeight() - h) / 2;
            cb.addTemplate(tp, offsetX, offsetY);
            document.close();
            Files.delete(tempFile.toPath());
            return new ByteArrayInputStream(out.toByteArray());

        } catch (DocumentException | IOException ex) {
            Logger.getLogger(OdsToPdf.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
