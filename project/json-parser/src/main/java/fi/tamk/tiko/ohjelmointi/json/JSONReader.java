package fi.tamk.tiko.ohjelmointi.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * Reads JSON formatted data.
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
public class JSONReader implements AutoCloseable {

    /**
     * Stores {@link JSONTokenizer}.
     */
    private JSONTokenizer tokenizer;

    /**
     * Stores readable object.
     */
    private Object readable;

    private boolean useReader(StringBuilder builder) throws IOException {
        if (readable instanceof Reader) {
            int character;

            while ((character = ((Reader) readable).read()) != -1) {
                builder.append((char) character);
            }

            return true;
        }

        return false;
    }

    private boolean useStream(StringBuilder builder) throws IOException {
        if (readable instanceof InputStream) {
            int character;

            while ((character = ((InputStream) readable).read()) != -1) {
                builder.append((char) character);
            }

            return true;
        }

        return false;
    }

    /**
     * Reads next available JSONType from stream.
     *
     * @return Valid JSONType or null.
     * @see JSONType
     */
    public JSONType readObject() throws IOException {
        if (tokenizer == null) {
            StringBuilder contents = new StringBuilder();

            if (!useReader(contents) && !useStream(contents)) {
                throw new IllegalStateException("Illegal JSONReader state.");
            }

            tokenizer = new JSONTokenizer(contents.toString());
        }

        return tokenizer.parse();
    }

    /**
     * Overrides default constructor.
     *
     * @param reader Reader object.
     * @see Reader
     */
    public JSONReader(Reader reader) {
        this.readable = reader;
    }

    public JSONReader(InputStream stream) {
        this.readable = stream;
    }

    /**
     * @see AutoCloseable#close close
     */
    @Override
    public void close() throws Exception {
        if (readable instanceof InputStream) {
            ((InputStream) readable).close();
        } else if (readable instanceof Reader) {
            ((Reader) readable).close();
        }
    }
}