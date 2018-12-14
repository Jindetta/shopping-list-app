package fi.tamk.tiko.ohjelmointi.gui.managers;

import fi.tamk.tiko.ohjelmointi.gui.Item;

import org.hibernate.*;
import org.hibernate.cfg.*;

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
    public static void addItem(Item item) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.getTransaction();

        transaction.begin();
        session.persist(item);
        transaction.commit();

        session.close();
    }

    /**
     * 
     * @return
     */
    public static Item getItem() {
        Session session = sessionFactory.openSession();
        Item item = session.get(Item.class, 0);

        session.close();
        return item;
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