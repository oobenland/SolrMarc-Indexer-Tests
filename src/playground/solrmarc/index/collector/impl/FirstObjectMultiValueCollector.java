package playground.solrmarc.index.collector.impl;

import playground.solrmarc.index.collector.MultiValueCollector;

import java.util.Collection;

public class FirstObjectMultiValueCollector extends MultiValueCollector {
    public final static String KEYWORD = "first";
    @Override
    public Object collect(final Collection<String> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.iterator().next();
    }
}