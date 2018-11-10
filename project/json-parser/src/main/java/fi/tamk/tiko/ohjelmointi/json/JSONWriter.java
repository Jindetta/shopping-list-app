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
public class JSONWriter {

    /**
     *
     */
    private Writer writer;

    /**
     *
     */
    public JSONWriter(Writer writer) {
        this.writer = writer;
    }
}