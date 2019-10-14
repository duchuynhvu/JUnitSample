/*
 * Demo project
 */
package com.tmavn.sample;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.tmavn.sample.common.Constant;
import com.tmavn.sample.common.Utils;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;

public class SpringWebAppInitializer implements WebApplicationInitializer {

    private final static Logger logger = LoggerFactory.getLogger(SpringWebAppInitializer.class);

    // Reset the Logback configuration.
    static {
        logger.debug("IN - Logger Configuration");
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.reset();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(loggerContext);
        try {
            String path = Utils.getBaseDirectory() + Constant.LOG_BACK_PATH;
            configurator.doConfigure(path);
            logger.debug("Logger config path: {}",path);
            logger.debug("OUT - Logger Configuration");
        } catch (Exception e) {
            logger.error("Exception: ", e);
            throw new ExceptionInInitializerError();
        }
    }

    /**
     * On startup.
     *
     * @param servletContext the servlet context
     * @throws ServletException the servlet exception
     */
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
        appContext.register(ApplicationInit.class);
        // servletContext.addListener(new ApplicationContextListener());
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("SpringDispatcher", new DispatcherServlet(appContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }
}
