package com.tmavn.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.tmavn.sample.AppConfig;

public class AppConfigTest {

	/**
	 *  1/4. Tạo mock object như real object và inject dependency (Properties)
	 */
    @InjectMocks 
    private AppConfig appConfig;
    
    @Before
    public void setUp() throws Exception {
        /**
         * 2/4. Lệnh khởi tạo 
         *    để Mockito xử lý các Annotation của mockito được áp dụng trong class này
         */        
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test() {
    	/**
    	 *  3/4. Define dependency
    	 */
    	Properties prop = new Properties();
        prop.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        prop.setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/Sample");
        prop.setProperty("hibernate.connection.username", "postgres");
        prop.setProperty("hibernate.connection.password", "root");
        prop.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        prop.setProperty("hibernate.show_sql", "false");
        prop.setProperty("hibernate.hbm2ddl.auto", "update");

        appConfig.setHibProperties(prop);
        appConfig.setOperationId("id1");

        /**
         *  4/4. Verifying behavior
         */
        assertTrue(appConfig.getHibProperties().containsKey("hibernate.connection.driver_class"));
        assertEquals(appConfig.getOperationId(), "id1");
    }
}
