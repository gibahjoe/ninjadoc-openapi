package com.devappliance.ninjadoc.config;

import com.devappliance.ninjadoc.util.Constants;

import static org.springframework.util.AntPathMatcher.DEFAULT_PATH_SEPARATOR;

/**
 * @author Gibah Joseph
 * email: gibahjoe@gmail.com
 * Jul, 2020
 **/
public class NinjaDocDefaultConfig {


    public static final boolean SWAGGER_UI_ENABLED = true;
    public static final String DEFAULT_API_DOCS_URL = "/v3/api-docs";
    public static final String SWAGGER_UI_PATH = DEFAULT_PATH_SEPARATOR + "swagger-ui.html";
    public static final String API_DOCS_URL = DEFAULT_API_DOCS_URL;
    public static final String DEFAULT_API_DOCS_URL_YAML = API_DOCS_URL + ".yaml";
    public static final String SWAGGER_CONFIG_URL = API_DOCS_URL + DEFAULT_PATH_SEPARATOR + Constants.SWAGGGER_CONFIG_FILE;
    public static final String DEFAULT_GROUP_NAME = "ninjadocDefault";
    public static final boolean NINJADOC_DEPRECATING_CONVERTER_ENABLED = true;
}
