package fi.tamk.tiko.ohjelmointi.json;

import java.util.HashMap;

/**
 * Stores JSON Object information.
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
public class JSONObject extends HashMap<String, JSONType> {

    /**
     * Stores auto-generated identifier.
     */
    private static final long serialVersionUID = 3588870534679760142L;

    /**
     * Adds a new array value.
     * @param key   Key identifier.
     * @param value JSONArray object.
     */
    public void putArray(String key, JSONArray value) {
        put(key, JSONType.createArray(value));
    }

    /**
     * Adds a new object value.
     * @param key   Key identifier.
     * @param value JSONObject object.
     */
    public void putObject(String key, JSONObject value) {
        put(key, JSONType.createObject(value));
    }

    /**
     * Adds a new string value.
     * @param key   Key identifier.
     * @param value String value.
     */
    public void putString(String key, String value) {
        put(key, JSONType.createString(value));
    }

    /**
     * Adds a new decimal value.
     *
     * @param key   Key identifier.
     * @param value Double value.
     */
    public void putDecimal(String key, Double value) {
        put(key, JSONType.createDecimal(value));
    }

    /**
     * Adds a new number value.
     * @param key   Key identifier.
     * @param value Long value.
     */
    public void putNumber(String key, Long value) {
        put(key, JSONType.createNumber(value));
    }

    /**
     * Adds a new boolean value.
     * @param key   Key identifier.
     * @param value Boolean value.
     */
    public void putBoolean(String key, Boolean value) {
        put(key, JSONType.createBoolean(value));
    }

    /**
     * Adds a new null value.
     * @param key   Key identifier.
     */
    public void putNull(String key) {
        put(key, JSONType.createNull());
    }
}