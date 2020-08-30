/*
 *
 *  *
 *  *  * Copyright 2019-2020 the original author or authors.
 *  *  *
 *  *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  * you may not use this file except in compliance with the License.
 *  *  * You may obtain a copy of the License at
 *  *  *
 *  *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *  *
 *  *  * Unless required by applicable law or agreed to in writing, software
 *  *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  * See the License for the specific language governing permissions and
 *  *  * limitations under the License.
 *  *
 *
 */

package com.devappliance.ninjadoc.controllers;

import com.devappliance.ninjadoc.*;
import com.devappliance.ninjadoc.config.ConfigKeys;
import com.devappliance.ninjadoc.config.NinjaDocDefaultConfig;
import com.devappliance.ninjadoc.customizers.OpenApiCustomiser;
import com.devappliance.ninjadoc.customizers.OperationCustomizer;
import com.devappliance.ninjadoc.wrappers.HandlerMethod;
import com.devappliance.ninjadoc.wrappers.RequestMethod;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Injector;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.PathUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.models.OpenAPI;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.Route;
import ninja.utils.NinjaProperties;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.util.AntPathMatcher.DEFAULT_PATH_SEPARATOR;

/**
 * The type Open api resource.
 *
 * @author bnasslahsen
 */
public class OpenApiResource extends AbstractOpenApiResource {
    private NinjaProperties ninjaProperties;

    /**
     * The Request mapping handler mapping.
     */

    /**
     * Instantiates a new Open api resource.
     *
     * @param groupName                   the group name
     * @param openAPIBuilderObjectFactory the open api builder object factory
     * @param requestBuilder              the request builder
     * @param responseBuilder             the response builder
     * @param operationParser             the operation parser
     * @param operationCustomizers        the operation customizers
     * @param openApiCustomisers          the open api customisers
     * @param ninjaDocConfigProperties    the spring doc config properties
     */
    public OpenApiResource(String groupName, Provider<OpenAPIBuilder> openAPIBuilderObjectFactory, AbstractRequestBuilder requestBuilder,
                           GenericResponseBuilder responseBuilder, OperationBuilder operationParser,
                           Optional<Set<OperationCustomizer>> operationCustomizers,
                           Optional<Set<OpenApiCustomiser>> openApiCustomisers,
                           NinjaDocConfigProperties ninjaDocConfigProperties, Injector injector) {
        super(groupName, openAPIBuilderObjectFactory, requestBuilder, responseBuilder, operationParser, operationCustomizers,
                openApiCustomisers, ninjaDocConfigProperties, injector);

    }

    @Inject
    public OpenApiResource(Provider<OpenAPIBuilder> openAPIBuilderObjectFactory, AbstractRequestBuilder requestBuilder,
                           GenericResponseBuilder responseBuilder, OperationBuilder operationParser,
                           Optional<Set<OperationCustomizer>> operationCustomizers,
                           Optional<Set<OpenApiCustomiser>> openApiCustomisers,
                           NinjaDocConfigProperties ninjaDocConfigProperties, Injector injector, NinjaProperties ninjaProperties) {
        super(NinjaDocDefaultConfig.DEFAULT_GROUP_NAME, openAPIBuilderObjectFactory, requestBuilder, responseBuilder, operationParser,
                operationCustomizers, openApiCustomisers, ninjaDocConfigProperties, injector);
        this.ninjaProperties = ninjaProperties;
    }

    @Operation(hidden = true)
    public Result openapiJson(Context context) throws JsonProcessingException {
        String apiDocsUrl = ninjaDocConfigProperties.getApiDocs().getPath();
        return extractOpenapiJson(context, apiDocsUrl);
    }

    public Result extractOpenapiJson(Context context, String apiDocsUrl) throws JsonProcessingException {
        calculateServerUrl(context, apiDocsUrl);
        OpenAPI openAPI = this.getOpenApi();
        if (!ninjaDocConfigProperties.isWriterWithDefaultPrettyPrinter())
            return Results.json().renderRaw(Json.mapper().writeValueAsString(openAPI).getBytes());
        else
            return Results.json().render(Json.mapper().writerWithDefaultPrettyPrinter().writeValueAsString(openAPI).getBytes());
    }

    @Operation(hidden = true)
    public Result openapiYaml(Context context) throws Exception {
        String apiDocsUrl = ninjaProperties.getWithDefault(ConfigKeys.API_DOCS_URL_YAML, NinjaDocDefaultConfig.DEFAULT_API_DOCS_URL_YAML);
        return extractOpenapiYaml(context, apiDocsUrl);
    }

    public Result extractOpenapiYaml(Context context, String apiDocsUrl) throws JsonProcessingException {
        calculateServerUrl(context, apiDocsUrl);
        OpenAPI openAPI = this.getOpenApi();
        if (!ninjaDocConfigProperties.isWriterWithDefaultPrettyPrinter())
            return Results.contentType("text/plain").render(getYamlMapper().writeValueAsString(openAPI));
        else
            return Results.contentType("text/plain").render(getYamlMapper().writerWithDefaultPrettyPrinter().writeValueAsString(openAPI));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void getPaths(Map<String, Object> restControllers) {
        Map<Route, HandlerMethod> routes = applicationContext.getRoutes().stream().collect(Collectors.toMap(route -> route, o -> new HandlerMethod(applicationContext.getBean(o.getControllerClass()), o.getControllerMethod())));
        calculatePath(restControllers, routes);
    }

    /**
     * Calculate path.
     *
     * @param restControllers the rest controllers
     * @param map             the map
     */
    protected void calculatePath(Map<String, Object> restControllers, Map<Route, HandlerMethod> map) {
        for (Map.Entry<Route, HandlerMethod> entry : map.entrySet()) {
            Route route = entry.getKey();
            HandlerMethod handlerMethod = entry.getValue();
            Set<String> patterns = Collections.singleton(route.getUri());
            Map<String, String> regexMap = new LinkedHashMap<>();
            for (String pattern : patterns) {
                String operationPath = PathUtils.parsePath(pattern, regexMap);
                if ((isRestController(restControllers, handlerMethod, operationPath))
                        && isPackageToScan(handlerMethod.getBeanType().getPackage())
                        && isPathToMatch(operationPath)) {
                    Set<RequestMethod> requestMethods = new HashSet<RequestMethod>();
                    requestMethods.add(RequestMethod.valueOf(route.getHttpMethod()));
                    // default allowed requestmethods
                    if (requestMethods.isEmpty())
                        requestMethods = this.getDefaultAllowedHttpMethods();
                    calculatePath(handlerMethod, operationPath, requestMethods);
                }
            }
        }
    }


    /**
     * Is rest controller boolean.
     *
     * @param restControllers the rest controllers
     * @param handlerMethod   the handler method
     * @param operationPath   the operation path
     * @return the boolean
     */
    protected boolean isRestController(Map<String, Object> restControllers, HandlerMethod handlerMethod,
                                       String operationPath) {

        return (restControllers.containsKey(handlerMethod.getBean().getClass().getSimpleName()) || isAdditionalRestController(handlerMethod.getBeanType()))
                && operationPath.startsWith(DEFAULT_PATH_SEPARATOR);
    }

    /**
     * Calculate server url.
     *
     * @param context
     * @param apiDocsUrl the api docs url
     */
    protected void calculateServerUrl(Context context, String apiDocsUrl) {
        super.initOpenAPIBuilder();
        String requestUrl = decode(String.format("%s://%s%s%s", context.getScheme(), context.getHostname(), context.getContextPath(), context.getRequestPath()));
        String calculatedUrl = requestUrl.substring(0, requestUrl.length() - apiDocsUrl.length());
        openAPIBuilder.setServerBaseUrl(calculatedUrl);
    }

}
