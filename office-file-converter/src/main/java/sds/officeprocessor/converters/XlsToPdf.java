package sds.officeprocessor.converters;

import java.io.InputStream;
import org.apache.poi.ss.usermodel.*;
import java.util.Iterator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class XlsToPdf implements IConvert {

    @Override
    public InputStream Convert(InputStream stream) {

        Workbook workbook;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            workbook = new HSSFWorkbook(stream);
            readSpreadSheet(workbook, bos);
            byte[] resultBytes = bos.toByteArray();
            return new ByteArrayInputStream(resultBytes);

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void readSpreadSheet(Workbook workbook, ByteArrayOutputStream bos) throws IOException,
            DocumentException {

        Document document = new Document();
        PdfWriter.getInstance(document, bos);
        document.open();

        Sheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();

        boolean flag = true;
        PdfPTable table = null;

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            int cellNumber = 0;

            if (flag) {
                table = new PdfPTable(row.getLastCellNum());
                flag = false;
            }

            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();

                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:


                        cellNumber = checkEmptyCellAndAddCellContentToPDFTable(cellNumber, cell, table);
                        
                        cellNumber++;
                        break;

                    case Cell.CELL_TYPE_NUMERIC:
                        cellNumber = checkEmptyCellAndAddCellContentToPDFTable(cellNumber, cell, table);
                        cellNumber++;
                        break;
                }
            }

            for (int i = 0; i < (cellNumber); i++) {
                table.addCell(" ");
            }
        }

        document.add(table);
        document.close();
    }

    private static int checkEmptyCellAndAddCellContentToPDFTable(int cellNumber, Cell cell, PdfPTable table) {
        if (cellNumber == cell.getColumnIndex()) {
            if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                table.addCell(Double.toString(cell.getNumericCellValue()));
            }
            if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                table.addCell(cell.getStringCellValue());
            }

        } else {
            while (cellNumber < cell.getColumnIndex()) {

                table.addCell(" ");
                cellNumber++;

            }
            if (cellNumber == cell.getColumnIndex()) {
                if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    table.addCell(Double.toString(cell.getNumericCellValue()));
                }
                if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                    table.addCell(cell.getStringCellValue());
                }

            }
            cellNumber = cell.getColumnIndex();
        }

        return cellNumber;
    }
}
