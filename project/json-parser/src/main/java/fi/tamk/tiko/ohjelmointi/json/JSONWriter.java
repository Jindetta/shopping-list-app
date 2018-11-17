package fi.tamk.tiko.ohjelmointi.json;

import java.io.Writer;
import java.io.IOException;

/**
 *
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
public class JSONWriter implements AutoCloseable {

    /**
     *
     */
    private Writer writer;

    /**
     *
     *
     * @param value
     */
    public void write(JSONType value) throws IOException {
        writer.write(value.toString());
    }

    /**
     * 
     * @param value
     * @throws IOException
     */
    public void writeArray(JSONArray value) throws IOException {
        write(JSONType.createArray(value));
    }

    /**
     * 
     * @param value
     * @throws IOException
     */
    public void writeObject(JSONObject value) throws IOException {
        write(JSONType.createObject(value));
    }

    /**
     * 
     * @param value
     * @throws IOException
     */
    public void writeString(String value) throws IOException {
        write(JSONType.createString(value));
    }

    /**
     * 
     * @param value
     * @throws IOException
     */
    public void writeDecimal(Double value) throws IOException {
        write(JSONType.createDecimal(value));
    }

    /**
     * 
     * @param value
     * @throws IOException
     */
    public void writeNumber(Long value) throws IOException {
        write(JSONType.createNumber(value));
    }

    /**
     * 
     * @param value
     * @throws IOException
     */
    public void writeBoolean(Boolean value) throws IOException {
        write(JSONType.createBoolean(value));
    }

    /**
     *
     */
    public JSONWriter(Writer writer) {
        this.writer = writer;
    }

    /**
     * @see AutoCloseable#close close
     */
    @Override
    public void close() throws Exception {
        if (writer != null) {
            writer.close();
        }
    }
}