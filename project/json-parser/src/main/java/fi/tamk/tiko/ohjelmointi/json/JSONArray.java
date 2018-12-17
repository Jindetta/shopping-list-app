package fi.tamk.tiko.ohjelmointi.json;

import java.util.ArrayList;

/**
 * Stores JSON Array information.
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
public class JSONArray extends ArrayList<JSONType> {

    /**
     * Stores auto-generated identifier.
     */
    private static final long serialVersionUID = 8948315495137219901L;

    /**
     * Adds a new array value.
     * @param value JSONArray value.
     */
    public void addArray(JSONArray value) {
        add(JSONType.createArray(value));
    }

    /**
     * Adds a new object value.
     * @param value JSONObject value.
     */
    public void addObject(JSONObject value) {
        add(JSONType.createObject(value));
    }

    /**
     * Adds a new string value.
     * @param value String value.
     */
    public void addString(String value) {
        add(JSONType.createString(value));
    }

    /**
     * Adds a new decimal value.
     * @param value Double value.
     */
    public void addDecimal(Double value) {
        add(JSONType.createDecimal(value));
    }

    /**
     * Adds a new number value.
     * @param value Long value.
     */
    public void addNumber(Long value) {
        add(JSONType.createNumber(value));
    }

    /**
     * Adds a new boolean value.
     * @param value Boolean value.
     */
    public void addBoolean(Boolean value) {
        add(JSONType.createBoolean(value));
    }

    /**
     * Adds a new null value.
     */
    public void addNull() {
        add(JSONType.createNull());
    }
}