package ua.kiev.univ.master;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import ua.kiev.univ.jms.impl.ProcessedDocument;

import javax.jms.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Observable;

/**
 * @author jamanal
 * @version Nov 27, 2010
 */
public class OutputQueueListener extends Observable implements MessageListener {

    private static final Logger logger = Logger.getLogger(OutputQueueListener.class);
    private File outputDirectory;

    public OutputQueueListener() {
        outputDirectory = new File("output");
        if (!outputDirectory.exists())
            outputDirectory = new File("build/master/output");
        if (!outputDirectory.exists())
            throw new RuntimeException("Failed to determine output directory path");
    }

    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;
        ProcessedDocument document;
        try {
            document = (ProcessedDocument) objectMessage.getObject();
        } catch (JMSException e) {
            logger.error("Caught exception", e);
            return;
        }
        File output = new File(outputDirectory, generateFileName(document));
        FileOutputStream stream = null;
        try {
            System.out.println(output.getAbsolutePath());
            output.createNewFile();
            stream = new FileOutputStream(output);
            IOUtils.write(document.getData(), stream);
        } catch (IOException e) {
            logger.error("Caught exception", e);
            return;
        } finally {
            IOUtils.closeQuietly(stream);
        }
        synchronized (this) {
            setChanged();
            notifyObservers(output);
        }
    }

    private String generateFileName(ProcessedDocument document) {
        String documentName = new File(document.getName()).getName();
        String documentExt = "";
        int i = documentName.lastIndexOf(".");
        if (i > 0) {
            documentExt = documentName.substring(i + 1);
            documentName = documentName.substring(0, i);
        }
        return (documentName + "-" + document.getGroup() +
                " (found at " + document.getSource() + ")." + documentExt).replace(" ", "_");
    }

}
