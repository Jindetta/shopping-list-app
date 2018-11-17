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
     * Sets object type and value.
     *
     * @param value Insertable object.
     * @see Object
     */
    public void set(Object value) {
        if (value == null) {
            type = JSONTypes.NULL;
        } else if (value instanceof Long) {
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
            throw new IllegalArgumentException("Unknown object type.");
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

        throw new ClassCastException("Unable to cast as JSONArray");
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

        throw new ClassCastException("Unable to cast as JSONObject");
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

        throw new ClassCastException("Unable to cast as String");
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

        throw new ClassCastException("Unable to cast as Double");
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

        throw new ClassCastException("Unable to cast as Long");
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

        throw new ClassCastException("Unable to cast as Boolean");
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
     * @return
     */
    public static JSONType createBoolean(Boolean value) {
        return new JSONType(value);
    }

    /**
     *
     *
     * @return
     */
    public static JSONType createDecimal(Double value) {
        return new JSONType(value);
    }

    /**
     *
     *
     * @return
     */
    public static JSONType createNumber(Long value) {
        return new JSONType(value);
    }

    /**
     *
     *
     * @return
     */
    public static JSONType createNull() {
        return new JSONType();
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
            case NULL:
                return JSONTokenizer.writeNull();
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

        throw new IllegalStateException("JSONType contains illegal object.");
    }
}