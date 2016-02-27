package playground.solrmarc.index.extractor.impl.direct;

import playground.solrmarc.index.extractor.AbstractValueExtractor;
import playground.solrmarc.index.utils.StringReader;
import playground.solrmarc.index.extractor.AbstractValueExtractorFactory;
import playground.solrmarc.tools.Utils;


public class DirectValueExtractorFactory extends AbstractValueExtractorFactory {

    @Override
    public boolean canHandle(final String solrFieldName, final String mappingConfiguration) {
        return mappingConfiguration.length() >= 3 && Utils.isNumber(mappingConfiguration.trim().substring(0, 3));
    }

    @Override
    public AbstractValueExtractor<?> createExtractor(final String solrFieldName, final StringReader mappingConfiguration) {
        int colonIndex = mappingConfiguration.indexOf(',');
        if (colonIndex <= -1) {
            return new DirectMultiValueExtractor(mappingConfiguration.readAll());
        } else {
            return new DirectMultiValueExtractor(mappingConfiguration.readString(colonIndex));
        }
    }
}
