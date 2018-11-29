package fi.tamk.tiko.ohjelmointi.gui;

import javafx.beans.property.LongProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import fi.tamk.tiko.ohjelmointi.json.map.JSONMappable;

/**
 * Stores single shopping list item values.
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
public class Item implements JSONMappable {

    /**
     * Stores item name.
     */
    private StringProperty itemName;

    /**
     * Stores item amount.
     */
    private LongProperty itemAmount;

    /**
     * Stores item checkmark.
     */
    private BooleanProperty itemMark;

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
    public Long getItemAmount() {
        return itemAmountProperty().get();
    }

    /**
     * Sets item amount.
     *
     * @param value Amount value.
     */
    public void setItemAmount(Long value) {
        itemAmountProperty().set(value);
    }

    /**
     * Gets item amount as property field.
     *
     * @return IntegerProperty.
     */
    public LongProperty itemAmountProperty() {
        if (itemAmount == null) {
            itemAmount = new SimpleLongProperty(this, "Amount");
        }

        return itemAmount;
    }

    /**
     * Gets item amount.
     *
     * @return Amount as Integer.
     */
    public boolean getItemMark() {
        return itemMarkProperty().get();
    }

    /**
     * Sets item amount.
     *
     * @param value Amount value.
     */
    public void setItemMark(boolean value) {
        itemMarkProperty().set(value);
    }

    /**
     * Gets item amount as property field.
     *
     * @return IntegerProperty.
     */
    public BooleanProperty itemMarkProperty() {
        if (itemMark == null) {
            itemMark = new SimpleBooleanProperty(this, "Marked");
        }

        return itemMark;
    }

    /**
     * Overrides default constructor.
     */
    public Item() {
        this(0L, "-");
    }

    /**
     * Overrides default constructor.
     *
     * @param amount Item amount.
     * @param item   Item name.
     */
    public Item(Long amount, String item) {
        setItemAmount(amount);
        setItemMark(false);
        setItemName(item);
    }

    /**
     * 
     */
    @Override
    public boolean isJSONMappable() {
        return true;
    }
}