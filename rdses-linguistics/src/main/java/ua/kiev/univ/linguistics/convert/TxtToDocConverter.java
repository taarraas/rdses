package ua.kiev.univ.linguistics.convert;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import ua.kiev.univ.jms.impl.RawDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hpsf.CustomProperties;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * @author jamanal
 * @version Nov 21, 2010
 */
public class TxtToDocConverter implements RawDocumentConverter {

    private static final Logger logger = Logger.getLogger(TxtToDocConverter.class);

    @Override
    public File convert(RawDocument document) {
        if (!document.getName().toLowerCase().endsWith(".txt"))
            return null;
        File result = null;
        try {
            result = File.createTempFile("rdses", "output");
            File inputFile = File.createTempFile("rdses", "input");
            FileOutputStream outputStream = new FileOutputStream(inputFile);
            try {
                IOUtils.write(document.getData(), outputStream);
            } finally {
                outputStream.close();
            }
            convertTxtToDoc(inputFile, result);
        } catch (IOException ex) {
            logger.error("Error occurred while converting document " + document.getName() + " form " + document.getSource(), ex);
            result = null;
        }
        return result;
    }

    private void convertTxtToDoc(File inputFile, File outputFile) throws IOException {
        File blankFile = new File("blank.doc");
        POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(blankFile));
        HWPFDocument doc = new HWPFDocument(fs);
        Range range = doc.getRange();
        FileInputStream inputStream = new FileInputStream(inputFile);
        String text;
        try {
            text = IOUtils.toString(inputStream);
        } finally {
            inputStream.close();
        }
        range.insertAfter(text);
        doc.write(new FileOutputStream(outputFile));
    }
}
