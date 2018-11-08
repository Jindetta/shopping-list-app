package fi.tamk.tiko.ohjelmointi.json;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;

/**
 *
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
public class JSONReader implements AutoCloseable {

    /**
     *
     */
    private JSONTokenizer tokenizer;

    /**
     *
     */
    private BufferedReader reader;

    /**
     *
     *
     * @return
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

        return tokenizer.parseNext();
    }

    /**
     *
     */
    public JSONReader(String filePath) throws IOException, FileNotFoundException {
        this(new File(filePath));
    }

    /**
     *
     */
    public JSONReader(File file) throws IOException, FileNotFoundException {
        reader = new BufferedReader(new FileReader(file));
    }

    /**
     *
     */
    @Override
    public void close() throws Exception {
        if (reader != null) {
            reader.close();
        }
    }
}