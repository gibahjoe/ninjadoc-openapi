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
import com.devappliance.ninjadoc.util.AfterInjectionListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Injector;
import io.swagger.v3.oas.annotations.Operation;
import ninja.Context;
import ninja.Result;
import ninja.params.PathParam;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.util.AntPathMatcher.DEFAULT_PATH_SEPARATOR;

/**
 * The type Multiple open api resource.
 *
 * @author bnasslahsen
 */
@Singleton
public class MultipleOpenApiResource implements AfterInjectionListener {

    /**
     * The Grouped open apis.
     */
    private final List<GroupedOpenApi> groupedOpenApis;

    /**
     * The Default open api builder.
     */
    private final javax.inject.Provider<OpenAPIBuilder> defaultOpenAPIBuilder;

    /**
     * The Request builder.
     */
    private final AbstractRequestBuilder requestBuilder;

    /**
     * The Response builder.
     */
    private final GenericResponseBuilder responseBuilder;

    /**
     * The Operation parser.
     */
    private final OperationBuilder operationParser;

    /**
     * The Request mapping handler mapping.
     */
//	private final RequestMappingInfoHandlerMapping requestMappingHandlerMapping;

    /**
     * The Actuator provider.
     */
//	private final Optional<ActuatorProvider> actuatorProvider;

    /**
     * The Spring doc config properties.
     */
    private final NinjaDocConfigProperties ninjaDocConfigProperties;

    //	/**
//	 * The Spring security o auth 2 provider.
//	 */
//	private final Optional<SecurityOAuth2Provider> springSecurityOAuth2Provider;
    private final Injector injector;
    /**
     * The Grouped open api resources.
     */
    private Map<String, OpenApiResource> groupedOpenApiResources;

    /**
     * The Router function provider.
     */
//	private final Optional<RouterFunctionProvider> routerFunctionProvider;

    /**
     * The Repository rest resource provider.
     */
//	private final Optional<RepositoryRestResourceProvider> repositoryRestResourceProvider;

    /**
     * Instantiates a new Multiple open api resource.
     *
     * @param groupedOpenApis          the grouped open apis
     * @param defaultOpenAPIBuilder    the default open api builder
     * @param requestBuilder           the request builder
     * @param responseBuilder          the response builder
     * @param operationParser          the operation parser
     * @param ninjaDocConfigProperties the spring doc config properties
     * @param injector
     */
    @Inject
    public MultipleOpenApiResource(Set<GroupedOpenApi> groupedOpenApis,
                                   Provider<OpenAPIBuilder> defaultOpenAPIBuilder, AbstractRequestBuilder requestBuilder,
                                   GenericResponseBuilder responseBuilder, OperationBuilder operationParser,
                                   NinjaDocConfigProperties ninjaDocConfigProperties, Injector injector) {

        this.groupedOpenApis = new ArrayList<>(groupedOpenApis);
        this.defaultOpenAPIBuilder = defaultOpenAPIBuilder;
        this.requestBuilder = requestBuilder;
        this.responseBuilder = responseBuilder;
        this.operationParser = operationParser;
//		this.requestMappingHandlerMapping = requestMappingHandlerMapping;
//		this.actuatorProvider = actuatorProvider;
        this.ninjaDocConfigProperties = ninjaDocConfigProperties;
//		this.springSecurityOAuth2Provider = springSecurityOAuth2Provider;
//		this.routerFunctionProvider = routerFunctionProvider;
//		this.repositoryRestResourceProvider=repositoryRestResourceProvider;
        this.injector = injector;
    }

    @Override
    public void postInject() {
        this.groupedOpenApiResources = groupedOpenApis.stream()
                .collect(Collectors.toMap(GroupedOpenApi::getGroup, item ->
                        {
                            NinjaDocConfigProperties.GroupConfig groupConfig = new NinjaDocConfigProperties.GroupConfig(item.getGroup(), item.getPathsToMatch(), item.getPackagesToScan(), item.getPackagesToExclude(), item.getPathsToExclude());
                            ninjaDocConfigProperties.addGroupConfig(groupConfig);
                            return new OpenApiResource(item.getGroup(),
                                    defaultOpenAPIBuilder,
                                    requestBuilder,
                                    responseBuilder,
                                    operationParser,
                                    Optional.of(new HashSet<>(item.getOperationCustomizers())),
                                    Optional.of(new HashSet<>(item.getOpenApiCustomisers())),
                                    ninjaDocConfigProperties,
                                    injector
                            );
                        }
                ));
    }

    /**
     * Openapi json string.
     *
     * @param request the request
     * @param group   the group
     * @return the string
     * @throws JsonProcessingException the json processing exception
     */
    @Operation(hidden = true)
    public Result openapiJson(Context request, @PathParam("group") String group)
            throws JsonProcessingException {
        return getOpenApiResourceOrThrow(group).extractOpenapiJson(request, ninjaDocConfigProperties.getApiDocs().getPath() + DEFAULT_PATH_SEPARATOR + group);
    }

    @Operation(hidden = true)
    public Result openapiYaml(Context request, @PathParam("group") String group) throws JsonProcessingException {
        return getOpenApiResourceOrThrow(group).extractOpenapiYaml(request, ninjaDocConfigProperties.getApiDocs().getPath() + DEFAULT_PATH_SEPARATOR + group);
    }


    /**
     * Gets open api resource or throw.
     *
     * @param group the group
     * @return the open api resource or throw
     */
    private OpenApiResource getOpenApiResourceOrThrow(String group) {
        OpenApiResource openApiResource = groupedOpenApiResources.get(group);
        if (openApiResource == null) {
            throw new IllegalArgumentException("No OpenAPI resource found for group: " + group);
        }
        return openApiResource;
    }
}
