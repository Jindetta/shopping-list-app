package fi.tamk.tiko.ohjelmointi.json.map;

import fi.tamk.tiko.ohjelmointi.json.*;

import java.lang.reflect.Method;
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
     * @param classType
     * @return
     * @throws Exception
     */
    @SuppressWarnings("all")
    private static <T> T newInstanceOf(Class<T> classType) throws Exception {
        return classType.getConstructor(null).newInstance(null);
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
    public static <T> T loadClassMapping(Class<T> object, JSONObject container) {
        T instance = null;

        try {
            instance = newInstanceOf(object);
        } catch (Exception e) {
            throw new IllegalStateException("JSONMapper cannot instantiate this object.");
        }

        return loadMapping(instance, container);
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

            if (!isValidJSONMappableClass(classInfo)) {
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

        try {
            Class<?> classInfo = object.getClass();
            JSONObject classData = new JSONObject();

            if (!isValidJSONMappableClass(classInfo)) {
                throw new IllegalStateException();
            }

            for (Field field : classInfo.getDeclaredFields()) {
                String key = readJSONDataValues(field);

                if (key != null && key.equals(field.getName())) {
                    Method getter = getterMethod(classInfo, key);

                    getter.trySetAccessible();
                    Object result = getter.invoke(object);
                    classData.put(key, new JSONType(result));
                }
            }

            container.putObject(classInfo.getName(), classData);
        } catch (Exception e) {
            throw new IllegalStateException("JSONMapper cannot save this object.");
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

    /**
     * 
     * @param classType
     * @return
     */
    public static boolean isValidJSONMappableClass(Class<?> classType) {
        return classType.getAnnotation(JSONMappable.class) != null;
    }
}