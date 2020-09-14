
package com.devappliance.ninjadoc.controllers;

import com.devappliance.ninjadoc.NinjaDocConfigProperties;
import com.devappliance.ninjadoc.SwaggerUiConfigParameters;
import com.devappliance.ninjadoc.SwaggerUiConfigProperties;
import com.devappliance.ninjadoc.util.Constants;
import ninja.lifecycle.Start;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.util.CollectionUtils;

import java.net.URISyntaxException;

import static org.springframework.util.AntPathMatcher.DEFAULT_PATH_SEPARATOR;

public abstract class AbstractSwaggerWelcome {

    /**
     * The Swagger ui configuration.
     */
    protected final SwaggerUiConfigProperties swaggerUiConfig;

    /**
     * The Spring doc config properties.
     */
    protected final NinjaDocConfigProperties ninjaDocConfigProperties;

    /**
     * The Swagger ui calculated config.
     */
    protected final SwaggerUiConfigParameters swaggerUiConfigParameters;


    /**
     * Instantiates a new Abstract swagger welcome.
     *
     * @param swaggerUiConfig           the swagger ui config
     * @param ninjaDocConfigProperties  the spring doc config properties
     * @param swaggerUiConfigParameters the swagger ui config parameters
     */
    public AbstractSwaggerWelcome(SwaggerUiConfigProperties swaggerUiConfig, NinjaDocConfigProperties ninjaDocConfigProperties, SwaggerUiConfigParameters swaggerUiConfigParameters) {
        this.swaggerUiConfig = swaggerUiConfig;
        this.ninjaDocConfigProperties = ninjaDocConfigProperties;
        this.swaggerUiConfigParameters = swaggerUiConfigParameters;
    }

    @Start(order = 90)
    public void afterPropertiesSet() {
        ninjaDocConfigProperties.getGroupConfigs().forEach(groupConfig -> swaggerUiConfigParameters.addGroup(groupConfig.getGroup()));
        calculateUiRootPath();
    }

    /**
     * Build url string.
     *
     * @param contextPath the context path
     * @param docsUrl     the docs url
     * @return the string
     */
    protected String buildUrl(String contextPath, final String docsUrl) {
        if (contextPath.endsWith(DEFAULT_PATH_SEPARATOR)) {
            return contextPath.substring(0, contextPath.length() - 1) + docsUrl;
        }
        return contextPath + docsUrl;
    }

    protected void buildConfigUrl(String contextPath, URIBuilder uriBuilder) throws URISyntaxException {
        String apiDocsUrl = ninjaDocConfigProperties.getApiDocs().getPath();
        if (StringUtils.isEmpty(swaggerUiConfig.getConfigUrl())) {
            String url = buildUrl(contextPath, apiDocsUrl);
            String swaggerConfigUrl = url + DEFAULT_PATH_SEPARATOR + Constants.SWAGGGER_CONFIG_FILE;
            swaggerUiConfigParameters.setConfigUrl(swaggerConfigUrl);
            if (CollectionUtils.isEmpty(swaggerUiConfigParameters.getUrls())) {
                String swaggerUiUrl = swaggerUiConfig.getUrl();
                if (StringUtils.isEmpty(swaggerUiUrl))
                    swaggerUiConfigParameters.setUrl(url);
                else
                    swaggerUiConfigParameters.setUrl(swaggerUiUrl);
            } else
                swaggerUiConfigParameters.addUrl(url);
        }
        calculateOauth2RedirectUrl(uriBuilder);
    }

    /**
     * Gets uri components builder.
     *
     * @param sbUrl the sb url
     * @return the uri components builder
     * @throws URISyntaxException
     */
    protected URIBuilder getUriComponentsBuilder(String sbUrl) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(sbUrl);
        if (swaggerUiConfig.isDisplayQueryParams() && StringUtils.isNotEmpty(swaggerUiConfigParameters.getUrl())) {
            swaggerUiConfigParameters.getConfigParameters().entrySet().stream()
                    .filter(entry -> !SwaggerUiConfigParameters.CONFIG_URL_PROPERTY.equals(entry.getKey()))
                    .filter(entry -> !entry.getKey().startsWith(SwaggerUiConfigParameters.URLS_PROPERTY))
                    .filter(entry -> StringUtils.isNotEmpty((String) entry.getValue()))
                    .forEach(entry -> uriBuilder.addParameter(entry.getKey(), String.valueOf(entry.getValue())));
        } else if (swaggerUiConfig.isDisplayQueryParamsWithoutOauth2() && StringUtils.isNotEmpty(swaggerUiConfigParameters.getUrl())) {
            swaggerUiConfigParameters.getConfigParameters().entrySet().stream()
                    .filter(entry -> !SwaggerUiConfigParameters.CONFIG_URL_PROPERTY.equals(entry.getKey()))
                    .filter(entry -> !SwaggerUiConfigParameters.OAUTH2_REDIRECT_URL_PROPERTY.equals(entry.getKey()))
                    .filter(entry -> !entry.getKey().startsWith(SwaggerUiConfigParameters.URLS_PROPERTY))
                    .filter(entry -> StringUtils.isNotEmpty((String) entry.getValue()))
                    .forEach(entry -> uriBuilder.addParameter(entry.getKey(), String.valueOf(entry.getValue())));
        } else {
            uriBuilder.addParameter(SwaggerUiConfigParameters.CONFIG_URL_PROPERTY, swaggerUiConfigParameters.getConfigUrl());
            if (StringUtils.isNotEmpty(swaggerUiConfigParameters.getLayout()))
                uriBuilder.addParameter(SwaggerUiConfigParameters.LAYOUT_PROPERTY, swaggerUiConfigParameters.getLayout());
            if (StringUtils.isNotEmpty(swaggerUiConfigParameters.getFilter()))
                uriBuilder.addParameter(SwaggerUiConfigParameters.FILTER_PROPERTY, swaggerUiConfigParameters.getFilter());
        }
        return uriBuilder;
    }

    /**
     * Calculate ui root path.
     *
     * @param sbUrls the sb urls
     */
    protected abstract void calculateUiRootPath(StringBuilder... sbUrls);

    protected abstract void calculateOauth2RedirectUrl(URIBuilder uriComponentsBuilder) throws URISyntaxException;
}
