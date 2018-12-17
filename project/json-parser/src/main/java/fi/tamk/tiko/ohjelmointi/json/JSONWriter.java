package fi.tamk.tiko.ohjelmointi.json;

import java.io.Writer;
import java.io.IOException;
import java.io.OutputStream;

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
    private Object writable;

    /**
     * Uses {@link Writer} to write contents.
     * @param data Writable JSON data.
     * @return true if successful, otherwise false.
     * @throws IOException Exception is thrown if file is unaccessible.
     */
    private boolean useWriter(String data) throws IOException {
        if (writable instanceof Writer) {
            ((Writer) writable).write(data);

            return true;
        }

        return false;
    }

    /**
     * Uses {@link OutputStream} to write contents.
     * @param data Writable JSON data.
     * @return true if successful, otherwise false.
     * @throws IOException Exception is thrown if file is unaccessible.
     */
    private boolean useStream(String data) throws IOException {
        if (writable instanceof OutputStream) {
            ((OutputStream) writable).write(data.getBytes());

            return true;
        }

        return false;
    }

    /**
     * Writes JSON data type.
     * @param value {@link JSONType}.
     * @throws IOException On write operation failure.
     */
    public void write(JSONType value) throws IOException {
        final String data = value.toString();

        if (!useWriter(data) && !useStream(data)) {
            throw new IllegalStateException("Illegal JSONWriter state.");
        }
    }

    /**
     * Writes JSON array.
     * @param value {@link JSONArray}.
     * @throws IOException On write operation failure.
     */
    public void writeArray(JSONArray value) throws IOException {
        write(JSONType.createArray(value));
    }

    /**
     * Writes JSON object.
     * @param value {@link JSONObject}.
     * @throws IOException On write operation failure.
     */
    public void writeObject(JSONObject value) throws IOException {
        write(JSONType.createObject(value));
    }

    /**
     * Writes JSON string.
     * @param value String.
     * @throws IOException On write operation failure.
     */
    public void writeString(String value) throws IOException {
        write(JSONType.createString(value));
    }

    /**
     * Writes JSON decimal.
     * @param value Double.
     * @throws IOException On write operation failure.
     */
    public void writeDecimal(Double value) throws IOException {
        write(JSONType.createDecimal(value));
    }

    /**
     * Writes JSON number.
     * @param value Long.
     * @throws IOException On write operation failure.
     */
    public void writeNumber(Long value) throws IOException {
        write(JSONType.createNumber(value));
    }

    /**
     * Writes JSON boolean.
     * @param value Boolean.
     * @throws IOException On write operation failure.
     */
    public void writeBoolean(Boolean value) throws IOException {
        write(JSONType.createBoolean(value));
    }

    /**
     * Writes null.
     * @throws IOException On write operation failure.
     */
    public void writeNull() throws IOException {
        write(JSONType.createNull());
    }

    /**
     * Overrides default constructor.
     * @param writer {@link Writer} object.
     */
    public JSONWriter(Writer writer) {
        this.writable = writer;
    }

    /**
     * Overrides default contructor.
     * @param stream {@link OutputStream} object.
     */
    public JSONWriter(OutputStream stream) {
        this.writable = stream;
    }

    /**
     * @see AutoCloseable#close close
     */
    @Override
    public void close() throws Exception {
        if (writable instanceof OutputStream) {
            ((OutputStream) writable).close();
        } else if (writable instanceof Writer) {
            ((Writer) writable).close();
        }
    }
}