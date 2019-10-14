/*
 * Copyright©2017 NTT corp． All Rights Reserved．
 */
package com.tmavn.sample.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmavn.sample.model.CheckResult;

public class JsonValidationTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    /** The json validation. */
    @InjectMocks
    private JsonValidation jsonValidation;

    /** The spy logger. */
    @Mock
    private static Logger spyLogger;

    /**
     * Sets the up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        jsonValidation = new JsonValidation();
        org.powermock.reflect.Whitebox.setInternalState(JsonValidation.class, "logger", spyLogger);
    }

    /**
     * Test json validation success.
     * 
     * @throws IOException
     */
    @Test
    public void testJsonValidation_successful() throws IOException {
        String json = "{\"callback\":\"http://localhost:8081/ListenerProject/api/v1/listener\",\"query\":\"state=Processing\"}";

        @SuppressWarnings("static-access")
        CheckResult result = jsonValidation.validate("src/test/resources/jsonschema-test/jsonschematest.json", json);

        // verify
        assertTrue(result.isSuccess());
        assertEquals(result.getMessage(), "");
        Mockito.verify(spyLogger).debug(Mockito.contains("OUT - validate()"));
    }

    /**
     * Test json validation unsuccessful case miss properties.
     * 
     * @throws IOException
     */
    @Test
    public void testJsonValidation_unsuccessful_missing() throws IOException {
        String json = "{\"callback\":\"http://localhost:8081/ListenerProject/api/v1/listener\"}";

        @SuppressWarnings("static-access")
        CheckResult result = jsonValidation.validate("src/test/resources/jsonschema-test/jsonschematest.json", json);

        // verify
        assertFalse(result.isSuccess());
        assertEquals(result.getMessage(), "Mandatory Attribute NG: [\"query\"])");
    }

    @Test
    public void testJsonValidation_unsuccessful_expected() throws IOException {
        String json = "{\"callback\":123,\"query\":\"state=Processing\"}";

        @SuppressWarnings("static-access")
        CheckResult result = jsonValidation.validate("src/test/resources/jsonschema-test/jsonschematest.json", json);

        // verify
        assertFalse(result.isSuccess());
        assertEquals(result.getMessage(), "Mandatory Type NG: [\"string\"])");
    }

    /**
     * Test json validation unsuccessful unwanted
     * 
     * @throws IOException
     */
    @Test
    public void testJsonValidation_unsuccessful_unwanted() throws IOException {
        String json = "{  \r\n" + "   \"iddd\":123,\r\n" + "   \"userId\":\"user1\",\r\n"
                + "   \"callback\":\"http://localhost:8081/ListenerProject/api/v1/listener\",\r\n"
                + "   \"query\":\"state=Processing\"\r\n" + "}";

        @SuppressWarnings("static-access")
        CheckResult result = jsonValidation.validate("src/test/resources/jsonschema-test/jsonschematest2.json", json);

        // verify
        assertFalse(result.isSuccess());
        assertEquals(result.getMessage(), "ERROR(Unwanted Attribute:  [\"iddd\"])");
    }

    /**
     * Test json validation oneof.
     * 
     * @throws IOException
     * 
     */
    @Test
    public void testJsonValidation_oneof() throws IOException {
        String json = "{\"callback\":\"http://localhost:8081/ListenerProject/api/v1/listener\",\"query\":\"state=Processing\"}";
        @SuppressWarnings("static-access")
        CheckResult result = jsonValidation.validate("src/test/resources/jsonschema-test/jsonschematest3.json", json);

        // verify
        assertFalse(result.isSuccess());
        assertEquals(result.getMessage(),
                "Instance failed to match exactly one schema:  {\"/oneOf/0\":[{\"level\":\"error\",\"schema\":{\"loadingURI\":\"#\",\"pointer\":\"/oneOf/0\"},\"instance\":{\"pointer\":\"\"},\"domain\":\"validation\",\"keyword\":\"required\",\"message\":\"object has missing required properties ([\\\"firstName\\\"])\",\"required\":[\"firstName\"],\"missing\":[\"firstName\"]}],\"/oneOf/1\":[{\"level\":\"error\",\"schema\":{\"loadingURI\":\"#\",\"pointer\":\"/oneOf/1\"},\"instance\":{\"pointer\":\"\"},\"domain\":\"validation\",\"keyword\":\"additionalProperties\",\"message\":\"object instance has properties which are not allowed by the schema: [\\\"callback\\\",\\\"query\\\"]\",\"unwanted\":[\"callback\",\"query\"]}]})");
    }

    /**
     * Test json validation exception.
     * 
     * @throws IOException
     */
    @Test
    public void testJsonValidation_JsonProcessingException() throws IOException {
        String json = "{{\"callback\":\"http://localhost:8081/ListenerProject/api/v1/listener\",\"query\":\"state=Processing\"}";
        @SuppressWarnings("static-access")
        CheckResult result = jsonValidation.validate("src/test/resources/jsonschema-test/jsonschematest.json", json);

        // verify
        assertFalse(result.isSuccess());
        assertEquals(result.getMessage(),
                "Unexpected character ('{' (code 123)): was expecting double-quote to start field name\n"
                        + " at [Source: {{\"callback\":\"http://localhost:8081/ListenerProject/api/v1/listener\",\"query\":\"state=Processing\"}; line: 1, column: 3]");
    }

    /**
     * Test json validation IO exception.
     * 
     * @throws IOException
     */
    @SuppressWarnings("static-access")
    @PrepareForTest({ JsonValidation.class })
    @Test
    public void testJsonValidation_IOException() throws Exception {
        // initialize mock data
        String json = "{\"callback\":\"http://localhost:8081/ListenerProject/api/v1/listener\",\"query\":\"state=Processing\"}";

        // mock method
        ObjectMapper mockObjectMapper = Mockito.mock(ObjectMapper.class);
        PowerMockito.whenNew(ObjectMapper.class).withAnyArguments().thenReturn(mockObjectMapper);
        PowerMockito.doThrow(new IOException()).when(mockObjectMapper).readTree(Mockito.anyString());

        // execute
        CheckResult result = jsonValidation.validate("src/test/resources/jsonschema-test/jsonschematest.json", json);

        // verify
        assertFalse(result.isSuccess());
    }

    /**
     * Test json validation json schema file error.
     */
    @Test
    public void testJsonValidation_unsuccessful_error() {
        String json = "{\r\n" + "   \"street_address\": \"1600 Pennsylvania Avenue NW\",\r\n"
                + "   \"city\": \"Washington\",\r\n" + "   \"state\": \"DC\",\r\n" + "   \"type\": \"residentiall\"\r\n"
                + "}";

        @SuppressWarnings("static-access")
        CheckResult result = jsonValidation.validate("src/test/resources/jsonschema-test/jsonschematest4.json", json);

        // verify
        assertFalse(result.isSuccess());
        assertEquals(result.getMessage(), "ERROR(JSON format NG)");
    }

    @Test
    public void testJsonValidation_jsonSchemaFile_error() {
        String json = "{\"callback\":\"http://localhost:8081/ListenerProject/api/v1/listener\",\"query\":\"state=Processing\"}";

        @SuppressWarnings("static-access")
        CheckResult result = jsonValidation.validate("D://tmp.json", json);

        // verify
        assertFalse(result.isSuccess());
        assertEquals(result.getMessage(), "ERROR(JSON format NG)");
    }
}
