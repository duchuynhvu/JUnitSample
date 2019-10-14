/*
 * Demo project
 */
package com.tmavn.sample.common;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmavn.sample.enums.ModuleEnum;
import com.tmavn.sample.model.CheckResult;
import com.tmavn.sample.model.ModuleAccess;

/**
 * The Class Utils.
 */
public class Utils {

    private Utils() {
        // You shall not instantiate an item of this class
    }

    /** The Constant logger. */
    private final static Logger logger = LoggerFactory.getLogger(Utils.class);

    /** The base directory. */
    private static String baseDirectory = null;

    /** The Constant mapper. */
    private static final ObjectMapper mapper = new ObjectMapper();

    /** The fb access list. */
    private static List<ModuleAccess> moduleAccessList = null;

    /** The Constant HTTP_1_1. */
    private static final String HTTP_1_1 = "HTTP/1.1";

    /** The Constant NO_CACHE. */
    private static final String NO_CACHE = "no-cache";

    /** The Constant EXPIRES_VALUE_DEFAULT. */
    private static final Date EXPIRES_VALUE_DEFAULT = Date
            .from(Instant.from(LocalDate.of(2000, 1, 1).atStartOfDay().atZone(ZoneId.systemDefault())));

    /** The Constant HIBERNATE_CONNECTION_DRIVER. */
    private static final String HIBERNATE_CONNECTION_DRIVER = "hibernate.connection.driver_class";

    /** The Constant HIBERNATE_CONNECTION_URL. */
    private static final String HIBERNATE_CONNECTION_URL = "hibernate.connection.url";

    /** The Constant HIBERNATE_CONNECTION_USERNAME. */
    private static final String HIBERNATE_CONNECTION_USERNAME = "hibernate.connection.username";

    /** The Constant HIBERNATE_CONNECTION_PASSWORD. */
    private static final String HIBERNATE_CONNECTION_PASSWORD = "hibernate.connection.password";

    /** The Constant HIBERNATE_CONNECTION_PASSWORD. */
    private static final String HIBERNATE_SHOW_SQL = "hibernate.show_sql";

    /** The Constant HIBERNATE_CONNECTION_PASSWORD. */
    private static final String HIBERNATE_DIALECT = "hibernate.dialect";

    /** The Constant HIBERNATE_CONNECTION_PASSWORD. */
    private static final String HIBERNATE_HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";

    /**
     * Gets the base directory.
     *
     * @return the baseDirectory
     * @throws ExceptionInInitializerError the exception in initializer error
     */
    public static String getBaseDirectory() throws ExceptionInInitializerError {
        logger.debug("IN - getBaseDirectory()");
        if (baseDirectory == null) {
            Properties baseProperties = new Properties();
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = null;

            // Read base directory
            try {
                is = classloader.getResourceAsStream("sample.conf");

                baseProperties.load(is);
                if (baseProperties.containsKey(Constant.BASE_DIRECTORY)) {
                    baseDirectory = baseProperties.getProperty(Constant.BASE_DIRECTORY);
                    baseDirectory = appendSlashIfNotExist(baseDirectory);
                    logger.debug("Base directory is: {}", baseDirectory);
                    logger.debug("OUT - getBaseDirectory()");
                } else {
                    logger.error("Have no setting {}", Constant.BASE_DIRECTORY);
                    throw new ExceptionInInitializerError();
                }
            } catch (IOException e) {
                logger.error("Exception: ", e);
                throw new ExceptionInInitializerError();
            } finally {
                try {
                    is.close();
                } catch (Exception e) {
                    logger.error("Exception: ", e);
                }
            }
        }
        logger.debug("Base directory is: {}", baseDirectory);
        logger.debug("OUT - getBaseDirectory()");
        return baseDirectory;
    }

    /**
     * Append slash to url if not exist
     * 
     * @param path the path
     * @return path
     */
    public static String appendSlashIfNotExist(String path) {
        if (path.charAt(path.length() - 1) != '/') {
            path += "/";
        }
        return path;
    }

    /**
     * Set header response.
     *
     * @param req the req
     * @param res the res
     */
    public static void setHeaderResponse(HttpServletRequest req, HttpServletResponse res) {
        logger.debug("IN - setHeaderResponse()");

        if (RequestMethod.DELETE.toString().equals(req.getMethod())) {
            logger.debug("OUT - setHeaderResponse()");
            return;
        }

        boolean checkedSetContentType = res.getStatus() == HttpStatus.CREATED.value();
        if (checkedSetContentType) {
            res.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        }
        res.setDateHeader(HttpHeaders.EXPIRES, EXPIRES_VALUE_DEFAULT.getTime());
        res.setHeader(HttpHeaders.CACHE_CONTROL, NO_CACHE);
        logger.debug("OUT - setHeaderResponse()");
    }

    /**
     * Check HTTP Request.
     *
     * @param req    HttpServletRequest
     * @param module the module
     * @return the check result
     */
    public static CheckResult checkRequestMessageCommon(HttpServletRequest req, ModuleEnum module) {
        logger.debug("IN - checkRequestMessageCommon()");

        // 5. Check HTTP header configuration
        CheckResult result = new CheckResult(true, CheckResult.MSG_OK);
        // Ignore this process for homepage
        if (req.getRequestURI().length() == req.getContextPath().length() + 1) {
            logger.debug("OUT - checkRequestMessageCommon()");
            return result;
        }

        logger.debug("Protocol Version: {}", req.getProtocol());
        if (!HTTP_1_1.equals(req.getProtocol())) {
            result.setSuccess(false);

            result.setMessage("");
            logger.debug("OUT - checkRequestMessageCommon()");
            return result;
        }

        logger.debug("ContentType: {}", req.getContentType());
        logger.debug("ContentLength: {}", req.getContentLength());
        logger.debug("Method: {}", req.getMethod());
        if (!req.getMethod().equals(RequestMethod.GET.toString())
                && !req.getMethod().equals(RequestMethod.DELETE.toString())) {
            if (!MediaType.APPLICATION_JSON_VALUE.equals(req.getContentType())
                    && !MediaType.APPLICATION_JSON_UTF8_VALUE.equals(req.getContentType())
                    || req.getContentLength() == -1 || req.getContentLength() == 0) {
                result.setSuccess(false);

                result.setMessage(CheckResult.MSG_JSON_NG);
                logger.debug("OUT - checkRequestMessageCommon()");
                return result;
            }

        }
        // 9. Check UserID & Operation Id in header
        String UserID = req.getHeader(Constant.HEADER_USER_ID);
        String operatorId = req.getHeader(Constant.HEADER_MODULE_ID);
        logger.debug("x-lcOpOccurenceId: {}", isEmpty(UserID) ? "" : UserID);
        logger.debug("x-operatorId: {}", isEmpty(operatorId) ? "" : operatorId);

        if (isEmpty(UserID)) {
            result.setSuccess(false);
            result.setMessage(CheckResult.MSG_HEADER_NG);
            logger.debug("OUT - checkRequestMessageCommon()");
            return result;
        }

        switch (module) {
        case SAMPLE_MODULE:

        }
        logger.debug("OUT - checkRequestMessageCommon()");
        return result;
    }

    /**
     * Check empty string.
     *
     * @param string the string
     * @return true if string = null or string is empty
     */
    private static boolean isEmpty(String string) {
        logger.debug("IN - isEmpty()");
        if (string == null || string.isEmpty()) {
            logger.debug("OUT - isEmpty()");
            return true;
        }
        logger.debug("OUT - isEmpty()");
        return false;
    }

    /**
     * Gets the fb access list.
     *
     * @return the fbAccessList
     */
    @SuppressWarnings("unchecked")
    public static synchronized List<ModuleAccess> getModuleAccessList() {
        logger.debug("IN - getModuleAccessList()");
        if (moduleAccessList == null) {
            logger.debug("Read module-access");
            String fbAccessFile = getBaseDirectory() + Constant.MODULE_ACCESS_FILE;
            String json = readJsonFile(fbAccessFile);
            if (json != null) {
                String schemaFile = getBaseDirectory() + Constant.JsonSchema.MODULE_ACCESS;
                CheckResult result = JsonValidation.validate(schemaFile, json);
                if (result.isSuccess()) {
                    moduleAccessList = ((List<ModuleAccess>) parseJson(json, new TypeReference<List<ModuleAccess>>() {
                    }));
                } else {
                    logger.warn(result.getMessage());
                }
            } else {
                logger.error("Can not read module-access.json");
            }
        }
        logger.debug("OUT - getModuleAccessList()");
        return moduleAccessList;
    }

    /**
     * Read json from file.
     *
     * @param filePath      String
     * @param typeReference the type reference
     * @return Object
     */
    public static Object readJsonFile(String filePath, TypeReference<?> typeReference) {
        // get data from file
        logger.debug("IN - readJsonFile()");
        Object object = null;
        byte[] jsonData;
        try {
            jsonData = Files.readAllBytes(Paths.get(filePath));
            // read json to object
            object = mapper.readValue(jsonData, typeReference);
            logger.debug("OUT - readJsonFile()");
        } catch (IOException e) {
            logger.error("Exception: ", e);
        }
        return object;
    }

    /**
     * Read json file.
     *
     * @param filePath the file path
     * @return the string
     */
    public static String readJsonFile(String filePath) {
        // get data from file
        logger.debug("IN - readJsonFile()");
        String result = null;
        try {
            byte[] jsonData;
            jsonData = Files.readAllBytes(Paths.get(filePath));
            // read json to object
            result = new String(jsonData);
        } catch (IOException e) {
            logger.error("Exception: ", e);
        } finally {
            logger.debug("OUT - readJsonFile()");
        }
        return result;
    }

    /**
     * Parses the json.
     *
     * @param jsonData      the json data
     * @param typeReference the type reference
     * @return the object
     */
    public static Object parseJson(String jsonData, TypeReference<?> typeReference) {
        // get data from file
        logger.debug("IN - parseJson()");
        Object object = null;
        try {
            // read json to object
            object = mapper.readValue(jsonData, typeReference);
            logger.debug("OUT - parseJson()");
        } catch (IOException e) {
            logger.error("Exception: ", e);
        }
        return object;
    }

    /**
     * Parses the object to json.
     *
     * @param object the object
     * @return the json string
     */
    public static String parseObjectToJson(Object object) {
        logger.debug("IN - parseObjectToJson");
        if (object == null) {
            logger.debug("OUT - parseObjectToJson");
            return null;
        }
        String json = null;
        try {
            logger.debug("OUT - parseObjectToJson");
            json = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Exception: ", e);
        }
        return json;
    }

    /**
     * Read properties file.
     *
     * @param filePath the file path
     * @return the properties
     */
    public static Properties readPropertiesFile(String filePath) {
        logger.debug("IN - readPropertiesFile()");
        Properties properties = null;

        // FileInputStream fileInputStream;
        try {
            // fileInputStream = new FileInputStream(filePath);
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"));
            properties = new Properties();
            properties.load(in);
            in.close();
        } catch (Exception e) {
            logger.error("Exception: ", e);
        }
        logger.debug("OUT - readPropertiesFile()");
        return properties;
    }

    /**
     * Check hibernate config.
     *
     * @param properties the properties
     * @return true, if successful
     */
    public static boolean checkHibernateConfig(Properties properties) {
        logger.debug("IN - checkHibernateConfig and Read properites");

        if (!properties.containsKey(HIBERNATE_CONNECTION_DRIVER)) {
            logger.error("{} proprerty isn't configed", HIBERNATE_CONNECTION_DRIVER);
            logger.debug("OUT - checkHibernateConfig");
            return false;
        }

        if (!properties.containsKey(HIBERNATE_CONNECTION_URL)) {
            logger.error("{} proprerty isn't configed", HIBERNATE_CONNECTION_URL);
            logger.debug("OUT - checkHibernateConfig");
            return false;
        }

        if (!properties.containsKey(HIBERNATE_CONNECTION_USERNAME)) {
            logger.error("{} proprerty isn't configed", HIBERNATE_CONNECTION_USERNAME);
            logger.debug("OUT - checkHibernateConfig");
            return false;
        }

        if (!properties.containsKey(HIBERNATE_CONNECTION_PASSWORD)) {
            logger.error("{} proprerty isn't configed", HIBERNATE_CONNECTION_PASSWORD);
            logger.debug("OUT - checkHibernateConfig");
            return false;
        }

        if (!properties.containsKey(HIBERNATE_DIALECT)) {
            logger.error("{} property isn't configed", HIBERNATE_DIALECT);
            logger.debug("OUT - checkHibernateConfig");
            return false;
        }

        if (!properties.containsKey(HIBERNATE_SHOW_SQL)) {
            logger.error("{} property isn't configed", HIBERNATE_SHOW_SQL);
            logger.debug("OUT - checkHibernateConfig");
            return false;
        }

        if (!properties.containsKey(HIBERNATE_HBM2DDL_AUTO)) {
            logger.error("{} property isn't configed", HIBERNATE_HBM2DDL_AUTO);
            logger.debug("OUT - checkHibernateConfig");
            return false;
        }
        logger.debug("OUT - checkHibernateConfig");

        return true;
    }

    /**
     * Find FbAccess Url from config file.
     *
     * @param functionBlockName the function block name
     * @param resourceName      the resource name
     * @return url
     */
    public static String findModuleAccessUrl(String moduleName, String resourceName) {
        logger.debug("IN - findModuleAccessUrl(), moduleName = {}, resourceName = {}", moduleName, resourceName);
        String url = null;
        List<ModuleAccess> moduleAccessList = getModuleAccessList();
        if (moduleAccessList != null) {
            for (ModuleAccess moduleAccess : moduleAccessList) {
                if (moduleName.equals(moduleAccess.getModuleName())
                        && resourceName.equals(moduleAccess.getResourceName())) {
                    url = moduleAccess.getUrl();
                    break;
                }
            }
        }
        logger.debug("OUT - findModuleAccessUrl()");
        return url;

    }

}
