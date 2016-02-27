package playground.solrmarc.index.indexer;


import playground.solrmarc.index.collector.MultiValueCollector;
import playground.solrmarc.index.collector.SingleValueCollector;
import playground.solrmarc.index.collector.impl.FirstObjectMultiValueCollector;
import playground.solrmarc.index.collector.impl.JoiningMultiValueCollector;
import playground.solrmarc.index.extractor.AbstractMultiValueExtractor;
import playground.solrmarc.index.extractor.AbstractSingleValueExtractor;
import playground.solrmarc.index.extractor.AbstractValueExtractor;
import playground.solrmarc.index.extractor.AbstractValueExtractorFactory;
import playground.solrmarc.index.mapping.AbstractMultiValueMapping;
import playground.solrmarc.index.mapping.AbstractSingleValueMapping;
import playground.solrmarc.index.mapping.AbstractValueMappingFactory;
import playground.solrmarc.index.utils.ReflectionUtils;
import playground.solrmarc.index.utils.StringReader;
import org.apache.log4j.Logger;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;


public class ValueIndexerFactory
{
    private final static Pattern COMMA_SPLIT_PATTERN = Pattern.compile(",");
    private final static FirstObjectMultiValueCollector FIRST_OBJECT_COLLECTOR = new FirstObjectMultiValueCollector();
    private final static MultiValueCollector MULTI_VALUE_COLLECTOR = new MultiValueCollector();
    private final static JoiningMultiValueCollector JOINING_MULTI_VALUE_COLLECTOR = new JoiningMultiValueCollector();
    private final static SingleValueCollector SINGLE_VALUE_COLLECTOR = new SingleValueCollector();

    private final static Logger logger = Logger.getLogger(ValueIndexerFactory.class);
    private final List<AbstractValueExtractorFactory> extractorFactories;
    private final List<AbstractValueMappingFactory> mappingFactories;

    public ValueIndexerFactory() throws InstantiationException, IllegalAccessException
    {
        this.extractorFactories = createExtractorFactories(ReflectionUtils.getExtractorFactoryClasses());
        this.mappingFactories = createMappingFactories(ReflectionUtils.getMappingFactoryClasses());
    }

    public List<AbstractValueIndexer<?>> createValueIndexers(Properties indexerProperties) throws IllegalAccessException, InstantiationException
    {
        final List<AbstractValueIndexer<?>> valueIndexers = new ArrayList<>();
        for (final String solrFieldName : indexerProperties.stringPropertyNames())
        {
            final String mappingDefinition = indexerProperties.getProperty(solrFieldName);
            final AbstractValueIndexer<?> valueIndexer = createValueIndexer(solrFieldName, mappingDefinition);
            if (valueIndexer != null)
            {
                valueIndexers.add(valueIndexer);
            }
        }
        return valueIndexers;
    }

    private List<AbstractValueExtractorFactory> createExtractorFactories(final Set<Class<? extends AbstractValueExtractorFactory>> factoryClasses) throws IllegalAccessException, InstantiationException
    {
        final List<AbstractValueExtractorFactory> factories = new ArrayList<>(factoryClasses.size());
        for (final Class<? extends AbstractValueExtractorFactory> extractorFactoryClass : factoryClasses)
        {
            if (Modifier.isAbstract(extractorFactoryClass.getModifiers())) {
                continue;
            }
            logger.trace("Create value extractor factory for " + extractorFactoryClass);
            final AbstractValueExtractorFactory factory = extractorFactoryClass.newInstance();
            factories.add(factory);
        }
        return factories;
    }

    private List<AbstractValueMappingFactory> createMappingFactories(final Set<Class<? extends AbstractValueMappingFactory>> factoryClasses) throws IllegalAccessException, InstantiationException
    {
        final List<AbstractValueMappingFactory> factories = new ArrayList<>(factoryClasses.size());
        for (final Class<? extends AbstractValueMappingFactory> extractorFactoryClass : factoryClasses)
        {
            logger.trace("Create value mapping factory for  s " + extractorFactoryClass);
            final AbstractValueMappingFactory factory = extractorFactoryClass.newInstance();
            factories.add(factory);
        }
        return factories;
    }

    /**
     * Creates an indexer representing the indexer process for one solr field (given by solrFieldName), defined
     * by the mappingConfiguration.
     *
     * @param solrFieldName        the name of the solr field.
     * @param mappingConfiguration the configuration definition for this indexer.
     * @return an indexer representing the indexer process for one solr field.
     */
    private AbstractValueIndexer<?> createValueIndexer(final String solrFieldName, final String mappingConfiguration)
    {
        final StringReader configurationReader = new StringReader(mappingConfiguration);
        final AbstractValueExtractor<?> extractor = createExtractor(solrFieldName, configurationReader);

        if (extractor == null)
        {
            return null;
        } else if (extractor instanceof AbstractMultiValueExtractor)
        {
            final AbstractMultiValueExtractor multiValueExtractor = (AbstractMultiValueExtractor) extractor;
            final AbstractMultiValueMapping[] mappings = createMultiValueMappings(configurationReader);
            final MultiValueCollector collector = createMultiValueCollector(configurationReader);
            return new MultiValueIndexer(solrFieldName, multiValueExtractor, mappings, collector);
        } else if (extractor instanceof AbstractSingleValueExtractor)
        {
            final AbstractSingleValueExtractor singleValueExtractor = (AbstractSingleValueExtractor) extractor;
            final AbstractSingleValueMapping[] mappings = createSingleValueMappings(configurationReader);
            final SingleValueCollector collector = createSingleValueCollector(configurationReader);
            return new SingleValueIndexer(solrFieldName, singleValueExtractor, mappings, collector);
        } else
        {
            throw new IllegalArgumentException("Only subclasses of AbstractMultiValueExtractor or AbstractSingleValueExtractor are allowed, but not " + extractor.getClass().getName());
        }
    }

    private AbstractValueExtractor<?> createExtractor(final String solrFieldName, final StringReader mappingConfiguration)
    {
        for (final AbstractValueExtractorFactory factory : extractorFactories)
        {
            if (factory.canHandle(solrFieldName, mappingConfiguration.getLookahead()))
            {
                return factory.createExtractor(solrFieldName, mappingConfiguration);
            }
        }
        throw new IllegalStateException("No indexer factory found for: " + solrFieldName + " = " + mappingConfiguration);
    }

    /**
     * Creates a bunch of value mappings in the same order as they are given in the settings.
     *
     * @param mappingConfiguration the date of the current configuration line
     * @return an array of mappings.
     */
    private AbstractMultiValueMapping[] createMultiValueMappings(final StringReader mappingConfiguration)
    {
        List<AbstractMultiValueMapping> mappings = new ArrayList<>();
        mappingConfiguration.skipUntilAfter(',');

        mappingConfiguration.mark();
        final String configurationData = mappingConfiguration.readAll().trim();
        mappingConfiguration.reset();

        if (configurationData.isEmpty())
        {
            return new AbstractMultiValueMapping[0];
        }

        for (final String mappingConfig : COMMA_SPLIT_PATTERN.split(configurationData))
        {
            if (isAValueMappingConfiguration(mappingConfig))
            {
                break;
            }
            AbstractMultiValueMapping valueMapping = createMultiValueMapping(mappingConfig.trim());
            if (valueMapping != null)
            {
                mappings.add(valueMapping);
            }
            mappingConfiguration.skip(mappingConfig.length() + 1);
        }
        return mappings.toArray(new AbstractMultiValueMapping[mappings.size()]);
    }

    /**
     * @see ValueIndexerFactory#createMultiValueMappings(StringReader)
     */
    private AbstractSingleValueMapping[] createSingleValueMappings(final StringReader mappingConfiguration)
    {
        List<AbstractSingleValueMapping> mappings = new ArrayList<>();
        mappingConfiguration.skipUntilAfter(',');

        mappingConfiguration.mark();
        final String config = mappingConfiguration.readAll().trim();
        mappingConfiguration.reset();

        if (config.isEmpty())
        {
            return new AbstractSingleValueMapping[0];
        }

        for (final String mappingConfig : COMMA_SPLIT_PATTERN.split(config))
        {
            if (!isAValueMappingConfiguration(mappingConfig))
            {
                break;
            }
            AbstractSingleValueMapping valueMapping = createSingleValueMapping(mappingConfig.trim());
            if (valueMapping != null)
            {
                mappings.add(valueMapping);
            }
            mappingConfiguration.skip(mappingConfig.length() + 1);
        }
        return mappings.toArray(new AbstractSingleValueMapping[mappings.size()]);
    }

    private boolean isAValueMappingConfiguration(final String configuration)
    {
        return !configuration.trim().toLowerCase().startsWith(FirstObjectMultiValueCollector.KEYWORD)
                && !configuration.trim().toLowerCase().startsWith(JoiningMultiValueCollector.KEYWORD);
    }

    private AbstractSingleValueMapping createSingleValueMapping(final String mappingConfig)
    {
        for (final AbstractValueMappingFactory mappingFactory : mappingFactories)
        {
            if (mappingFactory.canHandle(mappingConfig))
            {
                return mappingFactory.createSingleValueMapping(mappingConfig);
            }
        }
        throw new IllegalArgumentException("Could not handle impl: " + mappingConfig + "\nLoaded impl factories:\n" + mappingFactories.toString().replaceAll(",", ",\n"));
    }

    private AbstractMultiValueMapping createMultiValueMapping(final String mappingConfig)
    {
        for (final AbstractValueMappingFactory mappingFactory : mappingFactories)
        {
            if (mappingFactory.canHandle(mappingConfig))
            {
                return mappingFactory.createMultiValueMapping(mappingConfig);
            }
        }
        throw new IllegalArgumentException("Could not handle impl: " + mappingConfig + "\nLoaded impl factories:\n" + mappingFactories.toString().replaceAll(",", ",\n"));
    }

    private MultiValueCollector createMultiValueCollector(StringReader configurationReader)
    {
        // TODO: Factory!
        configurationReader.mark();
        String collectorIdentifier = configurationReader.readAll().trim();
        configurationReader.reset();

        if (collectorIdentifier.isEmpty())
        {
            return MULTI_VALUE_COLLECTOR;
        } else if (collectorIdentifier.startsWith("first"))
        {
            return FIRST_OBJECT_COLLECTOR;
        } else if (collectorIdentifier.startsWith("join"))
        {
            final int openParanthisis = configurationReader.indexOf('(');
            final int closeParanthisis = configurationReader.indexOf(')');
            if (openParanthisis >= 0 && closeParanthisis >= 0)
            {
                return new JoiningMultiValueCollector(configurationReader.readString(openParanthisis, closeParanthisis));
            } else
            {
                return JOINING_MULTI_VALUE_COLLECTOR;
            }
        }
        throw new IllegalStateException("The impl couldn't be identified. " + configurationReader);
    }

    private SingleValueCollector createSingleValueCollector(StringReader configurationReader)
    {
        // TODO: Factory!
        return SINGLE_VALUE_COLLECTOR;
    }
}
