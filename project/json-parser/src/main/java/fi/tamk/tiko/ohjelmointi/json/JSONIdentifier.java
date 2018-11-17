package fi.tamk.tiko.ohjelmointi.json;

/**
 * Stores identifier data.
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
public class JSONIdentifier {

    /**
     *
     */
    private char identifier;

    /**
     *
     */
    private JSONException exception;

    /**
     *
     *
     * @return
     */
    public char getIdentifier() {
        return identifier;
    }

    /**
     *
     *
     * @return
     */
    public JSONException getException() {
        return exception;
    }

    /**
     *
     *
     * @param identifier
     * @param message
     * @param args
     */
    public JSONIdentifier(char identifier, String message, Object... args) {
        this.exception = new JSONException(message, args);
        this.identifier = identifier;
    }
}