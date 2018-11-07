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
     *
     * @return
     */
    public JSONArray getAsArray() {
        if (type.equals(JSONTypes.ARRAY)) {
            return (JSONArray) get();
        }

        throw new JSONException("Unable to cast as JSONArray (%s)", type);
    }

    /**
     *
     *
     * @return
     */
    public JSONObject getAsObject() {
        if (type.equals(JSONTypes.OBJECT)) {
            return (JSONObject) get();
        }

        throw new JSONException("Unable to cast as JSONObject (%s)", type);
    }

    /**
     *
     *
     * @return
     */
    public String getAsString() {
        if (type.equals(JSONTypes.STRING)) {
            return (String) get();
        }

        throw new JSONException("Unable to cast as String (%s)", type);
    }

    /**
     *
     *
     * @return
     */
    public Double getAsDecimal() {
        if (type.equals(JSONTypes.DECIMAL)) {
            return (Double) get();
        }

        throw new JSONException("Unable to cast as Double (%s)", type);
    }

    /**
     *
     *
     * @return
     */
    public Long getAsNumber() {
        if (type.equals(JSONTypes.NUMBER)) {
            return (Long) get();
        }

        throw new JSONException("Unable to cast as Long (%s)", type);
    }

    /**
     *
     *
     * @return
     */
    public Boolean getAsBoolean() {
        if (type.equals(JSONTypes.BOOLEAN)) {
            return (Boolean) get();
        }

        throw new JSONException("Unable to cast as Boolean (%s)", type);
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