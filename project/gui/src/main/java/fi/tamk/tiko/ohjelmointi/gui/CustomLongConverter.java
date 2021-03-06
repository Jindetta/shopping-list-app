package fi.tamk.tiko.ohjelmointi.gui;

import javafx.util.converter.LongStringConverter;

/**
 * Implements a custom version of LongStringConverter.
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
public class CustomLongConverter extends LongStringConverter {

    /**
     * Stores previous value.
     */
    private Long value;

    /**
     * Overrides method to handle exceptions.
     */
    @Override
    public Long fromString(String value) {
        if (!value.isBlank()) {
            try {
                this.value = super.fromString(value);
            } catch (NumberFormatException e) {
                
            }
        }

        return this.value;
    }
}