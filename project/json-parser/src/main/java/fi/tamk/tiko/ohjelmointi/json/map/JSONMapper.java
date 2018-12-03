package fi.tamk.tiko.ohjelmointi.json.map;

import fi.tamk.tiko.ohjelmointi.json.*;

import java.lang.reflect.Method;
import java.lang.reflect.Field;

/**
 * Maps any Java object to/from JSON string.
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
public abstract class JSONMapper {

    /**
     * Stores method type.
     */
    private enum MethodType {

        /**
         * Defines method as Getter.
         */
        ACCESSOR,

        /**
         * Defines method as Setter.
         */
        MUTATOR
    }

    /**
     * Gets accessor or mutator method name.
     * @param type MethodType value.
     * @param name Field name.
     * @return Method name as String.
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
     * Gets accessor method from given class.
     * @param classInfo Class information.
     * @param fieldName Field name.
     * @return Reflected Method.
     * @throws NoSuchMethodException If corresponding method for given field is not found.
     */
    @SuppressWarnings("all")
    private static Method getterMethod(Class<?> classInfo, String fieldName) throws NoSuchMethodException {
        return classInfo.getMethod(getMethodType(MethodType.ACCESSOR, fieldName), null);
    }

    /**
     * Gets mutator method from given class.
     * @param classInfo Class information.
     * @param fieldName Field name.
     * @param type Parameter type.
     * @return Reflected Method.
     * @throws NoSuchMethodException If corresponding method for given field is not found.
     */
    @SuppressWarnings("all")
    private static Method setterMethod(Class<?> classInfo, String fieldName, Class<?> type) throws NoSuchMethodException {
        return classInfo.getMethod(getMethodType(MethodType.MUTATOR, fieldName), type);
    }

    /**
     * Creates a new instance of given class.
     * @param classType Class information.
     * @return New instance of Class<T>.
     * @throws Exception If method cannot instatiate given class.
     */
    @SuppressWarnings("all")
    private static <T> T newInstanceOf(Class<T> classType) throws Exception {
        return classType.getConstructor(null).newInstance(null);
    }

    /**
     * Reads JSON Data from given field.
     * @param field Field name.
     * @return JSONData as String.
     */
    private static String readJSONDataValues(Field field) {
        JSONData annotation = field.getAnnotation(JSONData.class);
        return annotation == null ? null : annotation.key();
    }

    /**
     * Loads mapping information to given class.
     * @param object    Class information.
     * @param container Stores JSON information.
     * @return New instance of Class<T> with loaded data.
     */
    public static <T> T loadClassMapping(Class<T> object, JSONObject container) {
        T instance = null;

        try {
            instance = newInstanceOf(object);
        } catch (Exception e) {
            throw new IllegalStateException("JSONMapper cannot instantiate this object.");
        }

        return loadInstanceMapping(instance, container);
    }

    /**
     * Loads mapping information to given instance.
     * @param object    Instance of any class.
     * @param container Stores JSON information.
     * @return Altered instance of given instance with loaded data.
     */
    public static <T> T loadInstanceMapping(T object, JSONObject container) {
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
     * Saves mappable data to JSON.
     * @param object Instance of any class.
     * @return JSONObject containing mapped data.
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
     * Creates JSON data container.
     * @param object Class information.
     * @param data   Class JSON data.
     * @return JSONObject container.
     */
    public static JSONObject mapObject(Class<?> object, JSONObject data) {
        JSONObject container = new JSONObject();
        container.putObject(object.getName(), data);
        return container;
    }

    /**
     * Checks if given class is JSON mappable.
     * @param classType Class information.
     * @return true if class has JSONMappable annotation, otherwise false.
     */
    public static boolean isValidJSONMappableClass(Class<?> classType) {
        return classType.getAnnotation(JSONMappable.class) != null;
    }
}