package ua.kiev.univ.linguistics.convert;

import ua.kiev.univ.jms.impl.RawDocument;

import java.io.File;

/**
 * @author jamanal
 * @version Nov 21, 2010
 */
public interface RawDocumentConverter {

    File convert(RawDocument document);
}
