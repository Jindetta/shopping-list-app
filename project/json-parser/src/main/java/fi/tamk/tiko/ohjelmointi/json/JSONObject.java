package fi.tamk.tiko.ohjelmointi.json;

import java.util.HashMap;

/**
 *
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
     *
     * @param key
     * @param value
     */
    public void putArray(String key, JSONArray value) {
        put(key, JSONType.createArray(value));
    }

    /**
     * 
     * @param key
     * @param value
     */
    public void putObject(String key, JSONObject value) {
        put(key, JSONType.createObject(value));
    }

    /**
     * 
     * @param key
     * @param value
     */
    public void putString(String key, String value) {
        put(key, JSONType.createString(value));
    }
}