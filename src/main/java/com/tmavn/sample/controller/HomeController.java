/*
 * Demo project
 */
package com.tmavn.sample.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Class HomeController.
 *
 */
@RestController
public class HomeController {

    /** The logger. */
    final Logger logger = LoggerFactory.getLogger(HomeController.class);

    /**
     * Show welcome message.
     *
     * @return the string
     */
    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String showWelcomeMessage() {
        logger.debug("Show index page");
        return "<html><body><h1>Sample Module is running!</h1></body></html>";
    }
    
    

}
