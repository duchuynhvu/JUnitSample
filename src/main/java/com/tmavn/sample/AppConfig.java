/*
 * Demo project
 */
package com.tmavn.sample;

import java.util.Properties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppConfig {

    /** The hibernate properties. */
    private Properties hibProperties;

    /** The operation id */
    private String operationId;

}
