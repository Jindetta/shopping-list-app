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

    /**
     * 
     * @param value
     */
    public void addString(String value) {
        add(JSONType.createString(value));
    }

    /**
     * 
     * @param value
     */
    public void addDecimal(Double value) {
        add(JSONType.createDecimal(value));
    }

    /**
     * 
     * @param value
     */
    public void addNumber(Long value) {
        add(JSONType.createNumber(value));
    }

    /**
     * 
     * @param value
     */
    public void addBoolean(Boolean value) {
        add(JSONType.createBoolean(value));
    }
}