package fi.tamk.tiko.ohjelmointi.json;

/**
 *
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
public class JSONType<T> {

    /**
     *
     */
    private JSONTypes type;

    /**
     *
     */
    private T object;

    /**
     *
     *
     * @return
     */
    public JSONTypes getType() {
        return type;
    }

    /**
     *
     *
     * @param value
     */
    public void set(T value) {
        if (value instanceof Long) {
            type = JSONTypes.NUMBER;
        } else if (value instanceof Double) {
            type = JSONTypes.DECIMAL;
        } else if (value instanceof Boolean) {
            type = JSONTypes.BOOLEAN;
        } else if (value instanceof String) {
            type = JSONTypes.STRING;
        } else if (value instanceof JSONObject) {
            type = JSONTypes.OBJECT;
        } else if (value instanceof JSONArray) {
            type = JSONTypes.ARRAY;
        } else {
            type = JSONTypes.NULL;
            value = null;
        }

        object = value;
    }

    /**
     *
     *
     * @return
     */
    public T get() {
        return object;
    }

    /**
     *
     */
    public JSONType() {
        this(null);
    }

    /**
     *
     *
     * @param object
     */
    public JSONType(T object) {
        set(object);
    }
}