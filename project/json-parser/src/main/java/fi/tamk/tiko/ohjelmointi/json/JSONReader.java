package fi.tamk.tiko.ohjelmointi.json;

import java.io.IOException;
import java.io.BufferedReader;
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
     * Stores {@link BufferedReader}.
     */
    private BufferedReader reader;

    /**
     * Reads next available JSONType from stream.
     *
     * @return Valid JSONType or null.
     * @see JSONType
     */
    public JSONType readNext() throws IOException {
        if (tokenizer == null) {
            StringBuilder contents = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                contents.append(line);
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
    public JSONReader(Reader reader) throws IOException {
        this.reader = new BufferedReader(reader);
    }

    /**
     * @see AutoCloseable#close close
     */
    @Override
    public void close() throws Exception {
        if (reader != null) {
            reader.close();
        }
    }
}