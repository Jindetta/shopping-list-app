package fi.tamk.tiko.ohjelmointi.json;

/**
 *
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
public class JSONType {

    /**
     *
     */
    private JSONTypes type;

    /**
     *
     */
    private Object object;

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
    public void set(Object value) {
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
    public Object get() {
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
     *
     * @return
     */
    public boolean isNull() {
        return get() == null && type.equals(JSONTypes.NULL);
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
    public JSONType(Object object) {
        set(object);
    }

    /**
     *
     *
     * @return
     */
    public static JSONType createArray(JSONArray array) {
        return new JSONType(array);
    }

    /**
     *
     *
     * @return
     */
    public static JSONType createObject(JSONObject object) {
        return new JSONType(object);
    }

    /**
     *
     *
     * @return
     */
    public static JSONType createString(String value) {
        return new JSONType(value);
    }

    /**
     *
     *
     * @param object
     * @return
     */
    @Override
    public boolean equals(Object object) {
        if (object instanceof JSONType) {
            JSONType t = (JSONType) object;

            return type.equals(t.getType()) && toString().equals(t.toString());
        }

        return false;
    }

    /**
     *
     *
     * @return
     */
    @Override
    public String toString() {
        switch (getType()) {
            case NUMBER:
                return JSONTokenizer.writeNumber(getAsNumber());
            case DECIMAL:
                return JSONTokenizer.writeDecimal(getAsDecimal());
            case BOOLEAN:
                return JSONTokenizer.writeBoolean(getAsBoolean());
            case STRING:
                return JSONTokenizer.writeString(getAsString());
            case OBJECT:
                return JSONTokenizer.writeObject(getAsObject());
            case ARRAY:
                return JSONTokenizer.writeArray(getAsArray());
        }

        return JSONTokenizer.writeNull();
    }
}