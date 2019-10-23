/*
 * Copyright©2017 NTT corp． All Rights Reserved．
 */
package com.tmavn.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.stubbing.answers.ThrowsException;
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

	/**
	 * Khởi tạo mock object như real object
	 */
    @InjectMocks
    public ApplicationInit applicationInit;

    /**
     * Khởi tạo để có thể sử dụng các mock annotation
     */
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    /**
     * khởi tạo các mock object
     */
    @Mock
    public AppConfig appConfig;
    
    @Mock
    public ServletContextEvent mockServletContextEvent;

    @Mock
    private static Logger spyLogger;

//    @Before
//    public void setUp() throws Exception {
//        applicationInit = new ApplicationInit();
//        appConfig = new AppConfig();
//        MockitoAnnotations.initMocks(this);
//    }

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
        // When
        // --mock static method
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.class, "getModuleAccessList").thenReturn(null);
        // Execute method under test
        ApplicationInit spyAppInit = PowerMockito.spy(applicationInit);
        spyAppInit.contextInitialized(mockServletContextEvent);
        // Verifying: expected = ExceptionInInitializerError.class
        
    }

    @Test(expected = ExceptionInInitializerError.class)
    public void testLoadConfig_exceptionInInitializerError_readPropertiesFile_null() throws Exception {
        // Given
        // --define data
    	List<ModuleAccess> list = new ArrayList<ModuleAccess>();
        ModuleAccess moduleAccess = new ModuleAccess();
        list.add(moduleAccess);
        // When	
        // --mock static method
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.class, "getModuleAccessList").thenReturn(list);
        PowerMockito.when(Utils.class, "readPropertiesFile", Mockito.anyString()).thenReturn(null);
        // Execute method under test
        ApplicationInit mock = PowerMockito.spy(applicationInit);
        mock.contextInitialized(mockServletContextEvent);
        // Verifying: expected = ExceptionInInitializerError.class
        
    }

    @Test(expected = ExceptionInInitializerError.class)
    public void testLoadConfig_exceptionInInitializerError_checkHibernateConfig() throws Exception {
        // Given
    	// --define data
    	List<ModuleAccess> list = new ArrayList<ModuleAccess>();
        ModuleAccess moduleAccess = new ModuleAccess();
        list.add(moduleAccess);
        // When
        // --mock static method
    	PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.class, "getModuleAccessList").thenReturn(list);
        PowerMockito.when(Utils.class, "readPropertiesFile", Mockito.anyString()).thenReturn(new Properties());
        // Execute method under test
        ApplicationInit mock = PowerMockito.spy(applicationInit);
        mock.contextInitialized(mockServletContextEvent);
        // Verifying the contextInitialized method is called
        Mockito.verify(mock).contextInitialized(mockServletContextEvent);

        //using in case if don't have the expected annotation
        //Mockito.doThrow(new ExceptionInInitializerError()).when(mock).contextInitialized(mockServletContextEvent);
    }

    @Test
    public void testLoadConfig_success() throws Exception {
    	// Given
    	// --define List
        List<ModuleAccess> list = new ArrayList<ModuleAccess>();
        ModuleAccess moduleAccess = new ModuleAccess();
        list.add(moduleAccess);
        // --define Properties
        Properties properties = new Properties();
        properties.put("moduleId", "SampleModule");
        // When
        // --mock static method
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.class, "getModuleAccessList").thenReturn(list);        
        PowerMockito.when(Utils.class, "readPropertiesFile", Mockito.anyString()).thenReturn(properties);
        PowerMockito.when(Utils.class, "checkHibernateConfig", Mockito.any(Properties.class)).thenReturn(true);
        // Execute method under test
        ApplicationInit mock = PowerMockito.spy(applicationInit);
        mock.contextInitialized(mockServletContextEvent);
        // Verifying        
        Mockito.verify(mock).contextInitialized(mockServletContextEvent);
    }

    @Test(expected = ExceptionInInitializerError.class)
    public void testLoadConfig_exceptionInInitializerError_propertiesNoContainsKey() throws Exception {
        // Given
        List<ModuleAccess> list = new ArrayList<ModuleAccess>();
        ModuleAccess moduleAccess = new ModuleAccess();
        list.add(moduleAccess);
        // When
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.class, "getModuleAccessList").thenReturn(list);
        PowerMockito.when(Utils.class, "checkHibernateConfig", Mockito.any(Properties.class)).thenReturn(true);
        PowerMockito.when(Utils.class, "readPropertiesFile", Mockito.anyString()).thenReturn(new Properties());
        // Execute method under test
        applicationInit.contextInitialized(mockServletContextEvent);
    }

    /**
     * Test load config entity manager factory.
     *
     * @throws Exception the exception
     */
    @Test
    public void testEntityManagerFactory_success() throws Exception {
    	// Given
        // --Mock constructor
    	LocalContainerEntityManagerFactoryBean mockFactory = mock(LocalContainerEntityManagerFactoryBean.class);
        PowerMockito.whenNew(LocalContainerEntityManagerFactoryBean.class).withNoArguments().thenReturn(mockFactory);
        // --Mock private field
        Whitebox.setInternalState(ApplicationInit.class, "config", appConfig);
        // Execute method under test
        applicationInit.entityManagerFactory();
    }

    @Test
    public void testSimpleDateFormat_success() {
        // Execute method under test
    	SimpleDateFormat test = applicationInit.simpleDateFormat();
        // Verifying
    	assertEquals(test.toPattern(), "yyyy-MM-dd HH:mm:ss");
    }

    @Test
    public void testAsyncRestTemplate_success() {
    	// Execute
    	applicationInit.asyncRestTemplate();
    	// Verifying
    	// ...
    }

    @Test
    public void testTransactionManager_success() throws Exception {
    	// Given
    	// --Mock private field
        Whitebox.setInternalState(ApplicationInit.class, "config", appConfig);
        // --Mock constructor
        LocalContainerEntityManagerFactoryBean mockFactory = mock(LocalContainerEntityManagerFactoryBean.class);
    	PowerMockito.whenNew(LocalContainerEntityManagerFactoryBean.class).withNoArguments().thenReturn(mockFactory);
        // Execute method under test
    	applicationInit.transactionManager();
    	// Verifying
    	// ...
    	
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testContextDestroyed_SQLException() throws Exception {
        // Given    	
		Enumeration<Driver> drivers = mock(Enumeration.class);
    	Driver driver = mock(Driver.class);
    	ServletContextEvent event = mock(ServletContextEvent.class);
    	// When
    	PowerMockito.mockStatic(DriverManager.class);
        PowerMockito.when(DriverManager.getDrivers()).thenReturn(drivers);
        PowerMockito.when(drivers.hasMoreElements()).thenReturn(true).thenReturn(false);
        PowerMockito.when(drivers.nextElement()).thenReturn(driver);
        // --Defining exception
        PowerMockito.doThrow(new SQLException()).when(DriverManager.class);
        DriverManager.deregisterDriver(driver);
        // Execute method under test
        applicationInit.contextDestroyed(event);
        // verifying
        PowerMockito.verifyStatic(DriverManager.class);
        DriverManager.deregisterDriver(driver);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testContextDestroyed_success() throws Exception {
        // Given
    	// --Mock private field
        Whitebox.setInternalState(ApplicationInit.class, "config", appConfig);
		Enumeration<Driver> drivers = mock(Enumeration.class);
		Driver driver = mock(Driver.class);
        ServletContextEvent mockEvent = mock(ServletContextEvent.class);
		// When
		PowerMockito.mockStatic(DriverManager.class);
		PowerMockito.when(DriverManager.getDrivers()).thenReturn(drivers);
        PowerMockito.when(drivers.hasMoreElements()).thenReturn(true).thenReturn(false);
        PowerMockito.when(drivers.nextElement()).thenReturn(driver);
        // Execute method under test
        applicationInit.contextDestroyed(mockEvent);
        // Verifying
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
