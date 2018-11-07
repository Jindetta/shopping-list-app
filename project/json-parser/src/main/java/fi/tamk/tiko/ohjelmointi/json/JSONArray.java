package fi.tamk.tiko.ohjelmointi.json;

import java.util.ArrayList;

/**
 *
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
public class JSONArray {

    /**
     *
     */
    private ArrayList<JSONType> values;

    /**
     *
     *
     * @param
     */
    public void add(JSONType object) {
        values.add(object);
    }

    /**
     *
     */
    public int size() {
        return values.size();
    }

    /**
     *
     */
    public JSONArray() {
        values = new ArrayList<>();
    }
}