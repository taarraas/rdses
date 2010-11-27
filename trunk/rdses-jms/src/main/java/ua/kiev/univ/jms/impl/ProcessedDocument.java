package ua.kiev.univ.jms.impl;

import ua.kiev.univ.jms.Document;

/**
 * @author jamanal
 * @version 2010-11-08
 */
public class ProcessedDocument extends RawDocument {

    private String group;

    public ProcessedDocument(String name, byte[] data, String source, String group) {
        super(name, data, source);
        this.group = group;
    }

    public String getGroup() {
        return group;
    }
}
