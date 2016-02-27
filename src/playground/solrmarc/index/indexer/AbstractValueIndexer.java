package playground.solrmarc.index.indexer;


import playground.solrmarc.index.collector.AbstractValueCollector;
import playground.solrmarc.index.extractor.AbstractValueExtractor;
import playground.solrmarc.index.mapping.AbstractValueMapping;
import org.marc4j.marc.Record;


public abstract class AbstractValueIndexer<T>
{
    private final String solrFieldName;
    private final AbstractValueExtractor<T> extractor;
    private final AbstractValueMapping<T>[] mappings;
    private final AbstractValueCollector<T> collector;

    public AbstractValueIndexer(final String solrFieldName, final AbstractValueExtractor<T> extractor, final AbstractValueMapping<T>[] mappings, final AbstractValueCollector<T> collector)
    {
        this.solrFieldName = solrFieldName;
        this.extractor = extractor;
        this.mappings = mappings;
        this.collector = collector;
    }

    public Object getFieldData(Record record) throws Exception
    {
        T values = extractor.extract(record);
        if (values == null)
        {
            return null;
        }
        for (final AbstractValueMapping<T> mapping : mappings)
        {
            values = mapping.map(values);
        }
        return collector.collect(values);
    }

    public String getSolrFieldName()
    {
        return solrFieldName;
    }
}
