package ua.kiev.univ.linguistics;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ua.kiev.univ.jms.impl.InputQueueGateway;
import ua.kiev.univ.jms.impl.OutputQueueGateway;
import ua.kiev.univ.jms.impl.ProcessedDocument;
import ua.kiev.univ.jms.impl.RawDocument;
import ua.kiev.univ.linguistics.convert.RawDocumentConverter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author sshyiko@luxoft.com
 * @version Nov 21, 2010
 */
public class Main {

    public static final String EXAMPLESDIR = "~/proj/DocumentSystematizer/archive/";
    private static final Logger logger = Logger.getLogger(Main.class);


    public static void main(String[] args) throws IOException{
        logger.info("Initializing. Loading examples from " + EXAMPLESDIR);
        Clusterizer clust = new Clusterizer(EXAMPLESDIR);
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
            // TODO: Stas, change null to appropriate values
            ProcessedDocument processedDocument = new ProcessedDocument(null, null, null);
            processedDocument.setGroup(clust.process(file));             // setting group is only here

            outputQueueGateway.push(processedDocument);
        }
    }
}
