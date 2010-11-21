package ua.kiev.univ.slave;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import ua.kiev.univ.common.Network;
import ua.kiev.univ.jms.impl.InputQueueGateway;
import ua.kiev.univ.jms.impl.RawDocument;
import ua.kiev.univ.slave.to.SearchConfig;
import ua.kiev.univ.slave.to.SearchProgress;
import ua.kiev.univ.slave.to.SearchStatus;
import ua.kiev.univ.slave.to.SerializableFileFilter;

import java.io.*;
import java.util.Map;

/**
 * @author jamanal
 * @version 2010-11-08
 */
public class SearchTask implements Runnable {

    private static Logger logger = Logger.getLogger(SearchTask.class);

    private InputQueueGateway inputQueueGateway;

    private SearchConfig searchConfig;
    private volatile SearchStatus status = SearchStatus.TERMINATED;
    private volatile int numberOfScannedFiles;
    private volatile int numberOfSubmittedFiles;

    @Autowired
    public void setInputQueueGateway(InputQueueGateway inputQueueGateway) {
        this.inputQueueGateway = inputQueueGateway;
    }

    public void setSearchConfig(SearchConfig searchConfig) {
        if (status == SearchStatus.RUNNING)
            throw new IllegalStateException("Couldn't change SearchConfig of running task");
        this.searchConfig = searchConfig;
    }

    public SearchStatus getStatus() {
        return status;
    }

    public SearchProgress getProgress() {
        return new SearchProgress(numberOfScannedFiles, numberOfSubmittedFiles);
    }

    @Override
    public void run() {
        logger.info("Search started...");
        numberOfScannedFiles = 0;
        numberOfSubmittedFiles = 0;
        status = SearchStatus.RUNNING;
        try {
            for (Map.Entry<String, SerializableFileFilter> searchConfiguration : searchConfig.getConfigurations()) {
                String path = searchConfiguration.getKey();
                FileFilter filter = searchConfiguration.getValue();
                processFilesRecursive(new File(path), filter);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        status = SearchStatus.TERMINATED;
        logger.info("Search completed");
    }

    private void processFilesRecursive(File root, FileFilter fileFilter) throws InterruptedException {
        File[] files = root.listFiles();
        for (File file: files) {
            logger.debug(file);
            if (Thread.interrupted())
                throw new InterruptedException();
            if (file.isDirectory())
                processFilesRecursive(file, fileFilter);
            else {
                numberOfScannedFiles++; // this operation is not atomic, but I believe we can allow some inaccuracy
                if (fileFilter.accept(file))
                    sendFileForProcessing(file);
            }
        }
    }

    private void sendFileForProcessing(File file) {
        try {
            String filename = file.getAbsolutePath();
            byte[] data;
            BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));
            try {
                data = IOUtils.toByteArray(stream);
            } finally {
                stream.close();
            }
            String ip = Network.getPublicIP();
            inputQueueGateway.push(new RawDocument(filename, data, ip));
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JmsException ex) {
            ex.printStackTrace();
        }
        numberOfSubmittedFiles++; // same situation as with numberOfScannedFiles++
    }
}
