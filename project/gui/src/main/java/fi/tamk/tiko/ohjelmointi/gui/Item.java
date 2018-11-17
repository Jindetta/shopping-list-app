package fi.tamk.tiko.ohjelmointi.gui;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
public class Item {

    /**
     * 
     */
    private StringProperty itemName;

    /**
     * 
     */
    private IntegerProperty itemAmount;

    /**
     * 
     * 
     * @return
     */
    public String getItemName() {
        return itemNameProperty().get();
    }

    /**
     * 
     * 
     * @param value
     */
    public void setItemName(String value) {
        itemNameProperty().set(value);
    }

    /**
     * 
     */
    public StringProperty itemNameProperty() {
        if (itemName == null) {
            itemName = new SimpleStringProperty(this, "Item");
        }

        return itemName;
    }

    /**
     * 
     * 
     * @return
     */
    public int getItemAmount() {
        return itemAmountProperty().get();
    }

    /**
     * 
     * 
     * @param value
     */
    public void setItemAmount(int value) {
        itemAmountProperty().set(value);
    }

    /**
     * 
     */
    public IntegerProperty itemAmountProperty() {
        if (itemAmount == null) {
            itemAmount = new SimpleIntegerProperty(this, "Amount");
        }

        return itemAmount;
    }

    /**
     * 
     */
    public Item(int amount, String item) {
        setItemAmount(amount);
        setItemName(item);
    }
}