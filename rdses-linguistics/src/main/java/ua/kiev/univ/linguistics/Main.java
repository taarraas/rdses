package ua.kiev.univ.linguistics;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ua.kiev.univ.jms.impl.InputQueueGateway;
import ua.kiev.univ.jms.impl.OutputQueueGateway;
import ua.kiev.univ.jms.impl.ProcessedDocument;
import ua.kiev.univ.jms.impl.RawDocument;
import ua.kiev.univ.linguistics.convert.RawDocumentConverter;

import java.io.*;

/**
 * @author jamanal
 * @version Nov 21, 2010
 */
public class Main {

    private static final Logger logger = Logger.getLogger(Main.class);

    private static String getTemplatesDir() {
        File dir = new File("templates");
        if (dir.isDirectory())
            return dir.getAbsolutePath();
        dir = new File("build/linguistics/templates");
        if (dir.isDirectory())
            return dir.getAbsolutePath();
        throw new RuntimeException("Failed to determine templates directory path");
    }

    public static void main(String[] args) throws IOException {
        String templatesDirectory = getTemplatesDir();
        logger.info("Initializing. Loading examples from " + templatesDirectory);
        Clusterizer clust = new Clusterizer(templatesDirectory);
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-linguistics-module.xml");
        InputQueueGateway inputQueueGateway = context.getBean(InputQueueGateway.class);
        OutputQueueGateway outputQueueGateway = context.getBean(OutputQueueGateway.class);
        RawDocumentConverter converter = context.getBean(RawDocumentConverter.class);
        RawDocument rawDocument;
        while ((rawDocument = inputQueueGateway.pull()) != null) {
            logger.info("Processing file " + rawDocument.getName() + " from " + rawDocument.getSource() + "...");
            File file = null;
            if (!rawDocument.getName().toLowerCase().endsWith(".doc"))
                file = converter.convert(rawDocument);
            else {
                try {
                    file = File.createTempFile("rdses", "output");
                    FileOutputStream outputStream = new FileOutputStream(file);
                    try {
                        IOUtils.write(rawDocument.getData(), outputStream);
                    } finally {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
            if (file == null) {
                logger.warn("Failed to convert document " + rawDocument.getName() + " from " + rawDocument.getSource());
                continue;
            }
            String group = clust.process(file);
            FileInputStream inputStream = new FileInputStream(file);
            byte[] data;
            try {
                data = IOUtils.toByteArray(inputStream);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
            ProcessedDocument processedDocument =
                    new ProcessedDocument(rawDocument.getName(), data, rawDocument.getSource(), group);
            outputQueueGateway.push(processedDocument);
        }
    }
}
