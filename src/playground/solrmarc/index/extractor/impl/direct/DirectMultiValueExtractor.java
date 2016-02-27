package playground.solrmarc.index.extractor.impl.direct;

import playground.solrmarc.index.extractor.AbstractMultiValueExtractor;
import org.marc4j.marc.Record;

import java.util.Collection;

public class DirectMultiValueExtractor implements AbstractMultiValueExtractor
{
    private final String fieldsAndSubfields;

    public DirectMultiValueExtractor(final String fieldsAndSubfields) {
        this.fieldsAndSubfields = fieldsAndSubfields;
    }

    @Override
    public Collection<String> extract(final Record record) {
        // TODO: Use SolrIndexerMixin#getFieldList(Record, String)
        return null;
    }
}
