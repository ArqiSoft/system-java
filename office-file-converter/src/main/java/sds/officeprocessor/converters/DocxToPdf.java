package sds.officeprocessor.converters;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.xwpf.converter.core.IXWPFConverter;
import org.apache.poi.xwpf.converter.core.XWPFConverterException;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class DocxToPdf implements IConvert {

    @Override
    public InputStream Convert(InputStream stream) {

        try {
            
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            XWPFDocument document = new XWPFDocument(stream);
            PdfOptions options = PdfOptions.create();

            IXWPFConverter<PdfOptions> instance = PdfConverter.getInstance();

            instance.convert(document, out, options);
            
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException | XWPFConverterException ex) {
            Logger.getLogger(DocxToPdf.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
