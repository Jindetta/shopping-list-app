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
    public static <T extends JSONMappable> T map(T object, JSONObject container) {
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
     * @param classType
     * @param object
     * @return
     */
    public static JSONObject setContainer(Class<? extends JSONMappable> classType, JSONObject object) {
        JSONObject container = new JSONObject();

        container.putObject(classType.getName(), object);

        return container;
    }

    /**
     * 
     * @param object
     * @param className
     * @param data
     * @return
     */
    private static <T extends JSONMappable> JSONObject getObjectData(T object, JSONObject data) {
        if (!object.isJSONMappable()) {
            throw new IllegalStateException();
        }

        return data.get(object.getClass().getName()).getAsObject();
    }

    /**
     * 
     * @param name
     * @return
     */
    private static String getSetter(String name) {
        return "set".concat(name.substring(0, 1).toUpperCase().concat(name.substring(1)));
    }
}