/*
 * Demo project
 */
package com.tmavn.sample.model;

import lombok.Getter;
import lombok.Setter;

/**
 * The Class CheckResult.
 */
@Getter
@Setter
public class CheckResult {

    /** The Constant MSG_OK. */
    public static final String MSG_OK = "OK";

    /** The Constant MSG_HEADER_NG. */
    public static final String MSG_HEADER_NG = "ERROR(HTTP Header NG)";

    /** The Constant MSG_USERID_NG. */
    public static final String MSG_LC_OP_OCCURRENCE_ID_NG = "ERROR(X-LcOpOccurrenceId NG)";

    /** The Constant MSG_OPERATORID_NG. */
    public static final String MSG_OPERATORID_NG = "ERROR(X-OperatorID NG)";

    /** The Constant MSG_JSON_NG. */
    public static final String MSG_JSON_NG = "ERROR(JSON format NG)";

    /** The Constant MSG_MANDATORY_ATT_NG. */
    public static final String MSG_MANDATORY_ATT_NG = "Mandatory Attribute NG: ";

    /** The Constant MSG_MANDATORY_TYPE_NG. */
    public static final String MSG_MANDATORY_TYPE_NG = "Mandatory Type NG: ";

    /** The Constant MSG_INSTANCE_FAILED_NG */
    public static final String MSG_INSTANCE_FAILED_NG = "Instance failed to match exactly one schema: ";

    /** The Constant MSG_REFERENCE_NG. */
    public static final String MSG_REFERENCE_NG = "ERROR(Reference NG: ";

    /** The Constant MSG_UN_WANTED_NG. */
    public static final String MSG_UN_WANTED_NG = "ERROR(Unwanted Attribute: ";
    
    /** The Constant MSG_ALREADY_EXISTS_NG. */
    public static final String MSG_ALREADY_EXISTS_NG = "ERROR(Already exists: ";
    
    
    /** The success. */
    private boolean success;

    /** The message. */
    private String message;

    /**
     * Instantiates a new check result.
     *
     * @param success the success
     * @param message the message
     */
    public CheckResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     * Instantiates a new check result.
     */
    public CheckResult() {

    }
}
