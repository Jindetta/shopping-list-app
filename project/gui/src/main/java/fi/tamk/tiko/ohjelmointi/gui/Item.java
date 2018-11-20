package fi.tamk.tiko.ohjelmointi.gui;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Stores single shopping list item values.
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
public class Item {

    /**
     * Stores item name.
     */
    private StringProperty itemName;

    /**
     * Stores item amount.
     */
    private IntegerProperty itemAmount;

    /**
     * Gets item name.
     *
     * @return Item name as String.
     */
    public String getItemName() {
        return itemNameProperty().get();
    }

    /**
     * Sets item name.
     *
     * @param value Item name.
     */
    public void setItemName(String value) {
        itemNameProperty().set(value);
    }

    /**
     * Gets item name as property field.
     *
     * @return StringProperty.
     */
    public StringProperty itemNameProperty() {
        if (itemName == null) {
            itemName = new SimpleStringProperty(this, "Item");
        }

        return itemName;
    }

    /**
     * Gets item amount.
     *
     * @return Amount as Integer.
     */
    public int getItemAmount() {
        return itemAmountProperty().get();
    }

    /**
     * Sets item amount.
     *
     * @param value Amount value.
     */
    public void setItemAmount(int value) {
        itemAmountProperty().set(value);
    }

    /**
     * Gets item amount as property field.
     *
     * @return IntegerProperty.
     */
    public IntegerProperty itemAmountProperty() {
        if (itemAmount == null) {
            itemAmount = new SimpleIntegerProperty(this, "Amount");
        }

        return itemAmount;
    }

    /**
     * Overrides default constructor.
     *
     * @param amount Item amount.
     * @param item   Item name.
     */
    public Item(int amount, String item) {
        setItemAmount(amount);
        setItemName(item);
    }
}