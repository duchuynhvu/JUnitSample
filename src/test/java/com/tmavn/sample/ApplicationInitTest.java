/*
 * Copyright©2017 NTT corp． All Rights Reserved．
 */
package com.tmavn.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContextEvent;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import com.tmavn.sample.common.JsonValidation;
import com.tmavn.sample.common.Utils;
import com.tmavn.sample.model.ModuleAccess;

@PrepareForTest({ Utils.class, ApplicationInit.class, Properties.class, AppConfig.class,
        LocalContainerEntityManagerFactoryBean.class, DriverManager.class, HibernateJpaVendorAdapter.class,
        LocalContainerEntityManagerFactoryBean.class, AbstractEntityManagerFactoryBean.class, Driver.class,
        Enumeration.class })
public class ApplicationInitTest {

    @InjectMocks
    public ApplicationInit applicationInit;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    public AppConfig appConfig;

    @Mock
    private static Logger spyLogger;

    @Before
    public void setUp() throws Exception {
        applicationInit = new ApplicationInit();
        appConfig = new AppConfig();
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test load config success.
     * 
     * @throws SecurityException
     * @throws Exception
     * 
     * @throws Exception         the exception
     */
    @Test(expected = ExceptionInInitializerError.class)
    public void testLoadConfig_exceptionInInitializerError_getModuleAccessList_null() throws Exception {
        ApplicationInit mock = PowerMockito.spy(applicationInit);

        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.class, "getModuleAccessList").thenReturn(null);

        ServletContextEvent servletContextEvent = mock(ServletContextEvent.class);
        mock.contextInitialized(servletContextEvent);
    }

    @Test(expected = ExceptionInInitializerError.class)
    public void testLoadConfig_exceptionInInitializerError_readPropertiesFile_null() throws Exception {
        PowerMockito.mockStatic(Utils.class);

        List<ModuleAccess> list = new ArrayList<ModuleAccess>();
        ModuleAccess moduleAccess = new ModuleAccess();
        list.add(moduleAccess);

        PowerMockito.when(Utils.class, "getModuleAccessList").thenReturn(list);
        PowerMockito.when(Utils.class, "readPropertiesFile", Mockito.anyString()).thenReturn(null);

        ApplicationInit mock = PowerMockito.spy(applicationInit);
        ServletContextEvent servletContextEvent = mock(ServletContextEvent.class);
        mock.contextInitialized(servletContextEvent);
    }

    @Test(expected = ExceptionInInitializerError.class)
    public void testLoadConfig_exceptionInInitializerError_checkHibernateConfig() throws Exception {
        PowerMockito.mockStatic(Utils.class);

        List<ModuleAccess> list = new ArrayList<ModuleAccess>();
        ModuleAccess moduleAccess = new ModuleAccess();
        list.add(moduleAccess);

        PowerMockito.when(Utils.class, "getModuleAccessList").thenReturn(list);
        PowerMockito.when(Utils.class, "readPropertiesFile", Mockito.anyString()).thenReturn(new Properties());

        ApplicationInit mock = PowerMockito.spy(applicationInit);
        ServletContextEvent servletContextEvent = mock(ServletContextEvent.class);
        mock.contextInitialized(servletContextEvent);
    }

    @Test
    public void testLoadConfig_success() throws Exception {
        PowerMockito.mockStatic(Utils.class);

        List<ModuleAccess> list = new ArrayList<ModuleAccess>();
        ModuleAccess moduleAccess = new ModuleAccess();
        list.add(moduleAccess);

        PowerMockito.when(Utils.class, "getModuleAccessList").thenReturn(list);

        Properties properties = new Properties();
        properties.put("moduleId", "SampleModule");

        PowerMockito.when(Utils.class, "readPropertiesFile", Mockito.anyString()).thenReturn(properties);

        PowerMockito.when(Utils.class, "checkHibernateConfig", Mockito.any(Properties.class)).thenReturn(true);

        ApplicationInit mock = PowerMockito.spy(applicationInit);
        ServletContextEvent servletContextEvent = mock(ServletContextEvent.class);
        mock.contextInitialized(servletContextEvent);
    }

    @Test(expected = ExceptionInInitializerError.class)
    public void testLoadConfig_exceptionInInitializerError_propertiesNoContainsKey() throws Exception {
        PowerMockito.mockStatic(Utils.class);

        List<ModuleAccess> list = new ArrayList<ModuleAccess>();
        ModuleAccess moduleAccess = new ModuleAccess();
        list.add(moduleAccess);
        PowerMockito.when(Utils.class, "getModuleAccessList").thenReturn(list);

        PowerMockito.when(Utils.class, "checkHibernateConfig", Mockito.any(Properties.class)).thenReturn(true);
        PowerMockito.when(Utils.class, "readPropertiesFile", Mockito.anyString()).thenReturn(new Properties());

        ApplicationInit mock = PowerMockito.spy(applicationInit);
        ServletContextEvent servletContextEvent = mock(ServletContextEvent.class);
        mock.contextInitialized(servletContextEvent);
    }

    /**
     * Test load config entity manager factory.
     *
     * @throws Exception the exception
     */
    @Test
    public void testEntityManagerFactory_success() throws Exception {
        LocalContainerEntityManagerFactoryBean factory = PowerMockito
                .mock(LocalContainerEntityManagerFactoryBean.class);
        PowerMockito.whenNew(LocalContainerEntityManagerFactoryBean.class).withNoArguments().thenReturn(factory);

        Whitebox.setInternalState(ApplicationInit.class, "config", appConfig);

        applicationInit.entityManagerFactory();
    }

    @Test
    public void testSimpleDateFormat_success() {
        SimpleDateFormat test = applicationInit.simpleDateFormat();
        assertEquals(test.toPattern(), "yyyy-MM-dd HH:mm:ss");
    }

    @Test
    public void testAsyncRestTemplate_success() {
        applicationInit.asyncRestTemplate();
    }

    @Test
    public void testTransactionManager_success() throws Exception {

        Whitebox.setInternalState(ApplicationInit.class, "config", appConfig);
        LocalContainerEntityManagerFactoryBean factory = PowerMockito
                .mock(LocalContainerEntityManagerFactoryBean.class);
        PowerMockito.whenNew(LocalContainerEntityManagerFactoryBean.class).withNoArguments().thenReturn(factory);

        applicationInit.transactionManager();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testContextDestroyed_SQLException() throws Exception {
        org.powermock.reflect.Whitebox.setInternalState(JsonValidation.class, "logger", spyLogger);
        PowerMockito.mockStatic(DriverManager.class);

        Enumeration<Driver> drivers = mock(Enumeration.class);
        PowerMockito.when(DriverManager.getDrivers()).thenReturn(drivers);

        PowerMockito.when(drivers.hasMoreElements()).thenReturn(true).thenReturn(false);

        Driver driver = mock(Driver.class);
        PowerMockito.when(drivers.nextElement()).thenReturn(driver);

        PowerMockito.doThrow(new SQLException()).when(DriverManager.class);
        DriverManager.deregisterDriver(driver);

        ServletContextEvent event = PowerMockito.mock(ServletContextEvent.class);

        // execute
        applicationInit.contextDestroyed(event);

        // verify
        PowerMockito.verifyStatic(DriverManager.class);
        DriverManager.deregisterDriver(driver);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testContextDestroyed_SQLExceptionConfigNull() throws Exception {
        org.powermock.reflect.Whitebox.setInternalState(JsonValidation.class, "logger", spyLogger);
        PowerMockito.mockStatic(DriverManager.class);

        Enumeration<Driver> drivers = mock(Enumeration.class);
        PowerMockito.when(DriverManager.getDrivers()).thenReturn(drivers);

        PowerMockito.when(drivers.hasMoreElements()).thenReturn(true).thenReturn(false);

        Driver driver = mock(Driver.class);
        PowerMockito.when(drivers.nextElement()).thenReturn(driver);

        PowerMockito.doThrow(new SQLException()).when(DriverManager.class);
        DriverManager.deregisterDriver(driver);

        ServletContextEvent event = PowerMockito.mock(ServletContextEvent.class);

//        AppConfig appConfig = null;
        Whitebox.setInternalState(ApplicationInit.class, "config", appConfig);
        // execute
        applicationInit.contextDestroyed(event);

        // verify
        PowerMockito.verifyStatic(DriverManager.class);
        DriverManager.deregisterDriver(driver);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testContextDestroyed_success() throws Exception {
        org.powermock.reflect.Whitebox.setInternalState(JsonValidation.class, "logger", spyLogger);
        Whitebox.setInternalState(ApplicationInit.class, "config", appConfig);
        PowerMockito.mockStatic(DriverManager.class);

        Enumeration<Driver> drivers = mock(Enumeration.class);
        PowerMockito.when(DriverManager.getDrivers()).thenReturn(drivers);

        PowerMockito.when(drivers.hasMoreElements()).thenReturn(true).thenReturn(false);

        Driver driver = mock(Driver.class);
        PowerMockito.when(drivers.nextElement()).thenReturn(driver);

        ServletContextEvent event = PowerMockito.mock(ServletContextEvent.class);
        // execute
        applicationInit.contextDestroyed(event);
        PowerMockito.verifyStatic(DriverManager.class);
        DriverManager.getDrivers();
    }
    

    @SuppressWarnings("unchecked")
    @Test
    public void testContextDestroyed_configIsNull_success() throws Exception {
        org.powermock.reflect.Whitebox.setInternalState(JsonValidation.class, "logger", spyLogger);
        appConfig = null;
        Whitebox.setInternalState(ApplicationInit.class, "config", appConfig);
        PowerMockito.mockStatic(DriverManager.class);

        Enumeration<Driver> drivers = mock(Enumeration.class);
        PowerMockito.when(DriverManager.getDrivers()).thenReturn(drivers);

        PowerMockito.when(drivers.hasMoreElements()).thenReturn(true).thenReturn(false);

        Driver driver = mock(Driver.class);
        PowerMockito.when(drivers.nextElement()).thenReturn(driver);

        ServletContextEvent event = PowerMockito.mock(ServletContextEvent.class);
        // execute
        applicationInit.contextDestroyed(event);
        PowerMockito.verifyStatic(DriverManager.class);
        DriverManager.getDrivers();
    }

    @Test
    public void testGetConfig_success() {
    	Whitebox.setInternalState(ApplicationInit.class, "config", appConfig);
        @SuppressWarnings("static-access")
        AppConfig appConfig = applicationInit.getConfig();
        assertNotNull(appConfig);
    }
}
