/*
 * Demo project
 */
package com.tmavn.sample.common;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;
import com.tmavn.sample.model.CheckResult;

/**
 * The Class ValidationUtils.
 */
public class JsonValidation {

    /** The Constant logger. */
    private final static Logger logger = LoggerFactory.getLogger(JsonValidation.class);

    /**
     * Check json validation.
     *
     * @param schemaFile the schema file
     * @param json       the json
     * @return the check result
     */
    public static CheckResult validate(String schemaFile, String json) {
        logger.debug("IN - validate()");
        logger.debug("Validate json with schema file: {}", schemaFile);
        // check json format
        CheckResult result = new CheckResult();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.readTree(json);
            result.setSuccess(true);
        } catch (JsonProcessingException jpe) {
            result.setMessage(jpe.getLocalizedMessage());
            result.setSuccess(false);
            return result;
        } catch (IOException e) {
            result.setMessage(e.getLocalizedMessage());
            result.setSuccess(false);
            return result;
        }
        // check json schema
        result = new CheckResult(true, CheckResult.MSG_OK);
        String message = "";
        try {
            final JsonNode jsonData = JsonLoader.fromString(json);
            final JsonNode jsonSchema = JsonLoader.fromPath(schemaFile);
            final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
            JsonValidator validator = factory.getValidator();
            ProcessingReport processingReport = validator.validate(jsonSchema, jsonData);

            if (!processingReport.isSuccess()) {
                result.setSuccess(false);
                // Parse report.toString() to get mandatory fields name
                String reportMsg = processingReport.toString();

                if (reportMsg.contains("missing: ")) {
                    String[] msg = reportMsg.split("missing: ");
                    message += CheckResult.MSG_MANDATORY_ATT_NG + msg[1].split("\\\n")[0] + ")";

                } else if (reportMsg.contains("expected: ")) {
                    String[] msg = reportMsg.split("expected: ");
                    message += CheckResult.MSG_MANDATORY_TYPE_NG + msg[1].split("\\\n")[0] + ")";
                } else if (reportMsg.contains("oneOf")) {
                    String[] msg = reportMsg.split("reports:");
                    message += CheckResult.MSG_INSTANCE_FAILED_NG + msg[1].split("\\\n")[0] + ")";
                } else if (reportMsg.contains("unwanted")) {
                    String[] msg = reportMsg.split("unwanted:");
                    message += CheckResult.MSG_UN_WANTED_NG + msg[1].split("\\\n")[0] + ")";
                } else {
                    message += CheckResult.MSG_JSON_NG;
                }

                logger.warn("Check json schema failed \n{}", reportMsg);
            }
            logger.debug("OUT - validate()");
        } catch (Exception e) {
            logger.error("Exception: ", e);
            result.setSuccess(false);
            result.setMessage(CheckResult.MSG_JSON_NG);
            return result;
        }

        result.setMessage(message);
        return result;
    }

}
