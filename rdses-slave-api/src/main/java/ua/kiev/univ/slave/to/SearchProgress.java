package ua.kiev.univ.slave.to;

import java.io.Serializable;

/**
 * @author jamanal
 * @version 2010-11-08
 */
public class SearchProgress implements Serializable {

    private int numberOfScannedFiles;
    private int numberOfSubmittedFiles;

    public SearchProgress(int numberOfScannedFiles, int numberOfSubmittedFiles) {
        this.numberOfScannedFiles = numberOfScannedFiles;
        this.numberOfSubmittedFiles = numberOfSubmittedFiles;
    }

    public int getNumberOfScannedFiles() {
        return numberOfScannedFiles;
    }

    public int getNumberOfSubmittedFiles() {
        return numberOfSubmittedFiles;
    }
}
