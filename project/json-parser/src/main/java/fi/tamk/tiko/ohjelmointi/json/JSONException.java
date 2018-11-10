package fi.tamk.tiko.ohjelmointi.json;

/**
 * Defines custom exceptions for JSON parsing.
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
public class JSONException extends RuntimeException {

    /**
     * Calls super-class constructor.
     *
     * @param message Exception message.
     */
    public JSONException(String message) {
        super(message);
    }

    /**
     * Calls in-class constructor with formatted String.
     *
     * @param messageFormat String format.
     * @param args          List of arguments.
     */
    public JSONException(String messageFormat, Object... args) {
        this(String.format(messageFormat, args));
    }
}