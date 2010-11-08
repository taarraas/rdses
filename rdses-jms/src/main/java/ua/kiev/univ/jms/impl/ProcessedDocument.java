package ua.kiev.univ.jms.impl;

import ua.kiev.univ.jms.Document;

/**
 * @author jamanal
 * @version 2010-11-08
 */
public class ProcessedDocument extends Document {

    public ProcessedDocument(String name, byte[] data, String comment) {
        super(name, data);
    }
}
