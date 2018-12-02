package fi.tamk.tiko.ohjelmointi.gui;

import javafx.util.converter.LongStringConverter;

public class CustomLongConverter extends LongStringConverter {

    /**
     * 
     */
    private Long value;

    /**
     * 
     */
    @Override
    public Long fromString(String value) {
        try {
            this.value = super.fromString(value);
        } catch (NumberFormatException e) {
            
        }

        return this.value;
    }
}