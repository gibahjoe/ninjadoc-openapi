package com.devappliance.ninjadoc.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.inject.Named;

/**
 * @author Gibah Joseph
 * email: gibahjoe@gmail.com
 * Jul, 2020
 **/
@Singleton
public class NinjaDocConfig {
    @Inject(optional = true)
    @Named(ConfigKeys.API_DOCS_URL)
    private String apiDocsUrl = NinjaDocDefaultConfig.DEFAULT_API_DOCS_URL;
    @Inject(optional = true)
    @Named(ConfigKeys.API_DOCS_URL_YAML)
    private String apiDocsUrlYaml = NinjaDocDefaultConfig.DEFAULT_API_DOCS_URL_YAML;
    @Inject(optional = true)
    @Named(ConfigKeys.SWAGGER_UI_PATH)
    private String swaggerUiPath = NinjaDocDefaultConfig.SWAGGER_UI_PATH;
    @Inject(optional = true)
    @Named(ConfigKeys.NINJADOC_SWAGGER_UI_ENABLED)
    private boolean swaggerUiEnabled = NinjaDocDefaultConfig.SWAGGER_UI_ENABLED;
    @Inject(optional = true)
    @Named(ConfigKeys.SWAGGER_CONFIG_URL)
    private String swaggerConfigUrl = NinjaDocDefaultConfig.SWAGGER_CONFIG_URL;
    @Inject(optional = true)
    @Named(ConfigKeys.NINJADOC_DEPRECATING_CONVERTER_ENABLED)
    private boolean deprecatingConverterEnabled = NinjaDocDefaultConfig.NINJADOC_DEPRECATING_CONVERTER_ENABLED;

    public String getApiDocsUrl() {
        return apiDocsUrl;
    }

    public String getApiDocsUrlYaml() {
        return apiDocsUrlYaml;
    }

    public String getSwaggerUiPath() {
        return swaggerUiPath;
    }

    public String getSwaggerConfigUrl() {
        return swaggerConfigUrl;
    }
}
