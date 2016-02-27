package playground.solrmarc.index.extractor.impl.fullrecord;

import org.marc4j.MarcXmlWriter;

import java.io.ByteArrayOutputStream;

public class FullRecordAsXMLValueExtractor extends AbstractFullRecordValueExtractor {
    public FullRecordAsXMLValueExtractor() {
        this(new ByteArrayOutputStream());
    }

    private FullRecordAsXMLValueExtractor(final ByteArrayOutputStream outputStream) {
        super(new MarcXmlWriter(outputStream, "UTF-8"), outputStream);
    }
}
