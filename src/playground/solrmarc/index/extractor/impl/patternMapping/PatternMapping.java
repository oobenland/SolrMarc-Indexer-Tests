package playground.solrmarc.index.extractor.impl.patternMapping;


import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PatternMapping
{
    /**
     * This is highly optimized!
     * Using Matcher#replaceAll(String) instead of String#replaceAll(String, String) is 50% faster.
     * Using one instance of Matcher and using Matcher#reset(String) is 10% faster than using
     * Pattern#matcher(String).
     */
    private final Matcher inputMatcher;
    private final String outputPattern;
    private final int orderIndex;

    public PatternMapping(final String inputPattern, final String outputPattern, final int orderIndex)
    {
        this.inputMatcher = Pattern.compile(inputPattern).matcher("");
        this.outputPattern = outputPattern;
        this.orderIndex = orderIndex;
    }

    public static String mapSingleValue(final List<PatternMapping> patternMappings, String value)
    {
        for (PatternMapping patternMapping : patternMappings)
        {
            if (patternMapping.canHandle(value))
            {
                value = patternMapping.map(value);
            }
        }
        return value;
    }

    public int getOrderIndex()
    {
        return orderIndex;
    }

    public boolean canHandle(final String value)
    {
        return inputMatcher.reset(value).find();
    }

    /**
     * PatternMapping#canHandle(String) has to be called before. Otherwise
     * the result will not be correct!
     *
     * @param value the value to be mapped.
     * @return the mapped value.
     */
    public String map(final String value)
    {
        return inputMatcher.replaceAll(outputPattern);
    }
}
