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

    private static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
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
            // todo: Taras, make your magic here and return processed document (replace the following line)
            ProcessedDocument processedDocument = null;
            outputQueueGateway.push(processedDocument);
        }
    }
}
