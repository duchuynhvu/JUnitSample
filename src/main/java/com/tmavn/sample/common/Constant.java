/*
 * Demo project
 */

package com.tmavn.sample.common;

/**
 * The Class Constant.
 */
public class Constant {

    private Constant() {
        // You shall not instantiate an item of this class
    }

    /** Base directory. */
    public static final String BASE_DIRECTORY = "base.directory";

    /** The Constant SAMPLE_CONFIG. */
    public static final String SAMPLE_CONFIG = "/sample.conf";

    /** Path of Logback.xml for SAMPLE RESOURCE. */
    public static final String LOG_BACK_PATH = "logback.xml";

    /** Header HEADER_USER_ID. */
    public static final String HEADER_USER_ID = "UserID";

    /** The Constant HEADER_MODULE_ID. */
    public static final String HEADER_MODULE_ID = "ModuleID";

    /** COMMON */
    /** The Constant MODULE_ACCESS_FILE. */
    public static final String MODULE_ACCESS_FILE = "/access/module_access.json";

    /** Date pattern */
    public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * Json schema.
     */
    public static final class JsonSchema {

        private JsonSchema() {
            // You shall not instantiate an item of this class
        }

        /** The Constant MODULE_ACCESS. */
        public static final String MODULE_ACCESS = "/jsonSchema/module_access.json";

        /** The Constant ORDER_DATA_POST. */
        public static final String ORDER_DATA_POST = "/jsonSchema/order_data_post.json";

        /** The Constant ORDER_DATA_PUT. */
        public static final String ORDER_DATA_PUT = "/jsonSchema/order_data_put.json";

        /** The Constant ORDER_DATA_PATCH. */
        public static final String ORDER_DATA_PATCH = "/jsonSchema/order_data_patch.json";

        /** The Constant LISTENER_INFO_POST. */
        public static final String LISTENER_INFO_POST = "/jsonSchema/listener_info_post.json";

        /** The Constant LISTENER_INFO_PUT. */
        public static final String LISTENER_INFO_PUT = "/jsonSchema/listener_info_put.json";

        /** The Constant LISTENER_INFO_PATCH. */
        public static final String LISTENER_INFO_PATCH = "/jsonSchema/listener_info_patch.json";
    }

    public static final class ConfigProperties {

        private ConfigProperties() {
            // You shall not instantiate an item of this class
        }

        /** The MODULE_ID */
        public static final String MODULE_ID = "moduleId";

        /** The functions/sample */
        public static final String USER_ID = "userId";

    }

    public static final class ModuleResource {

        private ModuleResource() {
            // You shall not instantiate an item of this class
        }

        /** The MODULE_OPS */
        public static final String MODULE_OPS = "OPS";

        /** The RESOURCE_CREATE_ORDER */
        public static final String RESOURCE_CREATE_ORDER = "CreateOrder";
    }
}
