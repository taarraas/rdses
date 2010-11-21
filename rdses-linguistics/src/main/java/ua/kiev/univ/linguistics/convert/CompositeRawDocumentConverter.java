package ua.kiev.univ.linguistics.convert;

import ua.kiev.univ.jms.impl.RawDocument;

import java.io.File;
import java.util.List;

/**
 * @author sshyiko@luxoft.com
 * @version Nov 21, 2010
 */
public class CompositeRawDocumentConverter implements RawDocumentConverter {

    private List<RawDocumentConverter> converters;

    public CompositeRawDocumentConverter(List<RawDocumentConverter> converters) {
        this.converters = converters;
    }

    @Override
    public File convert(RawDocument document) {
        File result = null;
        for (RawDocumentConverter converter : converters) {
            result = converter.convert(document);
            if (result != null)
                break;
        }
        return result;
    }
}
