package playground.solrmarc.index.indexer;


import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class ValueIndexerFactoryTests
{
    static
    {
        PropertyConfigurator.configure(new File("log4j.properties").getAbsolutePath());
    }

    @Test
    public void testEmptyConfiguration() throws IllegalAccessException, InstantiationException
    {
        final Properties configs = new Properties();
        final List<AbstractValueIndexer<?>> valueIndexers = createIndexers(configs);
        assertEquals(0, valueIndexers.size());
    }

    @Test
    public void testConstantIndexer() throws Exception
    {
        final Properties configs = new Properties();
        configs.put("constant", "\"Test constant\"");

        final List<AbstractValueIndexer<?>> valueIndexers = createIndexers(configs);
        assertEquals(1, valueIndexers.size());

        final SingleValueIndexer indexer = (SingleValueIndexer) valueIndexers.get(0);
        assertEquals("constant", indexer.getSolrFieldName());
        assertEquals("Test constant", indexer.getFieldData(null));
    }

    @Test
    public void testFullRecordIndexer() throws IllegalAccessException, InstantiationException
    {
        final Properties configs = new Properties();
        configs.put("fullRecord", "xml");

        final List<AbstractValueIndexer<?>> valueIndexers = createIndexers(configs);
        assertEquals(1, valueIndexers.size());

        final SingleValueIndexer indexer = (SingleValueIndexer) valueIndexers.get(0);
        assertEquals("fullRecord", indexer.getSolrFieldName());
    }

    @Test
    public void testMixinIndexer() throws Exception
    {
        final Properties configs = new Properties();
        configs.put("mixin", "custom, testMixinMethod");

        final List<AbstractValueIndexer<?>> valueIndexers = createIndexers(configs);
        assertEquals(1, valueIndexers.size());

        final SingleValueIndexer indexer = (SingleValueIndexer) valueIndexers.get(0);
        assertEquals("mixin", indexer.getSolrFieldName());
        assertEquals("<null>", indexer.getFieldData(null));
    }

    @Test(expected = NullPointerException.class)
    public void testManyIndexers() throws Exception
    {
        final Properties configs = new Properties();
        configs.put("mixin", "custom, testMixinMethod");
        configs.put("constant", "\"Test constant\"");
        configs.put("fullRecord", "xml");

        final List<AbstractValueIndexer<?>> valueIndexers = createIndexers(configs);
        assertEquals(3, valueIndexers.size());

        for (final AbstractValueIndexer<?> valueIndexer : valueIndexers)
        {
            // This will fail because the full record indexer dosn't allow NULL as record.
            assertNotNull(valueIndexer.getFieldData(null));
        }
    }

    @Test
    public void testJavaIndexerInheritanceParent() throws Exception
    {
        final Properties configs = new Properties();
        configs.put("constant", "java, testMethod");

        final List<AbstractValueIndexer<?>> valueIndexers = createIndexers(configs);
        assertEquals(1, valueIndexers.size());

        final SingleValueIndexer indexer = (SingleValueIndexer) valueIndexers.get(0);
        assertEquals("constant", indexer.getSolrFieldName());
        assertEquals("<null>", indexer.getFieldData(null));
    }

    @Test
    public void testJavaIndexerInheritanceChild() throws Exception
    {
        final Properties configs = new Properties();
        configs.put("constant", "java(ChildMixin), testMethod");

        final List<AbstractValueIndexer<?>> valueIndexers = createIndexers(configs);
        assertEquals(1, valueIndexers.size());

        final SingleValueIndexer indexer = (SingleValueIndexer) valueIndexers.get(0);
        assertEquals("constant", indexer.getSolrFieldName());
        assertEquals("Overwritten in ChildMixin", indexer.getFieldData(null));
    }

    private List<AbstractValueIndexer<?>> createIndexers(Properties configs) throws IllegalAccessException, InstantiationException
    {
        final ValueIndexerFactory factory = new ValueIndexerFactory();
        return factory.createValueIndexers(configs);
    }
}
