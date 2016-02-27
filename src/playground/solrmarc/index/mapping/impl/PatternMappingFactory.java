package playground.solrmarc.index.mapping.impl;

import playground.solrmarc.index.extractor.impl.patternMapping.PatternMapping;
import playground.solrmarc.index.extractor.impl.patternMapping.PatternMappingValueExtractorFactory;
import playground.solrmarc.index.mapping.AbstractSingleValueMapping;
import playground.solrmarc.index.mapping.AbstractMultiValueMapping;
import playground.solrmarc.index.mapping.AbstractValueMappingFactory;

import java.util.List;

public class PatternMappingFactory extends AbstractValueMappingFactory {
    @Override
    public boolean canHandle(String mappingConfiguration) {
        return mappingConfiguration.startsWith("(") && mappingConfiguration.endsWith(")");
    }

    @Override
    public AbstractSingleValueMapping createSingleValueMapping(String mappingConfiguration) {
        final String mappingName = mappingConfiguration.substring(1, mappingConfiguration.length() - 1);
        List<PatternMapping> patternMappings = PatternMappingValueExtractorFactory.getPatternMappingsForName(mappingName);
        return new SingleValuePatternMapping(patternMappings);
    }

    @Override
    public AbstractMultiValueMapping createMultiValueMapping(String mappingConfiguration) {
        final String mappingName = mappingConfiguration.substring(1, mappingConfiguration.length() - 1);
        List<PatternMapping> patternMappings = PatternMappingValueExtractorFactory.getPatternMappingsForName(mappingName);
        return new MultiValuePatternMapping(patternMappings);
    }
}
