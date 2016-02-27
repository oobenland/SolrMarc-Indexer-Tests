package playground.solrmarc.index.extractor.impl.fullrecord;

import org.marc4j.MarcJsonWriter;

import java.io.ByteArrayOutputStream;

public class FullRecordAsJSONValueExtractor extends AbstractFullRecordValueExtractor {
    public FullRecordAsJSONValueExtractor() {
        this(new ByteArrayOutputStream());
    }

    private FullRecordAsJSONValueExtractor(final ByteArrayOutputStream outputStream) {
        super(new MarcJsonWriter(outputStream, MarcJsonWriter.MARC_JSON), outputStream);
    }
}
