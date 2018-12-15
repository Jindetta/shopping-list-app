package fi.tamk.tiko.ohjelmointi.gui.managers;

import fi.tamk.tiko.ohjelmointi.gui.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.hibernate.query.NativeQuery;
import org.hibernate.cfg.*;
import org.hibernate.*;

import java.math.BigInteger;

/**
 * 
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
public abstract class DatabaseManager {

    /**
     * 
     */
    private static SessionFactory sessionFactory;

    /**
     * 
     */
    public static void initialize() {
        Configuration config = new Configuration().configure();
        sessionFactory = config.buildSessionFactory();
    }

    /**
     * 
     * @param items
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
     * 
     * @return
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
     * 
     */
    public static void destroy() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}