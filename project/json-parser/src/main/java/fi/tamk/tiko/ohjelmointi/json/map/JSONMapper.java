package fi.tamk.tiko.ohjelmointi.json.map;

import fi.tamk.tiko.ohjelmointi.json.*;

import java.lang.reflect.Method;

/**
 *
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
public abstract class JSONMapper {

    /**
     * 
     * @param object
     * @param container
     * @return
     */
    public static <T> T loadMapping(T object, JSONObject container) {
        try {
            Class<?> classInfo = object.getClass();
            JSONObject data = getObjectData(object, container);

            for (String key : data.keySet()) {
                Object value = data.get(key).get();
                Class<?> type = value == null ? Object.class : value.getClass();
                Method setter = classInfo.getMethod(getSetter(key), type);

                setter.trySetAccessible();
                setter.invoke(object, value);
            }
        } catch (Exception e) {
            throw new IllegalStateException("JSONMapper cannot map this object.");
        }

        return object;
    }

    /**
     * 
     * @param object
     * @return
     */
    public static <T> JSONObject saveMapping(T object) {
        JSONObject container = new JSONObject();

        container.putObject(classType.getName(), object);

        return container;
    }

    /**
     * 
     * @param object
     * @param data
     * @return
     */
    public static JSONObject mapString(Class<?> object, String data) {
        JSONObject container = null;

        try {
            JSONType value = new JSONTokenizer(data).parse();
            container = mapObject(object, value.getAsObject());
        } catch (Exception e) {

        }

        return container;
    }

    /**
     * 
     * @param object
     * @param data
     * @return
     */
    public static JSONObject mapObject(Class<?> object, JSONObject data) {
        JSONObject container = new JSONObject();
        container.putObject(object.getName(), data);
        return container;
    }
}