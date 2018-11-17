package fi.tamk.tiko.ohjelmointi.json;

import java.util.ArrayList;

/**
 *
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
     * 
     * @param value
     */
    public void addArray(JSONArray value) {
        add(JSONType.createArray(value));
    }

    /**
     * 
     * @param value
     */
    public void addObject(JSONObject value) {
        add(JSONType.createObject(value));
    }
}