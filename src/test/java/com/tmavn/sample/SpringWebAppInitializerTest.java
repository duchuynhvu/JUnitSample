package com.tmavn.sample;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.DispatcherServlet;

import com.tmavn.sample.common.Utils;

import ch.qos.logback.classic.joran.JoranConfigurator;

//@RunWith(PowerMockRunner.class)
@PrepareForTest({ LoggerFactory.class, Utils.class, JoranConfigurator.class, SpringWebAppInitializer.class })
public class SpringWebAppInitializerTest {
    
    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSpringWebAppInitializer_staticBlock_success() throws Exception {
        
        SpringWebAppInitializer springWebAppInitializer = new SpringWebAppInitializer();
        
        // Cannot verify in static block code so just invoke it, if have any exception, this test will automatically fail
    }

    @Test
    public void testOnStartup_success() throws ServletException {
        ServletContext servletContext = Mockito.mock(ServletContext.class);
        ServletRegistration.Dynamic dispatcher = Mockito.mock(ServletRegistration.Dynamic.class);
        Mockito.when(servletContext.addServlet(Mockito.any(String.class), Mockito.any(DispatcherServlet.class)))
                .thenReturn(dispatcher);
        Mockito.doNothing().when(dispatcher).setLoadOnStartup(1);
        Mockito.when(dispatcher.addMapping("/")).thenReturn(null);

        SpringWebAppInitializer springWebAppInitializer = new SpringWebAppInitializer();
        springWebAppInitializer.onStartup(servletContext);
        Mockito.verify(dispatcher, Mockito.times(1)).addMapping("/");
    }

}
