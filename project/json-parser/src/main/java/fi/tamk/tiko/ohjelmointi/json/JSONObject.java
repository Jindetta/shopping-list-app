package fi.tamk.tiko.ohjelmointi.json;

import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
public class JSONObject {

    /**
     *
     */
    private Map<String, JSONType> data;

    /**
     *
     */
    public JSONObject() {
        data = new HashMap<>();
    }

    /**
     *
     */
    private class JSONType {

        /**
         *
         */
        private JSONTypes type;

        /**
         *
         */
        public JSONType() {
            type = JSONTypes.NULL;
        }
    }
}