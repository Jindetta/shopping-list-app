package fi.tamk.tiko.ohjelmointi.json;

import java.io.Writer;
import java.io.IOException;

/**
 * Writes JSON formatted data.
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
public class JSONWriter implements AutoCloseable {

    /**
     * Stores {@link Writer}.
     */
    private Writer writer;

    /**
     * Writes JSON data type.
     *
     * @param value JSONType.
     * @throws IOException On write operation failure.
     */
    public void write(JSONType value) throws IOException {
        writer.write(value.toString());
    }

    /**
     * Writes JSON array.
     *
     * @param value JSONArray.
     * @throws IOException On write operation failure.
     */
    public void writeArray(JSONArray value) throws IOException {
        write(JSONType.createArray(value));
    }

    /**
     * Writes JSON object.
     *
     * @param value JSONObject.
     * @throws IOException On write operation failure.
     */
    public void writeObject(JSONObject value) throws IOException {
        write(JSONType.createObject(value));
    }

    /**
     * Writes JSON string.
     *
     * @param value String.
     * @throws IOException On write operation failure.
     */
    public void writeString(String value) throws IOException {
        write(JSONType.createString(value));
    }

    /**
     * Writes JSON decimal.
     *
     * @param value Double.
     * @throws IOException On write operation failure.
     */
    public void writeDecimal(Double value) throws IOException {
        write(JSONType.createDecimal(value));
    }

    /**
     * Writes JSON number.
     *
     * @param value Long.
     * @throws IOException On write operation failure.
     */
    public void writeNumber(Long value) throws IOException {
        write(JSONType.createNumber(value));
    }

    /**
     * Writes JSON boolean.
     *
     * @param value Boolean.
     * @throws IOException On write operation failure.
     */
    public void writeBoolean(Boolean value) throws IOException {
        write(JSONType.createBoolean(value));
    }

    /**
     * Writes null.
     *
     * @throws IOException On write operation failure.
     */
    public void writeNull() throws IOException {
        write(JSONType.createNull());
    }

    /**
     * Overrides default constructor.
     *
     * @param writer Writer object.
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