package ua.kiev.univ.jms.impl;

import ua.kiev.univ.jms.Document;

/**
 * @author jamanal
 * @version 2010-11-08
 */
public class RawDocument extends Document {

    private String source;

    /**
     * @param name filename of the document (including extension)
     * @param data document's content
     * @param source ip address of machine, where document has been found
     */
    public RawDocument(String name, byte[] data, String source) {
        super(name, data);
        this.source = source;
    }

    public String getSource() {
        return source;
    }
}
