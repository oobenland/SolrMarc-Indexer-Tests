package playground.solrmarc.index.collector;

public class SingleValueCollector implements AbstractValueCollector<String> {
    @Override
    public Object collect(String value) {
        return value;
    }
}
