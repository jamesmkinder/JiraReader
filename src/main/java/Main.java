import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.swing.*;

/**
 * Main method.  JQL method is hard-coded and fit for purpose to the STRTRK process.  If the program is to ever be utilized
 * for other projects, this method will need to be updated to allow for a JQL string to be passed as a main argument.
 */

public class Main {
    public static void main(String[] args) throws Exception {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        JQLHandler.handleJQL("type = 'tech sheet bug' and (status != closed or created > -455d)", session);
        session.close();
        System.exit(0);
    }
}