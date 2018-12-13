package fi.tamk.tiko.ohjelmointi.gui;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import fi.tamk.tiko.ohjelmointi.json.map.JSONMappable;
import fi.tamk.tiko.ohjelmointi.json.map.JSONData;

import javax.persistence.*;

/**
 * Stores single shopping list item values.
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
@Entity
@JSONMappable
public class Item {

    /**
     * Stores item name.
     */
    @Column(name="itemName")
    @JSONData(key="itemName")
    private StringProperty itemName;

    /**
     * Stores item amount.
     */
    @Column(name="itemAmount")
    @JSONData(key="itemAmount")
    private LongProperty itemAmount;

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
        setItemName(item);
    }
}