package sds.officeprocessor.converters;

import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.ConverterTypeVia;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OdtToPdf implements IConvert {

    @Override
    public InputStream Convert(InputStream stream) {

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            
            IXDocReport report = XDocReportRegistry.getRegistry().loadReport(stream, TemplateEngineKind.Velocity);
            
            IContext context = report.createContext();
            
            Options options = Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.ODFDOM); // or XWPF
            
            report.convert(context, options, out);
            
            return new ByteArrayInputStream(out.toByteArray());
            
        } catch (IOException | XDocReportException ex) {
            Logger.getLogger(OdtToPdf.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
