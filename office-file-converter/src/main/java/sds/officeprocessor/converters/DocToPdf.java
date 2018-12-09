package sds.officeprocessor.converters;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import java.io.*;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class DocToPdf implements IConvert {

    @Override
    public InputStream Convert(InputStream stream) {

        try {
            POIFSFileSystem fs = new POIFSFileSystem(stream);
            Document document = new Document();

            HWPFDocument doc = new HWPFDocument(fs);
            WordExtractor we = new WordExtractor(doc);

            ByteArrayOutputStream result = new ByteArrayOutputStream();

            PdfWriter writer = PdfWriter.getInstance(document, result);

            document.open();
            writer.setPageEmpty(true);
            document.newPage();
            writer.setPageEmpty(true);

            String[] paragraphs = we.getParagraphText();
            for (int i = 0; i < paragraphs.length; i++) {
                paragraphs[i] = paragraphs[i].replaceAll("\\cM?\r?\n", "");

                document.add(new Paragraph(paragraphs[i]));
            }
            
            document.close();
            
            return new ByteArrayInputStream(result.toByteArray());

        } catch (DocumentException | IOException e) {
            System.out.println("Exception during test");
            e.printStackTrace();
        }
        return null;
    }

}
