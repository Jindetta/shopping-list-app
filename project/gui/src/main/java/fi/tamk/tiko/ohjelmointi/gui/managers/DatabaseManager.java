package fi.tamk.tiko.ohjelmointi.gui.managers;

import fi.tamk.tiko.ohjelmointi.gui.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.hibernate.query.NativeQuery;
import org.hibernate.cfg.*;
import org.hibernate.*;

import java.math.BigInteger;

/**
 * Manages Database interaction with H2.
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
public abstract class DatabaseManager {

    /**
     * Stores factory for creating new sessions.
     */
    private static SessionFactory sessionFactory;

    /**
     * Initializes memory database configuration.
     */
    public static void initialize() {
        Configuration config = new Configuration().configure();
        sessionFactory = config.buildSessionFactory();
    }

    /**
     * Adds items to the database.
     * @param items List of Item objects.
     */
    public static void addItems(ObservableList<Item> items) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.getTransaction();

        transaction.begin();
        session.createSQLQuery("DELETE items").executeUpdate();

        for (Item item : items) {
            session.save(item);
        }

        transaction.commit();

        session.close();
    }

    /**
     * Gets Items from database.
     * @return List of Item objects.
     */
    @SuppressWarnings("unchecked")
    public static ObservableList<Item> getItems() {
        Session session = sessionFactory.openSession();
        ObservableList<Item> items = FXCollections.observableArrayList();

        NativeQuery<Object[]> rows = session.createSQLQuery("SELECT itemAmount, itemName FROM items");

        for(Object[] row : rows.list()) {
            items.add(new Item(((BigInteger) row[0]).longValue(), (String) row[1]));
        }

        session.close();
        return items;
    }

    /**
     * Closes the session factory.
     */
    public static void destroy() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}