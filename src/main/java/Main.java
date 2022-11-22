import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.swing.*;


public class Main {
    public static void main(String[] args) throws Exception {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        JQLHandler.handleJQL("type = 'tech sheet bug' and (status != closed or created > -30d)", session);
        session.close();
        JOptionPane.showMessageDialog(null, "Task completed", "Success", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }
}