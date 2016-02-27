package playground.solrmarc.index.collector;

public interface AbstractValueCollector<T> {
    Object collect(final T values);
}