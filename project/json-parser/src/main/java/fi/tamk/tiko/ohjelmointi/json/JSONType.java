package fi.tamk.tiko.ohjelmointi.json;

/**
 * Stores JSON data type information.
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
public class JSONType {

    /**
     * Stores JSON data type.
     */
    private JSONTypes type;

    /**
     * Stores JSON data value.
     */
    private Object object;

    /**
     * Gets type enum.
     *
     * @return JSONTypes enumeration.
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
     * Gets JSON data value.
     *
     * @return Object.
     */
    public Object get() {
        return object;
    }

    /**
     * Gets JSON data as array.
     *
     * @return JSONArray.
     */
    public JSONArray getAsArray() {
        if (type.equals(JSONTypes.ARRAY)) {
            return (JSONArray) get();
        }

        throw new ClassCastException("Unable to cast as JSONArray");
    }

    /**
     * Gets JSON data as object.
     *
     * @return JSONObject.
     */
    public JSONObject getAsObject() {
        if (type.equals(JSONTypes.OBJECT)) {
            return (JSONObject) get();
        }

        throw new ClassCastException("Unable to cast as JSONObject");
    }

    /**
     * Gets JSON data as string.
     *
     * @return String.
     */
    public String getAsString() {
        if (type.equals(JSONTypes.STRING)) {
            return (String) get();
        }

        throw new ClassCastException("Unable to cast as String");
    }

    /**
     * Gets JSON data as decimal.
     *
     * @return Double.
     */
    public Double getAsDecimal() {
        if (type.equals(JSONTypes.DECIMAL)) {
            return (Double) get();
        }

        throw new ClassCastException("Unable to cast as Double");
    }

    /**
     * Gets JSON data as number.
     *
     * @return Long.
     */
    public Long getAsNumber() {
        if (type.equals(JSONTypes.NUMBER)) {
            return (Long) get();
        }

        throw new ClassCastException("Unable to cast as Long");
    }

    /**
     * Gets JSON data as boolean.
     *
     * @return Boolean.
     */
    public Boolean getAsBoolean() {
        if (type.equals(JSONTypes.BOOLEAN)) {
            return (Boolean) get();
        }

        throw new ClassCastException("Unable to cast as Boolean");
    }

    /**
     * Checks if JSON data is null.
     *
     * @return true if value == null, otherwise false
     */
    public boolean isNull() {
        return get() == null && type.equals(JSONTypes.NULL);
    }

    /**
     * Overrides default constructor.
     */
    public JSONType() {
        this(null);
    }

    /**
     * Overrides default constructor.
     *
     * @param object Object value.
     */
    public JSONType(Object object) {
        set(object);
    }

    /**
     * 
     * @param object
     * @return
     */
    private static JSONTypes getTypeOf(Object object) {
        JSONTypes type = null;

        if (object == null) {
            type = JSONTypes.NULL;
        } else if (object instanceof Long) {
            type = JSONTypes.NUMBER;
        } else if (object instanceof String) {
            type = JSONTypes.STRING;
        } else if (object instanceof Double) {
            type = JSONTypes.DECIMAL;
        } else if (object instanceof Boolean) {
            type = JSONTypes.BOOLEAN;
        } else if (object instanceof JSONObject) {
            type = JSONTypes.OBJECT;
        } else if (object instanceof JSONArray) {
            type = JSONTypes.ARRAY;
        }

        return type;
    }

    /**
     * Creates new array JSONType.
     *
     * @return JSONType.
     */
    public static JSONType createArray(JSONArray array) {
        return new JSONType(array);
    }

    /**
     * Creates new object JSONType.
     *
     * @return JSONType.
     */
    public static JSONType createObject(JSONObject object) {
        return new JSONType(object);
    }

    /**
     * Creates new string JSONType.
     *
     * @return JSONType.
     */
    public static JSONType createString(String value) {
        return new JSONType(value);
    }

    /**
     * Creates new boolean JSONType.
     *
     * @return JSONType.
     */
    public static JSONType createBoolean(Boolean value) {
        return new JSONType(value);
    }

    /**
     * Creates new decimal JSONType.
     *
     * @return JSONType.
     */
    public static JSONType createDecimal(Double value) {
        return new JSONType(value);
    }

    /**
     * Creates new number JSONType.
     *
     * @return JSONType.
     */
    public static JSONType createNumber(Long value) {
        return new JSONType(value);
    }

    /**
     * Creates new null JSONType.
     *
     * @return JSONType.
     */
    public static JSONType createNull() {
        return new JSONType();
    }

    /**
     * 
     * @param object
     * @return
     */
    public static String getJSONString(Object object) {
        switch (getTypeOf(object)) {
            case NULL:
                return JSONTokenizer.writeNull();
            case NUMBER:
                return JSONTokenizer.writeNumber((Long) object);
            case STRING:
                return JSONTokenizer.writeString((String) object);
            case DECIMAL:
                return JSONTokenizer.writeDecimal((Double) object);
            case BOOLEAN:
                return JSONTokenizer.writeBoolean((Boolean) object);
            case OBJECT:
                return JSONTokenizer.writeObject((JSONObject) object);
            case ARRAY:
                return JSONTokenizer.writeArray((JSONArray) object);
        }

        return null;
    }

    /**
     * Overrides default implementation.
     *
     * @param object Object to compare.
     * @return true if object is equal, otherwise false
     */
    @Override
    public boolean equals(Object object) {
        if (object instanceof JSONType) {
            JSONType jsonType = (JSONType) object;

            if (type == jsonType.getType()) {
                Object value = jsonType.get();

                if (type != JSONTypes.NULL) {
                    return value.equals(get());
                }

                return value == get();
            }
        }

        return false;
    }

    /**
     * Overrides default implementation.
     *
     * @return String representation of this type.
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