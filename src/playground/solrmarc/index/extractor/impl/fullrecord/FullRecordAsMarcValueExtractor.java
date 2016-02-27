package playground.solrmarc.index.extractor.impl.fullrecord;

import org.marc4j.MarcStreamWriter;

import java.io.ByteArrayOutputStream;

public class FullRecordAsMarcValueExtractor extends AbstractFullRecordValueExtractor {
    public FullRecordAsMarcValueExtractor() {
        this(new ByteArrayOutputStream());
    }

    private FullRecordAsMarcValueExtractor(final ByteArrayOutputStream outputStream) {
        super(new MarcStreamWriter(outputStream, "UTF-8", true), outputStream);
    }
}
