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
     */
    private enum MethodType {

        /**
         * 
         */
        ACCESSOR,

        /**
         * 
         */
        MUTATOR
    }

    /**
     * 
     * @param type
     * @param name
     * @return
     */
    private static String getMethodType(MethodType type, String name) {
        name = name.substring(0, 1).toUpperCase().concat(name.substring(1));

        switch (type) {
            case ACCESSOR: return "get".concat(name);
            case MUTATOR: return "set".concat(name);
        }

        throw new IllegalArgumentException("Invalid method type.");
    }

    /**
     * 
     * @param classInfo
     * @param fieldName
     * @return
     * @throws NoSuchMethodException
     */
    @SuppressWarnings("all")
    private static Method getterMethod(Class<?> classInfo, String fieldName) throws NoSuchMethodException {
        return classInfo.getMethod(getMethodType(MethodType.ACCESSOR, fieldName), null);
    }

    /**
     * 
     * @param classInfo
     * @param fieldName
     * @param type
     * @return
     * @throws NoSuchMethodException
     */
    @SuppressWarnings("all")
    private static Method setterMethod(Class<?> classInfo, String fieldName, Class<?> type) throws NoSuchMethodException {
        return classInfo.getMethod(getMethodType(MethodType.MUTATOR, fieldName), type);
    }

    /**
     * 
     * @param field
     * @return
     */
    private static String readJSONDataValues(Field field) {
        JSONData annotation = field.getAnnotation(JSONData.class);
        return annotation == null ? null : annotation.key();
    }

    /**
     * 
     * @param object
     * @param container
     * @return
     */
    public static <T> T loadMapping(T object, JSONObject container) {
        try {
            Class<?> classInfo = object.getClass();

            if (classInfo.getAnnotation(JSONMappable.class) == null) {
                throw new IllegalStateException();
            }

            JSONObject data = container.get(classInfo.getName()).getAsObject();

            for (Field field : classInfo.getDeclaredFields()) {
                String key = readJSONDataValues(field);

                if (key != null && data.containsKey(key)) {
                    Object value = data.get(key).get();
                    Class<?> type = value == null ? field.getType() : value.getClass();
                    Method setter = setterMethod(classInfo, key, type);

                    setter.trySetAccessible();
                    setter.invoke(object, value);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("JSONMapper cannot load this object.");
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