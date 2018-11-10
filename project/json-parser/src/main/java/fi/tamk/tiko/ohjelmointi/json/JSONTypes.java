package fi.tamk.tiko.ohjelmointi.json;

/**
 * Defines allowed types for JSON.
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
public enum JSONTypes {

    /**
     * Allows boolean values.
     */
    BOOLEAN,

    /**
     * Allows number values.
     */
    NUMBER,

    /**
     * Allows decimal values.
     */
    DECIMAL,

    /**
     * Allows string values.
     */
    STRING,

    /**
     * Allows object values.
     */
    OBJECT,

    /**
     * Allows array values.
     */
    ARRAY,

    /**
     * Allows null values.
     */
    NULL
}