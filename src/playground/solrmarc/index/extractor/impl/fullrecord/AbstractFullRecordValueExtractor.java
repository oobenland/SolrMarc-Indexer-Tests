package playground.solrmarc.index.extractor.impl.fullrecord;

import playground.solrmarc.index.extractor.AbstractSingleValueExtractor;
import org.marc4j.MarcWriter;
import org.marc4j.marc.Record;

import java.io.ByteArrayOutputStream;

/**
 * This is a base class for all full record extractors.
 * It writes a record to the writer and reads the formatted
 * text from the writer's outputStream. The formatted text
 * will be the return value of the extraction.
 */
public class AbstractFullRecordValueExtractor implements AbstractSingleValueExtractor
{
    private final MarcWriter writer;
    private final ByteArrayOutputStream outputStream;

    /**
     * @param writer       a marc writer which writes to outputStream.
     * @param outputStream the stream which collects the output of the writer.
     */
    public AbstractFullRecordValueExtractor(final MarcWriter writer, final ByteArrayOutputStream outputStream) {
        this.writer = writer;
        this.outputStream = outputStream;
    }

    @Override
    public String extract(final Record record) {
        outputStream.reset();
        writer.write(record);
        return outputStream.toString();
    }
}
