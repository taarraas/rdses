package ua.kiev.univ.jms;

import java.io.Serializable;

/**
 * @author jamanal
 * @version 2010-11-08
 */
public abstract class Document implements Serializable {

    private String name;
    private byte[] data;

    /**
     * @param name filename of the document (including extension)
     * @param data document's content
     */
    protected Document(String name, byte[] data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public byte[] getData() {
        return data;
    }
}
