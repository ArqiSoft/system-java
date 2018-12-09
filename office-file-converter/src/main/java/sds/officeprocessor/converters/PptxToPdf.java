package sds.officeprocessor.converters;


import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.IOException;
import java.util.List;
import org.apache.poi.xslf.usermodel.XMLSlideShow;

public class PptxToPdf implements IConvert {

    private List<XSLFSlide> slides;

    @Override
    public InputStream Convert(InputStream stream) {

        try {
            Dimension pgsize = processSlides(stream);
            
            double zoom = 2;
            AffineTransform at = new AffineTransform();
            at.setToScale(zoom, zoom);
            
            Document document = new Document();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            
            PdfWriter writer = PdfWriter.getInstance(document, outStream);
            document.open();
            
            for (int i = 0; i < getNumSlides(); i++) {
                
                BufferedImage bufImg = new BufferedImage((int) Math.ceil(pgsize.width * zoom), (int) Math.ceil(pgsize.height * zoom), BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics = bufImg.createGraphics();
                graphics.setTransform(at);
                graphics.setPaint(getSlideBGColor(i));
                graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));
                try {
                    drawOntoThisGraphic(i, graphics);
                } catch (Exception e) {
                    
                }
                
                Image image = Image.getInstance(bufImg, null);
                document.setPageSize(new Rectangle(image.getScaledWidth(), image.getScaledHeight()));
                document.newPage();
                image.setAbsolutePosition(0, 0);
                document.add(image);
            }

            document.close();
            
            writer.close();
            
            return new ByteArrayInputStream(outStream.toByteArray());

        } catch (BadElementException | IOException ex) {
            Logger.getLogger(PptxToPdf.class.getName()).log(Level.SEVERE, null, ex);
        } catch (com.itextpdf.text.DocumentException ex) {
            Logger.getLogger(PptxToPdf.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private Dimension processSlides(InputStream stream) throws IOException {
        InputStream iStream = stream;
        XMLSlideShow ppt = new XMLSlideShow(iStream);
        Dimension dimension = ppt.getPageSize();
        slides = ppt.getSlides();
        return dimension;
    }

    private int getNumSlides() {
        return slides.size();
    }

    private void drawOntoThisGraphic(int index, Graphics2D graphics) {
        slides.get(index).draw(graphics);
    }

    private Color getSlideBGColor(int index) {
        return slides.get(index).getBackground().getFillColor();
    }

}
