package playground.solrmarc.index.extractor.impl.fullrecord;

import org.marc4j.marc.Record;

public class FullRecordAsTextValueExtractor extends AbstractFullRecordValueExtractor {
    public FullRecordAsTextValueExtractor() {
        // Special case: extract() is overridden, so the parameters aren't used.
        super(null, null);
    }

    @Override
    public String extract(final Record record) {
        return record.toString().replaceAll("\n", "<br/>");
    }
}
