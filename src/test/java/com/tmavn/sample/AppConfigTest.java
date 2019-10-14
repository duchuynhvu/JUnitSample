package com.tmavn.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.tmavn.sample.AppConfig;

public class AppConfigTest {

    @InjectMocks
    public AppConfig appConfig;

    @Before
    public void setUp() throws Exception {
        appConfig = new AppConfig();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test() {
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

        assertTrue(appConfig.getHibProperties().containsKey("hibernate.connection.driver_class"));
        assertEquals(appConfig.getOperationId(), "id1");
    }
}
