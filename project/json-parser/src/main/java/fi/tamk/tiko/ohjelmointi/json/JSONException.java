package fi.tamk.tiko.ohjelmointi.json;

/**
 * Defines custom exceptions for JSON parser.
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
public class JSONException extends RuntimeException {

    /**
     * Stores auto-generated identifier.
     */
    private static final long serialVersionUID = -2364958504251163329L;

    /**
     * Overrides default constructor.
     *
     * @param message Exception message.
     */
    public JSONException(String message) {
        super(message);
    }

    /**
     * Overrides default constructor.
     *
     * @param messageFormat String format.
     * @param args          List of arguments.
     */
    public JSONException(String messageFormat, Object... args) {
        this(String.format(messageFormat, args));
    }
}