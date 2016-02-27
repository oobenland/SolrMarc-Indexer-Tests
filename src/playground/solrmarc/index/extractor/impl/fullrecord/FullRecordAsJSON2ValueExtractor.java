package playground.solrmarc.index.extractor.impl.fullrecord;

import org.marc4j.MarcJsonWriter;

import java.io.ByteArrayOutputStream;

public class FullRecordAsJSON2ValueExtractor extends AbstractFullRecordValueExtractor {
    public FullRecordAsJSON2ValueExtractor() {
        this(new ByteArrayOutputStream());
    }

    private FullRecordAsJSON2ValueExtractor(final ByteArrayOutputStream outputStream) {
        super(new MarcJsonWriter(outputStream, MarcJsonWriter.MARC_IN_JSON), outputStream);
    }
}
