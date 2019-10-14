/*
 * Demo project
 */
package com.tmavn.sample;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.tmavn.sample.common.Constant;
import com.tmavn.sample.common.Utils;

/**
 * The Class ApplicationInit.
 */
@WebListener
@EnableWebMvc
@Configuration
@ComponentScan("com.tmavn.sample")
@EnableTransactionManagement
@EnableJpaRepositories("com.tmavn.sample.repository")
public class ApplicationInit extends WebMvcConfigurerAdapter implements ServletContextListener {

    /** The Constant logger. */
    private final static Logger logger = LoggerFactory.getLogger(ApplicationInit.class);

    /** Configuration class. */
    private static AppConfig config = null;

    /** The Constant MODEL_PACKAGE_TO_SCAN. */
    private static final String MODEL_PACKAGE_TO_SCAN = "com.tmavn.sample.entity";

    /**
     * Entity manager factory.
     *
     * @return the entity manager factory
     */
    @Bean
    public EntityManagerFactory entityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan(MODEL_PACKAGE_TO_SCAN);
        factory.setJpaProperties(config.getHibProperties());
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Bean
    public SimpleDateFormat simpleDateFormat() {
        SimpleDateFormat format = new SimpleDateFormat(Constant.DATE_PATTERN);
        return format ;
    }
    
    @Bean
    public AsyncRestTemplate asyncRestTemplate() {
        return new AsyncRestTemplate();
    }
    
    /**
     * Transaction manager.
     *
     * @return the jpa transaction manager
     */
    @Bean
    public JpaTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory());
        return transactionManager;
    }

    /**
     * Context initialized.
     *
     * @param servletContextEvent the servlet context event
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // Load config
        loadConfig();
    }

    /**
     * Load config.
     *
     * @throws ExceptionInInitializerError the exception in initializer error
     */
    private void loadConfig() throws ExceptionInInitializerError {
        logger.debug("IN - loadConfig()");

        // Read FbAccess.json
        if (Utils.getModuleAccessList() == null) {
            logger.error("Get fbAccessList failed");
            throw new ExceptionInInitializerError();
        }

        // Read owner config file
        config = new AppConfig();
        String fileConfig = Utils.getBaseDirectory() + Constant.SAMPLE_CONFIG;
        Properties properties = Utils.readPropertiesFile(fileConfig);
        if (properties == null) {
            logger.error("Read fileConfig error: ", fileConfig);
            throw new ExceptionInInitializerError();
        }

        // check properties config
        if (!Utils.checkHibernateConfig(properties)) {
            throw new ExceptionInInitializerError();
        }

        if (properties.containsKey(Constant.ConfigProperties.MODULE_ID)) {
            config.setOperationId(properties.getProperty(Constant.ConfigProperties.MODULE_ID));
        } else {
            logger.error("Have no setting ", Constant.ConfigProperties.MODULE_ID);
            throw new ExceptionInInitializerError();
        }

        config.setHibProperties(properties);

        logger.debug("OUT - loadConfig()");
    }

    /**
     * Context destroyed.
     *
     * @param event the event
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {

        logger.debug("IN - contextDestroyed()");
        // This manually deregisters JDBC driver
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                logger.debug("OUT - contextDestroyed()");
            } catch (SQLException e) {
                logger.error("Exception: ", e);
                logger.debug("OUT - contextDestroyed()");
            }
        }

        if (config != null) {
            config = null;
        }
        logger.debug("OUT - contextDestroyed()");
    }

    /**
     * Gets the config.
     *
     * @return the appConfig
     */
    public static AppConfig getConfig() {
        return config;
    }
}
