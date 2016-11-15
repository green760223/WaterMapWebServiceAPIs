package tw.com.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import org.apache.commons.dbcp2.cpdsadapter.DriverAdapterCPDS;
import org.apache.commons.dbcp2.datasources.SharedPoolDataSource;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import tw.com.util.PropertyLoader;

public class DbUtil {

	

	private static final SessionFactory sessionFactory = buildSessionFactory();
	

	public static Properties props = new Properties();
	

	
	private static SessionFactory buildSessionFactory() {
        try {
        	
  	        	
            Configuration configuration = new Configuration();
            configuration.configure();
//            configuration.setProperties(System.getProperties());
//            configuration.setProperty("hibernate.connection.url", PropertyLoader.props.getProperty("db.connection.url"));
//            configuration.setProperty("hibernate.connection.username", PropertyLoader.props.getProperty("db.connection.username"));
//            configuration.setProperty("hibernate.connection.password", PropertyLoader.props.getProperty("db.connection.pass"));

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            return sessionFactory;

        }
        catch (Throwable ex) {
            
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
	
	public static Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
	
	public static Session getOpenSession() {
		
		return sessionFactory.openSession();


    }
	
	public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
	


}
