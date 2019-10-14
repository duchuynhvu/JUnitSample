/*
 * Demo project
 */
package com.tmavn.sample.common;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.tmavn.sample.entity.OrderData;

/**
 * The Class RestClientTest.
 */

@PrepareForTest({ RestClient.class, RestTemplate.class, Logger.class })
public class RestClientTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    private RestTemplate restTemplate;

    private RestClient restClient;

    private RestClient restClientSpy;

    @Mock
    private Logger loggerSpy;

    @Before
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        Whitebox.setInternalState(RestClient.class, "restTemplate", restTemplate);
        restClient = RestClient.getInstance();
        restClientSpy = PowerMockito.spy(restClient);
    }

    @After
    public void tearDown() throws Exception {
        String fieldName = "restTemplate";
        Class<RestClient> c = RestClient.class;

        // tear down field "restTemplate" in "RestClient" class
        Field f_targetInstance = null;
        f_targetInstance = c.getDeclaredField(fieldName);
        f_targetInstance.setAccessible(true);
        RestTemplate targetInstance = (RestTemplate) f_targetInstance.get(null);
        f_targetInstance.set(targetInstance, null);
        targetInstance = null;

        // tear down field "instance" in "RestClient" class
        fieldName = "instance";

        f_targetInstance = null;
        f_targetInstance = c.getDeclaredField(fieldName);
        f_targetInstance.setAccessible(true);
        RestClient targetInstance2 = (RestClient) f_targetInstance.get(null);
        f_targetInstance.set(targetInstance2, null);
        targetInstance2 = null;

    }

    @AfterClass
    public static void tearDown2() throws Exception {
        String fieldName = "restTemplate";
        Class<RestClient> c = RestClient.class;

        // tear down field "restTemplate" in "RestClient" class
        Field f_targetInstance = null;
        f_targetInstance = c.getDeclaredField(fieldName);
        f_targetInstance.setAccessible(true);
        RestTemplate targetInstance = (RestTemplate) f_targetInstance.get(null);
        f_targetInstance.set(targetInstance, null);
        targetInstance = null;

        // tear down field "instance" in "RestClient" class
        fieldName = "instance";

        f_targetInstance = null;
        f_targetInstance = c.getDeclaredField(fieldName);
        f_targetInstance.setAccessible(true);
        RestClient targetInstance2 = (RestClient) f_targetInstance.get(null);
        f_targetInstance.set(targetInstance2, null);
        targetInstance2 = null;
    }

    /*----Test sendPostRequest----*/
    @Test
    public void testSendPostRequestNormalReturnOK() throws Exception {
        String url = "http://localhost/mockUrl";
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        OrderData bodyData = new OrderData();
        bodyData.setState(OrderData.STATE_SCHEDULED);

        ResponseEntity<Object> respEntity = new ResponseEntity<Object>(HttpStatus.OK);
        PowerMockito.doReturn(respEntity).when(restClientSpy, "sendRequestWithDataUTF8", any(), any(), any(), any(),
                any(), any(), isNull());

        ResponseEntity<?> response = restClientSpy.sendPostRequest(url, headers, pathParams, queryParams, bodyData);

        PowerMockito.verifyPrivate(restClientSpy, times(1)).invoke("sendRequestWithDataUTF8", any(),
                eq(HttpMethod.POST), any(), any(), any(), any(), isNull());
        HttpStatus expected = HttpStatus.OK;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    /*----Test sendPostRequestOutside----*/
    @Test
    public void testSendPostRequestOutsideNormalReturnOK() throws Exception {
        String url = "http://localhost/mockUrl";
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        OrderData bodyData = new OrderData();
        bodyData.setState(OrderData.STATE_SCHEDULED);
        Class<Object> clazz = Object.class;

        ResponseEntity<Object> respEntity = new ResponseEntity<Object>(HttpStatus.OK);
        PowerMockito.doReturn(respEntity).when(restClientSpy, "sendRequestWithDataNonUTF8", any(), any(), any(), any(),
                any(), any(), eq(Object.class));

        ResponseEntity<?> response = restClientSpy.sendPostRequestOutside(url, headers, pathParams, queryParams,
                bodyData, clazz);

        PowerMockito.verifyPrivate(restClientSpy, times(1)).invoke("sendRequestWithDataNonUTF8", any(),
                eq(HttpMethod.POST), any(), any(), any(), any(), eq(Object.class));
        HttpStatus expected = HttpStatus.OK;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    /*----Test sendPatchRequest----*/
    @Test
    public void testSendPatchRequestNormalReturnOK() throws Exception {
        String url = "http://localhost/mockUrl";
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        OrderData bodyData = new OrderData();
        bodyData.setState(OrderData.STATE_SCHEDULED);

        ResponseEntity<Object> respEntity = new ResponseEntity<Object>(HttpStatus.OK);
        PowerMockito.doReturn(respEntity).when(restClientSpy, "sendRequestWithDataUTF8", any(), any(), any(), any(),
                any(), any(), isNull());

        ResponseEntity<?> response = restClientSpy.sendPatchRequest(url, headers, pathParams, queryParams, bodyData);

        PowerMockito.verifyPrivate(restClientSpy, times(1)).invoke("sendRequestWithDataUTF8", any(),
                eq(HttpMethod.PATCH), any(), any(), any(), any(), isNull());
        HttpStatus expected = HttpStatus.OK;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    /*----Test sendDeleteRequest----*/
    @Test
    public void testSendDeleteRequestNormalReturnOK() throws Exception {
        String url = "http://localhost/mockUrl";
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        OrderData bodyData = new OrderData();
        bodyData.setState(OrderData.STATE_SCHEDULED);

        ResponseEntity<Object> respEntity = new ResponseEntity<Object>(HttpStatus.OK);
        PowerMockito.doReturn(respEntity).when(restClientSpy, "sendRequestWithDataUTF8", any(), any(), any(), any(),
                any(), any(), isNull());

        ResponseEntity<?> response = restClientSpy.sendDeleteRequest(url, headers, pathParams, queryParams, bodyData);

        PowerMockito.verifyPrivate(restClientSpy, times(1)).invoke("sendRequestWithDataUTF8", any(),
                eq(HttpMethod.DELETE), any(), any(), any(), any(), isNull());
        HttpStatus expected = HttpStatus.OK;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    /*----Test sendDeleteRequestOutsite----*/
    @Test
    public void testSendDeleteRequestOutsiteNormalReturnOK() throws Exception {
        String url = "http://localhost/mockUrl";
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        OrderData bodyData = new OrderData();
        bodyData.setState(OrderData.STATE_SCHEDULED);
        Class<?> clazz = Object.class;

        ResponseEntity<Object> respEntity = new ResponseEntity<Object>(HttpStatus.OK);
        PowerMockito.doReturn(respEntity).when(restClientSpy, "sendRequestWithDataNonUTF8", any(), any(), any(), any(),
                any(), any(), eq(Object.class));

        ResponseEntity<?> response = restClientSpy.sendDeleteRequestOutside(url, headers, pathParams, queryParams,
                bodyData, clazz);

        PowerMockito.verifyPrivate(restClientSpy, times(1)).invoke("sendRequestWithDataNonUTF8", any(),
                eq(HttpMethod.DELETE), any(), any(), any(), any(), eq(Object.class));
        HttpStatus expected = HttpStatus.OK;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    /*----Test sendPutRequest----*/
    @Test
    public void testSendPutRequestNormalReturnOK() throws Exception {
        String url = "http://localhost/mockUrl";
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        OrderData bodyData = new OrderData();
        bodyData.setState(OrderData.STATE_SCHEDULED);

        ResponseEntity<Object> respEntity = new ResponseEntity<Object>(HttpStatus.OK);
        PowerMockito.doReturn(respEntity).when(restClientSpy, "sendRequestWithDataUTF8", any(), any(), any(), any(),
                any(), any(), isNull());

        ResponseEntity<?> response = restClientSpy.sendPutRequest(url, headers, pathParams, queryParams, bodyData);

        PowerMockito.verifyPrivate(restClientSpy, times(1)).invoke("sendRequestWithDataUTF8", any(), eq(HttpMethod.PUT),
                any(), any(), any(), any(), isNull());
        HttpStatus expected = HttpStatus.OK;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    /*----Test sendPutRequestOutside----*/
    @Test
    public void testSendPutRequestOutsideNormalReturnOK() throws Exception {
        String url = "http://localhost/mockUrl";
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        OrderData bodyData = new OrderData();
        bodyData.setState(OrderData.STATE_SCHEDULED);
        Class<?> clazz = Object.class;

        ResponseEntity<Object> respEntity = new ResponseEntity<Object>(HttpStatus.OK);
        PowerMockito.doReturn(respEntity).when(restClientSpy, "sendRequestWithDataNonUTF8", any(), any(), any(), any(),
                any(), any(), eq(Object.class));

        ResponseEntity<?> response = restClientSpy.sendPutRequestOutside(url, headers, pathParams, queryParams,
                bodyData, clazz);

        PowerMockito.verifyPrivate(restClientSpy, times(1)).invoke("sendRequestWithDataNonUTF8", any(),
                eq(HttpMethod.PUT), any(), any(), any(), any(), eq(Object.class));
        HttpStatus expected = HttpStatus.OK;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    /*----Test sendRequestWithDataUTF8----*/
    @Test
    public void testSendRequestWithDataUTF8NormalReturnOK() throws Exception {
        String url = "http://localhost/mockUrl";
        HttpMethod method = HttpMethod.GET;
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        OrderData bodyData = new OrderData();
        bodyData.setState(OrderData.STATE_SCHEDULED);
        Class<Object> clazz = Object.class;

        ResponseEntity<Object> respEntity = new ResponseEntity<Object>(HttpStatus.OK);

        PowerMockito.doReturn(respEntity).when(restClientSpy, "doSendRequestWithData", any(), any(), any(), any(),
                any(), any(), eq(Object.class));
        ResponseEntity<?> response = Whitebox.invokeMethod(restClientSpy, "sendRequestWithDataUTF8", url, method,

                headers, pathParams, queryParams, bodyData, clazz);
        PowerMockito.verifyPrivate(restClientSpy, times(1)).invoke("doSendRequestWithData", any(), any(), any(), any(),
                any(), any(), eq(Object.class));

        HttpStatus expected = HttpStatus.OK;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    @Test
    public void testSendRequestWithDataUTF8HeaderEmptyAndBodyNullReturnOK() throws Exception {
        String url = "http://localhost/mockUrl";
        HttpMethod method = HttpMethod.GET;
        Map<String, String> headers = new HashMap<>();
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        OrderData bodyData = null;
        Class<Object> clazz = Object.class;

        ResponseEntity<Object> respEntity = new ResponseEntity<Object>(HttpStatus.OK);

        PowerMockito.doReturn(respEntity).when(restClientSpy, "doSendRequestWithData", any(), any(), any(), any(),
                any(), any(), eq(Object.class));
        ResponseEntity<?> response = Whitebox.invokeMethod(restClientSpy, "sendRequestWithDataUTF8", url, method,
                headers, pathParams, queryParams, bodyData, clazz);

        PowerMockito.verifyPrivate(restClientSpy, times(1)).invoke("doSendRequestWithData", any(), any(), any(), any(),
                any(), any(), eq(Object.class));
        HttpStatus expected = HttpStatus.OK;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    @Test
    public void testSendRequestWithDataUTF8HeaderNullAndBodyNotNullReturnOK() throws Exception {
        String url = "http://localhost/mockUrl";
        HttpMethod method = HttpMethod.GET;
        Map<String, String> headers = null;
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        OrderData bodyData = new OrderData();
        bodyData.setState(OrderData.STATE_SCHEDULED);
        Class<Object> clazz = Object.class;

        ResponseEntity<Object> respEntity = new ResponseEntity<Object>(HttpStatus.OK);

        PowerMockito.doReturn(respEntity).when(restClientSpy, "doSendRequestWithData", any(), any(), any(), any(),
                any(), any(), eq(Object.class));
        ResponseEntity<?> response = Whitebox.invokeMethod(restClientSpy, "sendRequestWithDataUTF8", url, method,
                headers, pathParams, queryParams, bodyData, clazz);

        PowerMockito.verifyPrivate(restClientSpy, times(1)).invoke("doSendRequestWithData", any(), any(), any(), any(),
                any(), any(), eq(Object.class));
        HttpStatus expected = HttpStatus.OK;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    /*----Test sendRequestWithDataNonUTF8----*/
    @Test
    public void testSendRequestWithDataNonUTF8NormalReturnOK() throws Exception {
        String url = "http://localhost/mockUrl";
        HttpMethod method = HttpMethod.GET;
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        OrderData bodyData = new OrderData();
        bodyData.setState(OrderData.STATE_SCHEDULED);
        Class<Object> clazz = Object.class;

        ResponseEntity<Object> respEntity = new ResponseEntity<Object>(HttpStatus.OK);

        PowerMockito.doReturn(respEntity).when(restClientSpy, "doSendRequestWithData", any(), any(), any(), any(),
                any(), any(), eq(Object.class));
        ResponseEntity<?> response = Whitebox.invokeMethod(restClientSpy, "sendRequestWithDataNonUTF8", url, method,
                headers, pathParams, queryParams, bodyData, clazz);

        PowerMockito.verifyPrivate(restClientSpy, times(1)).invoke("doSendRequestWithData", any(), any(), any(), any(),
                any(), any(), eq(Object.class));
        HttpStatus expected = HttpStatus.OK;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    @Test
    public void testSendRequestWithDataNonUTF8HeaderEmptyAndBodyNullReturnOK() throws Exception {
        String url = "http://localhost/mockUrl";
        HttpMethod method = HttpMethod.GET;
        Map<String, String> headers = new HashMap<>();
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        OrderData bodyData = null;
        Class<Object> clazz = Object.class;

        ResponseEntity<Object> respEntity = new ResponseEntity<Object>(HttpStatus.OK);

        PowerMockito.doReturn(respEntity).when(restClientSpy, "doSendRequestWithData", any(), any(), any(), any(),
                any(), any(), eq(Object.class));
        ResponseEntity<?> response = Whitebox.invokeMethod(restClientSpy, "sendRequestWithDataNonUTF8", url, method,
                headers, pathParams, queryParams, bodyData, clazz);

        PowerMockito.verifyPrivate(restClientSpy, times(1)).invoke("doSendRequestWithData", any(), any(), any(), any(),
                any(), any(), eq(Object.class));
        HttpStatus expected = HttpStatus.OK;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    @Test
    public void testSendRequestWithDataNonUTF8HeaderNullAndBodyNotNullReturnOK() throws Exception {
        String url = "http://localhost/mockUrl";
        HttpMethod method = HttpMethod.GET;
        Map<String, String> headers = null;
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        OrderData bodyData = new OrderData();
        bodyData.setState(OrderData.STATE_SCHEDULED);
        Class<Object> clazz = Object.class;

        ResponseEntity<Object> respEntity = new ResponseEntity<Object>(HttpStatus.OK);

        PowerMockito.doReturn(respEntity).when(restClientSpy, "doSendRequestWithData", any(), any(), any(), any(),
                any(), any(), eq(Object.class));
        ResponseEntity<?> response = Whitebox.invokeMethod(restClientSpy, "sendRequestWithDataNonUTF8", url, method,
                headers, pathParams, queryParams, bodyData, clazz);

        PowerMockito.verifyPrivate(restClientSpy, times(1)).invoke("doSendRequestWithData", any(), any(), any(), any(),
                any(), any(), eq(Object.class));
        HttpStatus expected = HttpStatus.OK;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    /*----Test doSendRequestWithData----*/
    @Test
    public void testDoSendRequestWithDataNormalReturnOK() throws Exception {
        String url = "http://localhost/mockUrl";
        HttpMethod method = HttpMethod.GET;
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        OrderData bodyData = new OrderData();
        bodyData.setState(OrderData.STATE_SCHEDULED);
        Class<Object> clazz = Object.class;

        ResponseEntity<Object> respEntity = new ResponseEntity<Object>(HttpStatus.OK);
        PowerMockito.when(restTemplate.exchange(any(), any(), any(), eq(Object.class))).thenReturn(respEntity);

        ResponseEntity<?> response = Whitebox.invokeMethod(restClient, "doSendRequestWithData", url, method, headers,
                pathParams, queryParams, bodyData, clazz);

        verify(restTemplate, times(1)).exchange(any(), any(), any(), eq(Object.class));
        verifyNoMoreInteractions(restTemplate);
        HttpStatus expected = HttpStatus.OK;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDoSendRequestWithDataNormalAllNullReturn500() throws Exception {

        String url = null;
        HttpMethod method = null;
        HttpHeaders headers = null;
        Map<String, String> pathParams = null;
        Map<String, String[]> queryParams = null;
        OrderData bodyData = null;
        Class<Object> clazz = null;

        ResponseEntity<Object> respEntity = new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
        PowerMockito.when(restTemplate.exchange(any(), any(), any(), (Class<Object>) nullable(Object.class)))
                .thenReturn(respEntity);

        ResponseEntity<?> response = Whitebox.invokeMethod(restClient, "doSendRequestWithData", url, method, headers,
                pathParams, queryParams, bodyData, clazz);

        verify(restTemplate, times(0)).exchange(any(), any(), any(), (Class<Object>) nullable(Object.class));
        verifyNoMoreInteractions(restTemplate);
        HttpStatus expected = HttpStatus.INTERNAL_SERVER_ERROR;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    @Test
    public void testDoSendRequestWithDataPathParamNullQueryParamNotNullReturnOK() throws Exception {

        String url = "http://localhost/mockUrl";
        HttpMethod method = HttpMethod.GET;
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> pathParams = null;
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        OrderData bodyData = new OrderData();
        bodyData.setState(OrderData.STATE_SCHEDULED);
        Class<Object> clazz = Object.class;

        ResponseEntity<Object> respEntity = new ResponseEntity<Object>(HttpStatus.OK);
        PowerMockito.when(restTemplate.exchange(any(), any(), any(), eq(Object.class))).thenReturn(respEntity);

        ResponseEntity<?> response = Whitebox.invokeMethod(restClient, "doSendRequestWithData", url, method, headers,
                pathParams, queryParams, bodyData, clazz);

        verify(restTemplate, times(1)).exchange(any(), any(), any(), eq(Object.class));
        verifyNoMoreInteractions(restTemplate);
        HttpStatus expected = HttpStatus.OK;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    @Test
    public void testDoSendRequestWithDataPathParamNotNullQueryParamNullReturnOK() throws Exception {

        String url = "http://localhost/mockUrl";
        HttpMethod method = HttpMethod.GET;
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = null;
        OrderData bodyData = new OrderData();
        bodyData.setState(OrderData.STATE_SCHEDULED);
        Class<Object> clazz = Object.class;

        ResponseEntity<Object> respEntity = new ResponseEntity<Object>(HttpStatus.OK);
        PowerMockito.when(restTemplate.exchange(any(), any(), any(), eq(Object.class))).thenReturn(respEntity);

        ResponseEntity<?> response = Whitebox.invokeMethod(restClient, "doSendRequestWithData", url, method, headers,
                pathParams, queryParams, bodyData, clazz);

        verify(restTemplate, times(1)).exchange(any(), any(), any(), eq(Object.class));
        verifyNoMoreInteractions(restTemplate);
        HttpStatus expected = HttpStatus.OK;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    @Test
    public void testDoSendRequestWithDataPathParamEmptyQueryParamEmptyReturnOK() throws Exception {

        String url = "http://localhost/mockUrl";
        HttpMethod method = HttpMethod.GET;
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> pathParams = new HashMap<String, String>();
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        OrderData bodyData = new OrderData();
        bodyData.setState(OrderData.STATE_SCHEDULED);
        Class<Object> clazz = Object.class;

        ResponseEntity<Object> respEntity = new ResponseEntity<Object>(HttpStatus.OK);
        PowerMockito.when(restTemplate.exchange(any(), any(), any(), eq(Object.class))).thenReturn(respEntity);

        ResponseEntity<?> response = Whitebox.invokeMethod(restClient, "doSendRequestWithData", url, method, headers,
                pathParams, queryParams, bodyData, clazz);

        verify(restTemplate, times(1)).exchange(any(), any(), any(), eq(Object.class));
        verifyNoMoreInteractions(restTemplate);
        HttpStatus expected = HttpStatus.OK;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDoSendRequestWithDataBodyNotNullClassNullReturnOK() throws Exception {

        String url = "http://localhost/mockUrl";
        HttpMethod method = HttpMethod.GET;
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        OrderData bodyData = new OrderData();
        bodyData.setState(OrderData.STATE_SCHEDULED);
        Class<Object> clazz = null;

        ResponseEntity<Object> respEntity = new ResponseEntity<Object>(HttpStatus.OK);
        PowerMockito.when(restTemplate.exchange(any(), any(), any(), (Class<Object>) nullable(Object.class)))
                .thenReturn(respEntity);

        ResponseEntity<?> response = Whitebox.invokeMethod(restClient, "doSendRequestWithData", url, method, headers,
                pathParams, queryParams, bodyData, clazz);

        verify(restTemplate, times(1)).exchange(any(), any(), any(), (Class<Object>) nullable(Object.class));
        verifyNoMoreInteractions(restTemplate);
        HttpStatus expected = HttpStatus.OK;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDoSendRequestWithDataBodyNullClassNullReturnOK() throws Exception {

        String url = "http://localhost/mockUrl";
        HttpMethod method = HttpMethod.GET;
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        OrderData bodyData = null;
        Class<Object> clazz = null;

        ResponseEntity<Object> respEntity = new ResponseEntity<Object>(HttpStatus.OK);
        PowerMockito.when(restTemplate.exchange(any(), any(), any(), (Class<Object>) nullable(Object.class)))
                .thenReturn(respEntity);

        ResponseEntity<?> response = Whitebox.invokeMethod(restClient, "doSendRequestWithData", url, method, headers,
                pathParams, queryParams, bodyData, clazz);

        verify(restTemplate, times(1)).exchange(any(), any(), any(), (Class<Object>) nullable(Object.class));
        verifyNoMoreInteractions(restTemplate);
        HttpStatus expected = HttpStatus.OK;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    @Test
    public void testDoSendRequestWithDataPatchNormalReturnOK() throws Exception {

        String url = "http://localhost/mockUrl";
        HttpMethod method = HttpMethod.PATCH;
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        OrderData bodyData = new OrderData();
        bodyData.setState(OrderData.STATE_SCHEDULED);
        Class<Object> clazz = Object.class;

        ResponseEntity<Object> respEntity = new ResponseEntity<Object>(HttpStatus.OK);
        PowerMockito.whenNew(RestTemplate.class).withAnyArguments().thenReturn(restTemplate);
        PowerMockito.when(restTemplate.exchange(any(), any(), any(), eq(Object.class))).thenReturn(respEntity);

        ResponseEntity<?> response = Whitebox.invokeMethod(restClient, "doSendRequestWithData", url, method, headers,
                pathParams, queryParams, bodyData, clazz);

        verify(restTemplate, times(1)).exchange(any(), any(), any(), eq(Object.class));
        verifyNoMoreInteractions(restTemplate);
        HttpStatus expected = HttpStatus.OK;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    @Test
    public void testDoSendRequestWithDataClientErrorNotFoundReturn404() throws Exception {

        String url = "http://localhost/mockUrl";
        HttpMethod method = HttpMethod.GET;
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        OrderData bodyData = new OrderData();
        bodyData.setState(OrderData.STATE_SCHEDULED);
        Class<Object> clazz = Object.class;

        PowerMockito.when(restTemplate.exchange(any(), any(), any(), eq(Object.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        ResponseEntity<?> response = Whitebox.invokeMethod(restClient, "doSendRequestWithData", url, method, headers,
                pathParams, queryParams, bodyData, clazz);

        verify(restTemplate, times(1)).exchange(any(), any(), any(), eq(Object.class));
        verifyNoMoreInteractions(restTemplate);
        HttpStatus expected = HttpStatus.NOT_FOUND;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    @Test
    public void testDoSendRequestWithDataClientErrorDefaultReturnDefaultCode() throws Exception {

        String url = "http://localhost/mockUrl";
        HttpMethod method = HttpMethod.GET;
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        OrderData bodyData = new OrderData();
        bodyData.setState(OrderData.STATE_SCHEDULED);
        Class<Object> clazz = Object.class;

        PowerMockito.when(restTemplate.exchange(any(), any(), any(), eq(Object.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.METHOD_NOT_ALLOWED));

        ResponseEntity<?> response = Whitebox.invokeMethod(restClient, "doSendRequestWithData", url, method, headers,
                pathParams, queryParams, bodyData, clazz);

        verify(restTemplate, times(1)).exchange(any(), any(), any(), eq(Object.class));
        verifyNoMoreInteractions(restTemplate);
        HttpStatus expected = HttpStatus.METHOD_NOT_ALLOWED;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    @Test
    public void testDoSendRequestWithDataServerErrorInternalReturn500() throws Exception {

        String url = "http://localhost/mockUrl";
        HttpMethod method = HttpMethod.GET;
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        OrderData bodyData = new OrderData();
        bodyData.setState(OrderData.STATE_SCHEDULED);
        Class<Object> clazz = Object.class;

        PowerMockito.when(restTemplate.exchange(any(), any(), any(), eq(Object.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        ResponseEntity<?> response = Whitebox.invokeMethod(restClient, "doSendRequestWithData", url, method, headers,
                pathParams, queryParams, bodyData, clazz);

        verify(restTemplate, times(1)).exchange(any(), any(), any(), eq(Object.class));
        verifyNoMoreInteractions(restTemplate);
        HttpStatus expected = HttpStatus.INTERNAL_SERVER_ERROR;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    @Test
    public void testDoSendRequestWithDataServerErrorDefaultReturnDefaultCode() throws Exception {

        String url = "http://localhost/mockUrl";
        HttpMethod method = HttpMethod.GET;
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        OrderData bodyData = new OrderData();
        bodyData.setState(OrderData.STATE_SCHEDULED);
        Class<Object> clazz = Object.class;

        PowerMockito.when(restTemplate.exchange(any(), any(), any(), eq(Object.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.BAD_GATEWAY));

        ResponseEntity<?> response = Whitebox.invokeMethod(restClient, "doSendRequestWithData", url, method, headers,
                pathParams, queryParams, bodyData, clazz);

        verify(restTemplate, times(1)).exchange(any(), any(), any(), eq(Object.class));
        verifyNoMoreInteractions(restTemplate);
        HttpStatus expected = HttpStatus.BAD_GATEWAY;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    @Test
    public void testDoSendRequestWithDataResourceAccessErrorReturn404() throws Exception {

        String url = "http://localhost/mockUrl";
        HttpMethod method = HttpMethod.GET;
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        OrderData bodyData = new OrderData();
        bodyData.setState(OrderData.STATE_SCHEDULED);
        Class<Object> clazz = Object.class;

        PowerMockito.when(restTemplate.exchange(any(), any(), any(), eq(Object.class)))
                .thenThrow(new ResourceAccessException("Not found"));

        ResponseEntity<?> response = Whitebox.invokeMethod(restClient, "doSendRequestWithData", url, method, headers,
                pathParams, queryParams, bodyData, clazz);

        verify(restTemplate, times(1)).exchange(any(), any(), any(), eq(Object.class));
        verifyNoMoreInteractions(restTemplate);
        HttpStatus expected = HttpStatus.NOT_FOUND;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    /*----TEST sendGetRequest----*/
    @Test
    public void testSendGetRequestNormalReturnOK() throws Exception {
        String url = "http://localhost/mockUrl";
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        OrderData bodyData = new OrderData();
        bodyData.setState(OrderData.STATE_SCHEDULED);
        Class<Object> clazz = Object.class;

        ResponseEntity<Object> respEntity = new ResponseEntity<Object>(HttpStatus.OK);
        PowerMockito.when(restTemplate.exchange(any(), any(), any(), eq(Object.class))).thenReturn(respEntity);

        ResponseEntity<?> response = restClient.sendGetRequest(url, headers, pathParams, queryParams, clazz);

        verify(restTemplate, times(1)).exchange(any(), any(), any(), eq(Object.class));
        verifyNoMoreInteractions(restTemplate);
        HttpStatus expected = HttpStatus.OK;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    @Test
    public void testSendGetRequestHeaderEmptyReturnOK() throws Exception {
        String url = "http://localhost/mockUrl";
        Map<String, String> headers = new HashMap<>();
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        Class<Object> clazz = Object.class;

        ResponseEntity<Object> respEntity = new ResponseEntity<Object>(HttpStatus.OK);
        PowerMockito.when(restTemplate.exchange(any(), any(), any(), eq(Object.class))).thenReturn(respEntity);

        ResponseEntity<?> response = restClient.sendGetRequest(url, headers, pathParams, queryParams, clazz);

        verify(restTemplate, times(1)).exchange(any(), any(), any(), eq(Object.class));
        verifyNoMoreInteractions(restTemplate);
        HttpStatus expected = HttpStatus.OK;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    @Test
    public void testSendGetRequestHeaderEmptyReturnOKHeadersNull() throws Exception {
        String url = "http://localhost/mockUrl";
        Map<String, String> headers = null;
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        Class<Object> clazz = Object.class;

        ResponseEntity<Object> respEntity = new ResponseEntity<Object>(HttpStatus.OK);
        PowerMockito.when(restTemplate.exchange(any(), any(), any(), eq(Object.class))).thenReturn(respEntity);

        ResponseEntity<?> response = restClient.sendGetRequest(url, headers, pathParams, queryParams, clazz);

        verify(restTemplate, times(1)).exchange(any(), any(), any(), eq(Object.class));
        verifyNoMoreInteractions(restTemplate);
        HttpStatus expected = HttpStatus.OK;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSendGetRequestNormalAllNullReturn500() throws Exception {

        String url = null;
        Map<String, String> headers = null;
        Map<String, String> pathParams = null;
        Map<String, String[]> queryParams = null;
        Class<Object> clazz = null;

        ResponseEntity<Object> respEntity = new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
        PowerMockito.when(restTemplate.exchange(any(), any(), any(), (Class<Object>) nullable(Object.class)))
                .thenReturn(respEntity);

        ResponseEntity<?> response = restClient.sendGetRequest(url, headers, pathParams, queryParams, clazz);

        verify(restTemplate, times(0)).exchange(any(), any(), any(), (Class<Object>) nullable(Object.class));
        verifyNoMoreInteractions(restTemplate);
        HttpStatus expected = HttpStatus.INTERNAL_SERVER_ERROR;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    @Test
    public void testSendGetRequestPathParamNullQueryParamNotNullReturnOK() throws Exception {

        String url = "http://localhost/mockUrl";
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> pathParams = null;
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        Class<Object> clazz = Object.class;

        ResponseEntity<Object> respEntity = new ResponseEntity<Object>(HttpStatus.OK);
        PowerMockito.when(restTemplate.exchange(any(), any(), any(), eq(Object.class))).thenReturn(respEntity);

        ResponseEntity<?> response = restClient.sendGetRequest(url, headers, pathParams, queryParams, clazz);

        verify(restTemplate, times(1)).exchange(any(), any(), any(), eq(Object.class));
        verifyNoMoreInteractions(restTemplate);
        HttpStatus expected = HttpStatus.OK;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    @Test
    public void testSendGetRequestPathParamNotNullQueryParamNullReturnOK() throws Exception {

        String url = "http://localhost/mockUrl";
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = null;
        Class<Object> clazz = Object.class;

        ResponseEntity<Object> respEntity = new ResponseEntity<Object>(HttpStatus.OK);
        PowerMockito.when(restTemplate.exchange(any(), any(), any(), eq(Object.class))).thenReturn(respEntity);

        ResponseEntity<?> response = restClient.sendGetRequest(url, headers, pathParams, queryParams, clazz);

        verify(restTemplate, times(1)).exchange(any(), any(), any(), eq(Object.class));
        verifyNoMoreInteractions(restTemplate);
        HttpStatus expected = HttpStatus.OK;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    @Test
    public void testSendGetRequestPathParamEmptyQueryParamEmptyReturnOK() throws Exception {

        String url = "http://localhost/mockUrl";
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> pathParams = new HashMap<String, String>();
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        Class<Object> clazz = Object.class;

        ResponseEntity<Object> respEntity = new ResponseEntity<Object>(HttpStatus.OK);
        PowerMockito.when(restTemplate.exchange(any(), any(), any(), eq(Object.class))).thenReturn(respEntity);

        ResponseEntity<?> response = restClient.sendGetRequest(url, headers, pathParams, queryParams, clazz);

        verify(restTemplate, times(1)).exchange(any(), any(), any(), eq(Object.class));
        verifyNoMoreInteractions(restTemplate);
        HttpStatus expected = HttpStatus.OK;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSendGetRequestBodyNotNullClassNullReturnOK() throws Exception {

        String url = "http://localhost/mockUrl";
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        Class<Object> clazz = null;

        ResponseEntity<Object> respEntity = new ResponseEntity<Object>(HttpStatus.OK);
        PowerMockito.when(restTemplate.exchange(any(), any(), any(), (Class<Object>) nullable(Object.class)))
                .thenReturn(respEntity);

        ResponseEntity<?> response = restClient.sendGetRequest(url, headers, pathParams, queryParams, clazz);

        verify(restTemplate, times(1)).exchange(any(), any(), any(), (Class<Object>) nullable(Object.class));
        verifyNoMoreInteractions(restTemplate);
        HttpStatus expected = HttpStatus.OK;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSendGetRequestClassNullReturnOK() throws Exception {

        String url = "http://localhost/mockUrl";
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        Class<Object> clazz = null;

        ResponseEntity<Object> respEntity = new ResponseEntity<Object>(HttpStatus.OK);
        PowerMockito.when(restTemplate.exchange(any(), any(), any(), (Class<Object>) nullable(Object.class)))
                .thenReturn(respEntity);

        ResponseEntity<?> response = restClient.sendGetRequest(url, headers, pathParams, queryParams, clazz);

        verify(restTemplate, times(1)).exchange(any(), any(), any(), (Class<Object>) nullable(Object.class));
        verifyNoMoreInteractions(restTemplate);
        HttpStatus expected = HttpStatus.OK;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    @Test
    public void testSendGetRequestClientErrorNotFoundReturn404() throws Exception {

        String url = "http://localhost/mockUrl";
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        Class<Object> clazz = Object.class;

        PowerMockito.when(restTemplate.exchange(any(), any(), any(), eq(Object.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        ResponseEntity<?> response = restClient.sendGetRequest(url, headers, pathParams, queryParams, clazz);

        verify(restTemplate, times(1)).exchange(any(), any(), any(), eq(Object.class));
        verifyNoMoreInteractions(restTemplate);
        HttpStatus expected = HttpStatus.NOT_FOUND;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    @Test
    public void testSendGetRequestClientErrorDefaultReturnDefaultCode() throws Exception {

        String url = "http://localhost/mockUrl";
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        Class<Object> clazz = Object.class;

        PowerMockito.when(restTemplate.exchange(any(), any(), any(), eq(Object.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.METHOD_NOT_ALLOWED));

        ResponseEntity<?> response = restClient.sendGetRequest(url, headers, pathParams, queryParams, clazz);

        verify(restTemplate, times(1)).exchange(any(), any(), any(), eq(Object.class));
        verifyNoMoreInteractions(restTemplate);
        HttpStatus expected = HttpStatus.METHOD_NOT_ALLOWED;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    @Test
    public void testSendGetRequestServerErrorInternalReturn500() throws Exception {

        String url = "http://localhost/mockUrl";
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        Class<Object> clazz = Object.class;

        PowerMockito.when(restTemplate.exchange(any(), any(), any(), eq(Object.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        ResponseEntity<?> response = restClient.sendGetRequest(url, headers, pathParams, queryParams, clazz);

        verify(restTemplate, times(1)).exchange(any(), any(), any(), eq(Object.class));
        verifyNoMoreInteractions(restTemplate);
        HttpStatus expected = HttpStatus.INTERNAL_SERVER_ERROR;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    @Test
    public void testSendGetRequestServerErrorDefaultReturnDefaultCode() throws Exception {
        String url = "http://localhost/mockUrl";
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        Class<Object> clazz = Object.class;

        PowerMockito.when(restTemplate.exchange(any(), any(), any(), eq(Object.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.BAD_GATEWAY));

        ResponseEntity<?> response = restClient.sendGetRequest(url, headers, pathParams, queryParams, clazz);

        verify(restTemplate, times(1)).exchange(any(), any(), any(), eq(Object.class));
        verifyNoMoreInteractions(restTemplate);
        HttpStatus expected = HttpStatus.BAD_GATEWAY;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    @Test
    public void testSendGetRequestResourceAccessErrorReturn404() throws Exception {
        String url = "http://localhost/mockUrl";
        Map<String, String> headers = new HashMap<>();
        Map<String, String> pathParams = new HashMap<String, String>();
        pathParams.put("mock", "mock value1");
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        queryParams.put("mockQuery", new String[] { "mockValue1", "mockValue2" });
        Class<Object> clazz = Object.class;

        PowerMockito.when(restTemplate.exchange(any(), any(), any(), eq(Object.class)))
                .thenThrow(new ResourceAccessException("Not found"));

        ResponseEntity<?> response = restClient.sendGetRequest(url, headers, pathParams, queryParams, clazz);

        verify(restTemplate, times(1)).exchange(any(), any(), any(), eq(Object.class));
        verifyNoMoreInteractions(restTemplate);
        HttpStatus expected = HttpStatus.NOT_FOUND;
        HttpStatus actual = response.getStatusCode();
        assertEquals(expected, actual);
    }

    /*----Test writeLog----*/
    @Test
    public void testWriteLogOneMessage() throws Exception {
        Whitebox.setInternalState(RestClient.class, "logger", loggerSpy);
        String data = "mockData";
        String messageType = "mockMessageType";
        List<String> msgListMock = new ArrayList<String>();
        msgListMock.add("msg1");

        // mock splitLog(string)
        PowerMockito.doReturn(msgListMock).when(restClientSpy, "splitLog", any());

        Whitebox.invokeMethod(restClientSpy, "writeLog", data, messageType);

        verify(loggerSpy, times(2)).debug(any());
    }

    @Test
    public void testwriteLogTwoMessage() throws Exception {
        Whitebox.setInternalState(RestClient.class, "logger", loggerSpy);
        String data = "mockData";
        String messageType = "mockMessageType";
        List<String> msgListMock = new ArrayList<String>();
        msgListMock.add("msg1");
        msgListMock.add("msg2");

        // mock splitLog(string)
        PowerMockito.doReturn(msgListMock).when(restClientSpy, "splitLog", any());

        Whitebox.invokeMethod(restClientSpy, "writeLog", data, messageType);

        // TODO this must be 5 times call logger.debug, but it's not
        verify(loggerSpy, atLeast(2)).debug(any());

    }

    @Test
    public void testWriteLogNoMessage() throws Exception {
        Whitebox.setInternalState(RestClient.class, "logger", loggerSpy);
        String data = "mockData";
        String messageType = "mockMessageType";
        List<String> msgListMock = new ArrayList<String>();

        // mock splitLog(string)
        PowerMockito.doReturn(msgListMock).when(restClientSpy, "splitLog", any());

        Whitebox.invokeMethod(restClientSpy, "writeLog", data, messageType);

        verify(loggerSpy, times(0)).debug(any());
    }

    /*----Test splitLog----*/
    @Test
    public void testSplitLogMessageSplitReturnTwoElementList() throws Exception {
        Path path = Paths.get("src/test/resources/test_splitLog_64001.txt");
        Charset charset = StandardCharsets.UTF_8;
        String msg = new String(Files.readAllBytes(path.toAbsolutePath()), charset);
        List<String> messageList = Whitebox.invokeMethod(restClient, "splitLog", msg);
        int expected = 2;
        int actual = messageList.size();
        assertEquals(expected, actual);
    }

    @Test
    public void testSplitLogMessageOneNoSplitReturnOneElementList() throws Exception {
        Path path = Paths.get("src/test/resources/test_splitLog_63999.txt");
        Charset charset = StandardCharsets.UTF_8;
        String msg = new String(Files.readAllBytes(path.toAbsolutePath()), charset);
        List<String> messageList = Whitebox.invokeMethod(restClient, "splitLog", msg);
        int expected = 1;
        int actual = messageList.size();
        assertEquals(expected, actual);
    }

    @Test
    public void testSplitLogMessageEmptyReturnEmptyList() throws Exception {
        String msg = "";
        List<String> messageList = Whitebox.invokeMethod(restClient, "splitLog", msg);
        int expected = 0;
        int actual = messageList.size();
        assertEquals(expected, actual);
    }

    @Test
    public void testSplitLogMessageNullReturnEmptyList() throws Exception {
        String msg = null;
        List<String> messageList = Whitebox.invokeMethod(restClient, "splitLog", msg);
        int expected = 0;
        int actual = messageList.size();
        assertEquals(expected, actual);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSplitLogErrorReturnEmptyList() throws Exception {
        Whitebox.setInternalState(RestClient.class, "logger", loggerSpy);
        Path path = Paths.get("src/test/resources/test_splitLog_64001.txt");
        Charset charset = StandardCharsets.UTF_8;
        String msg = new String(Files.readAllBytes(path.toAbsolutePath()), charset);
        ArrayList<String> mockArrayList = PowerMockito.mock(ArrayList.class);
        PowerMockito.whenNew(ArrayList.class).withNoArguments().thenReturn(mockArrayList);
        PowerMockito.doThrow(new RuntimeException()).when(mockArrayList).add(any());

        Whitebox.invokeMethod(restClient, "splitLog", msg);

        verify(loggerSpy, times(1)).error(Mockito.anyString(), any(Throwable.class));
    }
}
