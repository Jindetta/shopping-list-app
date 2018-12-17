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
@Table(name="items")
public class Item {

    /**
     * Stores item id (primary key).
     */
    @Id
    @Column(name="itemId", updatable=false, nullable=false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public int itemId;

    /**
     * Stores item name.
     */
    @Column(name="itemName")
    @JSONData(key="itemName")
    private String itemName;

    /**
     * Stores item amount.
     */
    @Column(name="itemAmount")
    @JSONData(key="itemAmount")
    private Long itemAmount;

    /**
     * Stores item properties.
     */
    @Transient
    private ItemProperties properties;

    /**
     * Gets item name.
     * @return Item name as String.
     */
    public String getItemName() {
        return properties.getItem();
    }

    /**
     * Sets item name.
     * @param value Item name.
     */
    public void setItemName(String value) {
        properties.setItem(value);
    }

    /**
     * Gets item amount.
     * @return Amount as Integer.
     */
    public Long getItemAmount() {
        return properties.getAmount();
    }

    /**
     * Sets item amount.
     * @param value Amount value.
     */
    public void setItemAmount(Long value) {
        properties.setAmount(value);
    }

    /**
     * Overrides default constructor.
     */
    public Item() {
        this(0L, "-");
    }

    /**
     * Overloads default contructor.
     * @param amount Item amount.
     * @param item   Item name.
     */
    public Item(Long amount, String item) {
        properties = new ItemProperties(amount, item);
    }

    /**
     * Inner class for properties.
     */
    private class ItemProperties {

        /**
         * Stores item name.
         */
        private StringProperty item;

        /**
         * Stores item amount.
         */
        private LongProperty amount;

        /**
         * Gets item name.
         * @return Item name as String.
         */
        public String getItem() {
            return itemProperty().get();
        }

        /**
         * Sets item name.
         * @param value Item name.
         */
        public void setItem(String value) {
            itemProperty().set(itemName = value);
        }

        /**
         * Gets item name as property field.
         * @return {@link StringProperty}.
         */
        public StringProperty itemProperty() {
            if (item == null) {
                item = new SimpleStringProperty(this, "Item");
            }

            return item;
        }

        /**
         * Gets item amount.
         * @return Amount as Integer.
         */
        public Long getAmount() {
            return amountProperty().get();
        }

        /**
         * Sets item amount.
         * @param value Amount value.
         */
        public void setAmount(Long value) {
            amountProperty().set(itemAmount = value);
        }

        /**
         * Gets item amount as property field.
         * @return {@link IntegerProperty}.
         */
        public LongProperty amountProperty() {
            if (amount == null) {
                amount = new SimpleLongProperty(this, "Amount");
            }

            return amount;
        }

        /**
         * Overrides default contructor.
         * @param amount Item amount.
         * @param item   Item name.
         */
        public ItemProperties(Long amount, String item) {
            setAmount(amount);
            setItem(item);
        }
    }
}