package fi.tamk.tiko.ohjelmointi.json.map;

import fi.tamk.tiko.ohjelmointi.json.*;

import java.lang.reflect.Field;

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
     * @param classData
     * @return
     */
    public static <T extends JSONMappable> T map(T object, JSONObject classData) {
        try {
            if (!object.isJSONMappable()) {
                throw new IllegalStateException();
            }

            Class<?> classInfo = object.getClass();

            for (String key : classData.keySet()) {
                JSONType jsonType = classData.get(key);
                Field field = classInfo.getDeclaredField(key);

                field.trySetAccessible();
                field.set(object, jsonType.get());
            }
        } catch (Exception e) {
            throw new IllegalStateException("JSONMapper cannot map this object.");
        }

        return object;
    }
}