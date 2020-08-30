
package com.devappliance.ninjadoc.util;

import org.springframework.util.ResourceUtils;

import static org.springframework.util.AntPathMatcher.DEFAULT_PATH_SEPARATOR;

/**
 * The type Constants.
 *
 * @author bnasslahsen
 */
public final class Constants {

    /**
     * The constant DEFAULT_SERVER_DESCRIPTION.
     */
    public static final String DEFAULT_SERVER_DESCRIPTION = "Generated server url";

    /**
     * The constant NINJADOC_DEPRECATING_CONVERTER_ENABLED.
     */
    public static final String NINJADOC_DEPRECATING_CONVERTER_ENABLED = "ninjadoc.model-converters.deprecating-converter.enabled";

    /**
     * The constant NINJADOC_SCHEMA_RESOLVE_PROPERTIES.
     */
    public static final String NINJADOC_SCHEMA_RESOLVE_PROPERTIES = "ninjadoc.api-docs.resolve-schema-properties";

    /**
     * The constant NINJADOC_CACHE_DISABLED.
     */
    public static final String NINJADOC_CACHE_DISABLED = "ninjadoc.cache.disabled";

    /**
     * The constant NINJADOC_SWAGGER_UI_ENABLED.
     */
    public static final boolean NINJADOC_SWAGGER_UI_ENABLED = false;

    /**
     * The constant NULL.
     */
    public static final String NULL = ":#{null}";

    /**
     * The constant NINJADOC_SHOW_ACTUATOR.
     */
    public static final String NINJADOC_SHOW_ACTUATOR = "ninjadoc.show-actuator";

    /**
     * The constant NINJADOC_ACTUATOR_TAG.
     */
    public static final String NINJADOC_ACTUATOR_TAG = "Actuator";

    /**
     * The constant NINJADOC_ACTUATOR_DESCRIPTION.
     */
    public static final String NINJADOC_ACTUATOR_DESCRIPTION = "Monitor and interact";

    /**
     * The constant NINJADOC_ACTUATOR_DOC_DESCRIPTION.
     */
    public static final String NINJADOC_ACTUATOR_DOC_DESCRIPTION = "Spring Boot Actuator Web API Documentation";

    /**
     * The constant DEFAULT_WEB_JARS_PREFIX_URL.
     */
    public static final String DEFAULT_WEB_JARS_PREFIX_URL = "/ndoc";

    /**
     * The constant CLASSPATH_RESOURCE_LOCATION.
     */
    public static final String CLASSPATH_RESOURCE_LOCATION = ResourceUtils.CLASSPATH_URL_PREFIX + "/META-INF/resources";

    /**
     * The constant SWAGGER_UI_URL.
     */
    public static final String SWAGGER_RES_BASE = "/swagger-ui/3.32.5/";
    /**
     * The constant SWAGGER_UI_URL.
     */
    public static final String SWAGGER_UI_URL = SWAGGER_RES_BASE + "index.html";

    /**
     * The constant SWAGGER_UI_OAUTH_REDIRECT_URL.
     */
    public static final String SWAGGER_UI_OAUTH_REDIRECT_URL = "/swagger-ui/oauth2-redirect.html";

    /**
     * The constant APPLICATION_OPENAPI_YAML.
     */
    public static final String APPLICATION_OPENAPI_YAML = "application/vnd.oai.openapi";

    /**
     * The constant DEFAULT_SWAGGER_UI_PATH.
     */
    public static final String DEFAULT_SWAGGER_UI_PATH = DEFAULT_PATH_SEPARATOR + "swagger-ui.html";

    /**
     * The constant SWAGGER_UI_PATH.
     */
    public static final String SWAGGER_UI_PATH = DEFAULT_SWAGGER_UI_PATH;

    /**
     * The constant DEFAULT_GROUP_NAME.
     */
    public static final String DEFAULT_GROUP_NAME = "ninjadocDefault";

    /**
     * The constant GROUP_CONFIG_FIRST_PROPERTY.
     */
    public static final String GROUP_CONFIG_FIRST_PROPERTY = "ninjadoc.group-configs[0].group";

    /**
     * The constant GROUP_NAME_NOT_NULL.
     */
    public static final String GROUP_NAME_NOT_NULL = "Group name can not be null";

    /**
     * The constant GET_METHOD.
     */
    public static final String GET_METHOD = "get";

    /**
     * The constant POST_METHOD.
     */
    public static final String POST_METHOD = "post";

    /**
     * The constant PUT_METHOD.
     */
    public static final String PUT_METHOD = "put";

    /**
     * The constant DELETE_METHOD.
     */
    public static final String DELETE_METHOD = "delete";

    /**
     * The constant PATCH_METHOD.
     */
    public static final String PATCH_METHOD = "patch";

    /**
     * The constant TRACE_METHOD.
     */
    public static final String TRACE_METHOD = "trace";

    /**
     * The constant HEAD_METHOD.
     */
    public static final String HEAD_METHOD = "head";

    /**
     * The constant OPTIONS_METHOD.
     */
    public static final String OPTIONS_METHOD = "options";

    /**
     * The constant QUERY_PARAM.
     */
    public static final String QUERY_PARAM = "query";

    /**
     * The constant DEFAULT_DESCRIPTION.
     */
    public static final String DEFAULT_DESCRIPTION = "default response";

    /**
     * The constant DEFAULT_TITLE.
     */
    public static final String DEFAULT_TITLE = "OpenAPI definition";

    /**
     * The constant DEFAULT_VERSION.
     */
    public static final String DEFAULT_VERSION = "v0";

    /**
     * The constant OPENAPI_STRING_TYPE.
     */
    public static final String OPENAPI_STRING_TYPE = "string";

    /**
     * The constant OPENAPI_ARRAY_TYPE.
     */
    public static final String OPENAPI_ARRAY_TYPE = "array";

    /**
     * The constant GRACEFUL_EXCEPTION_OCCURRED.
     */
    public static final String GRACEFUL_EXCEPTION_OCCURRED = "Graceful exception occurred";

    /**
     * The constant SWAGGER_UI_DEFAULT_URL.
     */
    public static final String SWAGGER_UI_DEFAULT_URL = "https://petstore.swagger.io/v2/swagger.json";

    /**
     * The constant CSRF_DEFAULT_COOKIE_NAME.
     */
    public static final String CSRF_DEFAULT_COOKIE_NAME = "XSRF-TOKEN";

    /**
     * The constant CSRF_DEFAULT_HEADER_NAME.
     */
    public static final String CSRF_DEFAULT_HEADER_NAME = "X-XSRF-TOKEN";
    /**
     * Swagger config file name
     */
    public static final String SWAGGGER_CONFIG_FILE = "swagger-config";

    /**
     * Instantiates a new Constants.
     */
    private Constants() {
        super();
    }

}
