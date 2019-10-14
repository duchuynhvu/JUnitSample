/*
 * Demo project
 */
package com.tmavn.sample.common;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * The Class RestClient.
 */
public class RestClient {

    /** The Constant logger. */
    final private static Logger logger = LoggerFactory.getLogger(RestClient.class);

    /** The rest template. */
    private static RestTemplate restTemplate;

    /** The instance. */
    private static RestClient instance = null;

    static {
        restTemplate = new RestTemplate();
    }

    /**
     * Instantiates a new rest client.
     */
    private RestClient() {
        // Exists only to defeat instantiation.

    }

    /**
     * Gets the single instance of RestClient.
     *
     * @return single instance of RestClient
     */
    public static synchronized RestClient getInstance() {
        logger.debug("getInstance-start");
        if (instance == null) {
            logger.debug("brand-1");
            synchronized (RestClient.class) {
                if (instance == null) {
                    logger.debug("brand-2");
                    instance = new RestClient();
                }
            }
        }
        logger.debug("getInstance-end");
        return instance;
    }

    /**
     * Call GET HTTP.
     *
     * @param uri         the uri
     * @param headers     the headers
     * @param pathParams  the path params
     * @param queryParams the query params
     * @param T           the t
     * @return the response entity
     */
    public ResponseEntity<?> sendGetRequest(String uri, Map<String, String> headers, Map<String, ?> pathParams,
            Map<String, String[]> queryParams, Class<?> T) {
        logger.debug("IN - sendGetRequest()");
        if (T == null) {
            T = Void.class;
        }

        if (pathParams != null && !pathParams.isEmpty()) {
            for (Map.Entry<String, ?> entry : pathParams.entrySet()) {
                uri += "{" + entry.getKey() + "}";
            }
        }

        ResponseEntity<?> respResult = new ResponseEntity<String>(HttpStatus.OK);

        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri);
            if (queryParams != null && !queryParams.isEmpty()) {
                for (Object key : queryParams.keySet()) {
                    String keyStr = (String) key;
                    String[] valueArr = queryParams.get(keyStr);
                    for (String value : valueArr) {
                        builder = builder.queryParam(keyStr, value);
                    }
                }
            }

            HttpHeaders hd = new HttpHeaders();
            String UserID = "";
            String moduleID = "";
            if (headers != null && !headers.isEmpty()) {
                hd.setAll(headers);
                UserID = (headers.getOrDefault(Constant.HEADER_USER_ID, ""));
                moduleID = (headers.getOrDefault(Constant.HEADER_MODULE_ID, ""));
            }

            HttpEntity<?> entity = new HttpEntity<>(hd);

            URI sendUri;
            if (pathParams != null && !pathParams.isEmpty()) {
                sendUri = builder.buildAndExpand(pathParams).encode().toUri();
            } else {
                sendUri = builder.build().encode().toUri();
            }
            logger.debug("sendGetRequest with URI: {}", sendUri);
            logger.debug("Header info: UserID = {}, moduleID = {}", UserID, moduleID);
            logger.debug("Request header (full): {}", hd);

            respResult = restTemplate.exchange(sendUri, HttpMethod.GET, entity, T);

            logger.debug("Response status: {}", respResult.getStatusCode());
            writeLog(Utils.parseObjectToJson(respResult.getBody()), "Response body");
            logger.debug("OUT - sendGetRequest()");
            return respResult;
        } catch (HttpClientErrorException e) {
            // Handle to return error message to sender
            switch (HttpStatus.valueOf(e.getRawStatusCode())) {
            case BAD_REQUEST:
            case NOT_FOUND:
            case CONFLICT:
                logger.warn(e.getResponseBodyAsString());
                respResult = ResponseEntity.status(HttpStatus.valueOf(e.getRawStatusCode()))
                        .body(e.getResponseBodyAsString());
                break;
            default:
                logger.warn(e.getMessage());
                respResult = ResponseEntity.status(HttpStatus.valueOf(e.getRawStatusCode())).body(e.getMessage());
            }
        } catch (HttpServerErrorException e) {
            // Handle to return error message to sender
            switch (HttpStatus.valueOf(e.getRawStatusCode())) {
            case INTERNAL_SERVER_ERROR:
                logger.warn(e.getResponseBodyAsString());
                respResult = ResponseEntity.status(HttpStatus.valueOf(e.getRawStatusCode()))
                        .body(e.getResponseBodyAsString());
                break;
            default:
                logger.warn(e.getMessage());
                respResult = ResponseEntity.status(HttpStatus.valueOf(e.getRawStatusCode())).body(e.getMessage());
            }
        } catch (ResourceAccessException e) {
            logger.error("Exception: ", e);
            respResult = ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Exception: ", e);
            respResult = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return respResult;
    }

    /**
     * Call POST HTTP.
     *
     * @param uri         the uri
     * @param headers     the headers
     * @param params      the params
     * @param queryParams the query params
     * @param bodyData    the body data
     * @return the response entity
     */
    public ResponseEntity<?> sendPostRequest(String uri, Map<String, String> headers, Map<String, String> params,
            Map<String, String[]> queryParams, Object bodyData) {
        return sendRequestWithDataUTF8(uri, HttpMethod.POST, headers, params, queryParams, bodyData, null);
    }

    /**
     * Send post request outside.
     *
     * @param uri         the uri
     * @param headers     the headers
     * @param params      the params
     * @param queryParams the query params
     * @param bodyData    the body data
     * @param T           the class of response object
     * @return the response entity
     */
    public ResponseEntity<?> sendPostRequestOutside(String uri, Map<String, String> headers, Map<String, String> params,
            Map<String, String[]> queryParams, Object bodyData, Class<?> T) {
        return sendRequestWithDataNonUTF8(uri, HttpMethod.POST, headers, params, queryParams, bodyData, T);
    }

    /**
     * Send put request outside.
     *
     * @param uri         the uri
     * @param headers     the headers
     * @param params      the params
     * @param queryParams the query params
     * @param bodyData    the body data
     * @param T           the class of response object
     * @return the response entity
     */
    public ResponseEntity<?> sendPutRequestOutside(String uri, Map<String, String> headers, Map<String, String> params,
            Map<String, String[]> queryParams, Object bodyData, Class<?> T) {
        return sendRequestWithDataNonUTF8(uri, HttpMethod.PUT, headers, params, queryParams, bodyData, T);
    }

    /**
     * Send DELETE request outside.
     *
     * @param uri         the uri
     * @param headers     the headers
     * @param params      the params
     * @param queryParams the query params
     * @param bodyData    the body data
     * @param T           the class of response object
     * @return the response entity
     */
    public ResponseEntity<?> sendDeleteRequestOutside(String uri, Map<String, String> headers,
            Map<String, String> params, Map<String, String[]> queryParams, Object bodyData, Class<?> T) {
        return sendRequestWithDataNonUTF8(uri, HttpMethod.DELETE, headers, params, queryParams, bodyData, T);
    }

    /**
     * Call PUT HTTP.
     *
     * @param uri         the uri
     * @param headers     the headers
     * @param params      the params
     * @param queryParams the query params
     * @param bodyData    the body data
     * @return the response entity
     */
    public ResponseEntity<?> sendPutRequest(String uri, Map<String, String> headers, Map<String, String> params,
            Map<String, String[]> queryParams, Object bodyData) {
        return sendRequestWithDataUTF8(uri, HttpMethod.PUT, headers, params, queryParams, bodyData, null);
    }

    /**
     * Call DELETE HTTP.
     *
     * @param uri         the uri
     * @param headers     the headers
     * @param params      the params
     * @param queryParams the query params
     * @param bodyData    the body data
     * @return the response entity
     */
    public ResponseEntity<?> sendDeleteRequest(String uri, Map<String, String> headers, Map<String, String> params,
            Map<String, String[]> queryParams, Object bodyData) {
        return sendRequestWithDataUTF8(uri, HttpMethod.DELETE, headers, params, queryParams, bodyData, null);
    }

    /**
     * Call PATCH HTTP.
     *
     * @param uri         the uri
     * @param headers     the headers
     * @param params      the params
     * @param queryParams the query params
     * @param bodyData    the body data
     * @return the response entity
     */
    public ResponseEntity<?> sendPatchRequest(String uri, Map<String, String> headers, Map<String, String> params,
            Map<String, String[]> queryParams, Object bodyData) {
        return sendRequestWithDataUTF8(uri, HttpMethod.PATCH, headers, params, queryParams, bodyData, null);
    }

    /**
     * Call HTTP request (POST,PUT,PATCH,DELETE).
     *
     * @param uri         the uri
     * @param method      the method
     * @param headers     the headers
     * @param pathParams  the path params
     * @param queryParams the query params
     * @param bodyData    the body data
     * @param T           the class of response object
     * @return the response entity
     */
    private ResponseEntity<?> sendRequestWithDataUTF8(String uri, HttpMethod method, Map<String, String> headers,
            Map<String, String> pathParams, Map<String, String[]> queryParams, Object bodyData, Class<?> T) {
        logger.debug("IN - sendRequestWithDataUTF8()");

        HttpHeaders hd = new HttpHeaders();
        String UserID = "";
        String moduleID = "";
        if (headers != null && !headers.isEmpty()) {
            hd.setAll(headers);
            UserID = (headers.getOrDefault(Constant.HEADER_USER_ID, ""));
            moduleID = (headers.getOrDefault(Constant.HEADER_MODULE_ID, ""));
        }
        if (bodyData != null) {
            hd.setContentType(MediaType.APPLICATION_JSON_UTF8);
        }
        logger.debug("Request header: UserID = {}, moduleID = {}", UserID, moduleID);

        ResponseEntity<?> response = doSendRequestWithData(uri, method, hd, pathParams, queryParams, bodyData, T);
        logger.debug("OUT - sendRequestWithDataUTF8()");
        return response;
    }

    /**
     * Call HTTP request (POST,PUT,PATCH,DELETE).
     *
     * @param uri         the uri
     * @param method      the method
     * @param headers     the headers
     * @param pathParams  the path params
     * @param queryParams the query params
     * @param bodyData    the body data
     * @param T           the class of response object
     * @return the response entity
     */
    private ResponseEntity<?> sendRequestWithDataNonUTF8(String uri, HttpMethod method, Map<String, String> headers,
            Map<String, String> pathParams, Map<String, String[]> queryParams, Object bodyData, Class<?> T) {
        logger.debug("IN - sendRequestWithDataNonUTF8()");

        HttpHeaders hd = new HttpHeaders();
        if (headers != null && !headers.isEmpty()) {
            hd.setAll(headers);
        }
        if (bodyData != null) {
            hd.setContentType(MediaType.APPLICATION_JSON);
        }

        ResponseEntity<?> response = doSendRequestWithData(uri, method, hd, pathParams, queryParams, bodyData, T);
        logger.debug("OUT - sendRequestWithDataNonUTF8()");
        return response;
    }

    /**
     * Call HTTP request (POST,PUT,PATCH,DELETE).
     *
     * @param uri         the uri
     * @param method      the method
     * @param hd          the header
     * @param pathParams  the path params
     * @param queryParams the query params
     * @param bodyData    the body data
     * @param T           the class of response object
     * @return the response entity
     */
    private ResponseEntity<?> doSendRequestWithData(String uri, HttpMethod method, HttpHeaders hd,
            Map<String, String> pathParams, Map<String, String[]> queryParams, Object bodyData, Class<?> T) {
        logger.debug("IN - doSendRequestWithData()");

        if (pathParams != null && !pathParams.isEmpty()) {
            for (Map.Entry<String, ?> entry : pathParams.entrySet()) {
                uri += "{" + entry.getKey() + "}/";
            }
        }
        ResponseEntity<?> respResult = null;
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri);
            if (queryParams != null && !queryParams.isEmpty()) {
                for (Object key : queryParams.keySet()) {
                    String keyStr = (String) key;
                    String[] valueArr = queryParams.get(keyStr);
                    for (String value : valueArr) {
                        builder = builder.queryParam(keyStr, value);
                    }
                }
            }

            HttpEntity<?> entity = null;
            String jsonData = null;
            if (bodyData != null) {
                jsonData = Utils.parseObjectToJson(bodyData);
                entity = new HttpEntity<>(jsonData, hd);
            } else {
                entity = new HttpEntity<>(hd);
            }

            URI sendUri;
            if (pathParams != null && !pathParams.isEmpty()) {
                sendUri = builder.buildAndExpand(pathParams).encode().toUri();
            } else {
                sendUri = builder.build().encode().toUri();
            }
            logger.debug("Request header (full): {}", hd);
            writeLog(jsonData, "Request body");

            if (T == null) {
                if (bodyData != null) {
                    T = bodyData.getClass();
                } else {
                    T = Void.class;
                }
            }

            logger.debug("doSendRequestWithData with URI: {}", sendUri);
            if (method.equals(HttpMethod.PATCH)) {
                HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
                RestTemplate restTemplatePatch = new RestTemplate(requestFactory);
                respResult = restTemplatePatch.exchange(sendUri, HttpMethod.PATCH, entity, T);
                writeLog(Utils.parseObjectToJson(respResult.getBody()), "Response body");
                return respResult;
            }
            respResult = restTemplate.exchange(sendUri, method, entity, T);

            logger.debug("Response status: {}", respResult.getStatusCode());
            writeLog(Utils.parseObjectToJson(respResult.getBody()), "Response body");
        } catch (HttpClientErrorException e) {
            // Handle to return error message to sender
            switch (HttpStatus.valueOf(e.getRawStatusCode())) {
            case BAD_REQUEST:
            case NOT_FOUND:
            case CONFLICT:
                logger.warn(e.getResponseBodyAsString());
                respResult = ResponseEntity.status(HttpStatus.valueOf(e.getRawStatusCode()))
                        .body(e.getResponseBodyAsString());
                break;
            default:
                logger.warn(e.getMessage());
                respResult = ResponseEntity.status(HttpStatus.valueOf(e.getRawStatusCode())).body(e.getMessage());
            }
        } catch (HttpServerErrorException e) {
            // Handle to return error message to sender
            switch (HttpStatus.valueOf(e.getRawStatusCode())) {
            case INTERNAL_SERVER_ERROR:
                logger.warn(e.getResponseBodyAsString());
                respResult = ResponseEntity.status(HttpStatus.valueOf(e.getRawStatusCode()))
                        .body(e.getResponseBodyAsString());
                break;
            default:
                logger.warn(e.getMessage());
                respResult = ResponseEntity.status(HttpStatus.valueOf(e.getRawStatusCode())).body(e.getMessage());
            }
        } catch (ResourceAccessException e) {
            logger.error("Exception: ", e);
            respResult = ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Exception: ", e);
            respResult = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        logger.debug("OUT - doSendRequestWithData()");
        return respResult;
    }

    /**
     * Write debug log for message which have data greater than 64000 bytes.
     *
     * @param data        the data to write log
     * @param messageType the message type
     */
    private void writeLog(String data, String messageType) {
        if (data == null) {
            return;
        }
        List<String> msgList = splitLog(data);
        if (msgList.isEmpty()) {
            return;
        } else if (msgList.size() == 1) {
            logger.debug(messageType);
            logger.debug(msgList.get(0));
        } else {
            logger.debug("Split {} message into {} parts because size > 64000 bytes", messageType, msgList.size());
            for (int i = 0; i < msgList.size(); i++) {
                logger.debug("{} part {}", messageType, i + 1);
                logger.debug(msgList.get(i));
            }
        }
    }

    /**
     * Split log message into message list, each message will be under 64000 bytes.
     *
     * @param msg the original message
     * @return the message list
     */
    private List<String> splitLog(String msg) {
        // When using rsyslog, max size for udp is 64KB, need to split message which
        // size > 64KB
        // Because have other info + main log message, choose max size 64000 bytes
        int maxLength = 64000; // 64000 bytes
        List<String> messageList = new ArrayList<String>();
        if (msg == null || msg.isEmpty()) {
            return messageList;
        }
        try {
            byte[] b = msg.getBytes("UTF-8");
            if (b.length < maxLength) {
                messageList.add(msg);
                return messageList;
            }
            int index = 0;
            while (index < b.length) {
                int splitLength = (b.length - index < maxLength) ? b.length - index : maxLength;
                String str = new String(b, index, Math.min(index + maxLength, splitLength));
                messageList.add(str);
                index += maxLength;
            }
        } catch (Exception e) {
            logger.error("Exception: ", e);
        }
        return messageList;
    }
}
