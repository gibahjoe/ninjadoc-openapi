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

package com.devappliance.ninjadoc;

import com.devappliance.ninjadoc.customizers.OpenApiBuilderCustomiser;
import com.devappliance.ninjadoc.wrappers.ApplicationContext;
import com.devappliance.ninjadoc.wrappers.HandlerMethod;
import com.google.inject.Inject;
import com.google.inject.Injector;
import io.swagger.v3.core.jackson.TypeNameResolver;
import io.swagger.v3.core.util.AnnotationsUtils;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.devappliance.ninjadoc.util.Constants.*;

/**
 * The type Open api builder.
 *
 * @author bnasslahsen
 */
public class OpenAPIBuilder {

    /**
     * The Context.
     */
    private final ApplicationContext context;

    /**
     * The Security parser.
     */
    private final SecurityParser securityParser;

    /**
     * The Mappings map.
     */
    private final Map<String, Object> mappingsMap = new HashMap<>();

    /**
     * The Springdoc tags.
     */
    private final Map<HandlerMethod, io.swagger.v3.oas.models.tags.Tag> springdocTags = new HashMap<>();

    /**
     * The Open api builder customisers.
     */
    private final Optional<Set<OpenApiBuilderCustomiser>> openApiBuilderCustomisers;

    /**
     * The Spring doc config properties.
     */
    private final NinjaDocConfigProperties ninjaDocConfigProperties;

    /**
     * The Open api.
     */
    private OpenAPI openAPI;

    /**
     * The Cached open api.
     */
    private OpenAPI cachedOpenAPI;

    /**
     * The Calculated open api.
     */
    private OpenAPI calculatedOpenAPI;

    /**
     * The Is servers present.
     */
    private boolean isServersPresent;

    /**
     * The Server base url.
     */
    private String serverBaseUrl;

    /**
     * Instantiates a new Open api builder.
     *
     * @param securityParser            the security parser
     * @param openAPI                   the open api
     * @param ninjaDocConfigProperties  the spring doc config properties
     * @param openApiBuilderCustomisers the open api builder customisers
     */
    @Inject
    public OpenAPIBuilder(SecurityParser securityParser, OpenAPI openAPI,
                          NinjaDocConfigProperties ninjaDocConfigProperties,
                          Optional<Set<OpenApiBuilderCustomiser>> openApiBuilderCustomisers, Injector injector) {
        if (openAPI != null) {
            this.openAPI = openAPI;
            if (this.openAPI.getComponents() == null)
                this.openAPI.setComponents(new Components());
            if (this.openAPI.getPaths() == null)
                this.openAPI.setPaths(new Paths());
            if (!CollectionUtils.isEmpty(this.openAPI.getServers()))
                this.isServersPresent = true;
        }
        this.context = ApplicationContext.from(injector);

        this.securityParser = securityParser;
        this.ninjaDocConfigProperties = ninjaDocConfigProperties;
        this.openApiBuilderCustomisers = openApiBuilderCustomisers;
        if (ninjaDocConfigProperties.isUseFqn())
            TypeNameResolver.std.setUseFqn(true);
    }

    /**
     * Split camel case string.
     *
     * @param str the str
     * @return the string
     */
    public static String splitCamelCase(String str) {
        return str.replaceAll(
                String.format(
                        "%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"),
                "-")
                .toLowerCase(Locale.ROOT);
    }

    /**
     * Build.
     */
    public void build() {
        Optional<OpenAPIDefinition> apiDef = getOpenAPIDefinition();
        if (openAPI == null) {
            this.calculatedOpenAPI = new OpenAPI();
            this.calculatedOpenAPI.setComponents(new Components());
            this.calculatedOpenAPI.setPaths(new Paths());
        } else
            this.calculatedOpenAPI = openAPI;

        if (apiDef.isPresent()) {
            buildOpenAPIWithOpenAPIDefinition(calculatedOpenAPI, apiDef.get());
        }
        // Set default info
        else if (calculatedOpenAPI.getInfo() == null) {
            Info infos = new Info().title(DEFAULT_TITLE).version(DEFAULT_VERSION).description("");
            calculatedOpenAPI.setInfo(infos);
        }
        // Set default mappings
        this.mappingsMap.putAll(context.getControllers());
//		this.mappingsMap.putAll(context.getBeansWithAnnotation(RequestMapping.class));
//		this.mappingsMap.putAll(context.getBeansWithAnnotation(Controller.class));

        // add security schemes
        this.calculateSecuritySchemes(calculatedOpenAPI.getComponents());
        openApiBuilderCustomisers.ifPresent(customisers -> customisers.forEach(customiser -> customiser.customise(this)));
    }

    /**
     * Update servers open api.
     *
     * @param openAPI the open api
     * @return the open api
     */
    public OpenAPI updateServers(OpenAPI openAPI) {
        if (!isServersPresent)        // default server value
        {
            Server server = new Server().url(serverBaseUrl).description(DEFAULT_SERVER_DESCRIPTION);
            List<Server> servers = new ArrayList<>();
            servers.add(server);
            openAPI.setServers(servers);
        }
        return openAPI;
    }

    /**
     * Sets servers present.
     *
     * @param serversPresent the servers present
     */
    public void setServersPresent(boolean serversPresent) {
        isServersPresent = serversPresent;
    }

    /**
     * Build tags operation.
     *
     * @param handlerMethod the handler method
     * @param operation     the operation
     * @param openAPI       the open api
     * @return the operation
     */
    public Operation buildTags(HandlerMethod handlerMethod, Operation operation, OpenAPI openAPI) {

        // class tags
        Set<Tags> tagsSet = AnnotatedElementUtils
                .findAllMergedAnnotations(handlerMethod.getBeanType(), Tags.class);
        Set<Tag> classTags = tagsSet.stream()
                .flatMap(x -> Stream.of(x.value())).collect(Collectors.toSet());
        classTags.addAll(AnnotatedElementUtils.findAllMergedAnnotations(handlerMethod.getBeanType(), Tag.class));

        // method tags
        tagsSet = AnnotatedElementUtils
                .findAllMergedAnnotations(handlerMethod.getMethod(), Tags.class);
        Set<Tag> methodTags = tagsSet.stream()
                .flatMap(x -> Stream.of(x.value())).collect(Collectors.toSet());
        methodTags.addAll(AnnotatedElementUtils.findAllMergedAnnotations(handlerMethod.getMethod(), Tag.class));


        List<Tag> allTags = new ArrayList<>();
        Set<String> tagsStr = new HashSet<>();

        if (!CollectionUtils.isEmpty(methodTags)) {
            tagsStr.addAll(methodTags.stream().map(Tag::name).collect(Collectors.toSet()));
            allTags.addAll(methodTags);
        }

        if (!CollectionUtils.isEmpty(classTags)) {
            tagsStr.addAll(classTags.stream().map(Tag::name).collect(Collectors.toSet()));
            allTags.addAll(classTags);
        }

        if (springdocTags.containsKey(handlerMethod)) {
            io.swagger.v3.oas.models.tags.Tag tag = springdocTags.get(handlerMethod);
            tagsStr.add(tag.getName());
            if (openAPI.getTags() == null || !openAPI.getTags().contains(tag)) {
                openAPI.addTagsItem(tag);
            }
        }

        Optional<Set<io.swagger.v3.oas.models.tags.Tag>> tags = AnnotationsUtils
                .getTags(allTags.toArray(new Tag[0]), true);

        if (tags.isPresent()) {
            Set<io.swagger.v3.oas.models.tags.Tag> tagSet = tags.get();
            // Existing tags
            List<io.swagger.v3.oas.models.tags.Tag> openApiTags = openAPI.getTags();
            if (!CollectionUtils.isEmpty(openApiTags))
                tagSet.addAll(openApiTags);
            openAPI.setTags(new ArrayList<>(tagSet));
        }

//         Handle SecurityRequirement at operation level
        io.swagger.v3.oas.annotations.security.SecurityRequirement[] securityRequirements = securityParser
                .getSecurityRequirements(handlerMethod);
        if (securityRequirements != null) {
            if (securityRequirements.length == 0)
                operation.setSecurity(Collections.emptyList());
            else
                securityParser.buildSecurityRequirement(securityRequirements, operation);
        }
        if (!CollectionUtils.isEmpty(tagsStr))
            operation.setTags(new ArrayList<>(tagsStr));


        if (isAutoTagClasses(operation))
            operation.addTagsItem(splitCamelCase(handlerMethod.getBeanType().getSimpleName()));

        return operation;
    }

    /**
     * Resolve properties schema.
     *
     * @param schema                the schema
     * @param propertyResolverUtils the property resolver utils
     * @return the schema
     */
    @SuppressWarnings("unchecked")
    public Schema resolveProperties(Schema schema, PropertyResolverUtils propertyResolverUtils) {
        resolveProperty(schema::getName, schema::name, propertyResolverUtils);
        resolveProperty(schema::getTitle, schema::title, propertyResolverUtils);
        resolveProperty(schema::getDescription, schema::description, propertyResolverUtils);

        Map<String, Schema> properties = schema.getProperties();
        if (!CollectionUtils.isEmpty(properties)) {
            Map<String, Schema> resolvedSchemas = properties.entrySet().stream().map(es -> {
                es.setValue(resolveProperties(es.getValue(), propertyResolverUtils));
                return es;
            }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            schema.setProperties(resolvedSchemas);
        }

        return schema;
    }

    /**
     * Sets server base url.
     *
     * @param serverBaseUrl the server base url
     */
    public void setServerBaseUrl(String serverBaseUrl) {
        this.serverBaseUrl = serverBaseUrl;
    }

    /**
     * Gets open api definition.
     *
     * @return the open api definition
     */
    private Optional<OpenAPIDefinition> getOpenAPIDefinition() {
        // Look for OpenAPIDefinition in a spring managed bean
//        Object instance = context.getInstance(Key.get(Object.class, OpenAPIDefinition.class));
//        if (instance == null) {
//        }
//        OpenAPIDefinition apiDef = null;
//        Class<?> objClz = instance.getClass();
//        apiDef = AnnotatedElementUtils.findMergedAnnotation(objClz, OpenAPIDefinition.class);
//
//        return Optional.ofNullable(apiDef);
        return Optional.empty();
    }

    /**
     * Build open api with open api definition.
     *
     * @param openAPI the open api
     * @param apiDef  the api def
     */
    private void buildOpenAPIWithOpenAPIDefinition(OpenAPI openAPI, OpenAPIDefinition apiDef) {
        // info
        AnnotationsUtils.getInfo(apiDef.info()).map(this::resolveProperties).ifPresent(openAPI::setInfo);
        // OpenApiDefinition security requirements
        securityParser.getSecurityRequirements(apiDef.security()).ifPresent(openAPI::setSecurity);
        // OpenApiDefinition external docs
        AnnotationsUtils.getExternalDocumentation(apiDef.externalDocs()).ifPresent(openAPI::setExternalDocs);
        // OpenApiDefinition tags
        AnnotationsUtils.getTags(apiDef.tags(), false).ifPresent(tags -> openAPI.setTags(new ArrayList<>(tags)));
        // OpenApiDefinition servers
        Optional<List<Server>> optionalServers = AnnotationsUtils.getServers(apiDef.servers());
        if (optionalServers.isPresent()) {
            openAPI.setServers(optionalServers.get());
            this.isServersPresent = true;
        }
        // OpenApiDefinition extensions
        if (apiDef.extensions().length > 0) {
            openAPI.setExtensions(AnnotationsUtils.getExtensions(apiDef.extensions()));
        }
    }

    /**
     * Resolve properties info.
     *
     * @param info the info
     * @return the info
     */
    private Info resolveProperties(Info info) {
        PropertyResolverUtils propertyResolverUtils = context.getBean(PropertyResolverUtils.class);
        resolveProperty(info::getTitle, info::title, propertyResolverUtils);
        resolveProperty(info::getDescription, info::description, propertyResolverUtils);
        resolveProperty(info::getVersion, info::version, propertyResolverUtils);
        resolveProperty(info::getTermsOfService, info::termsOfService, propertyResolverUtils);

        License license = info.getLicense();
        if (license != null) {
            resolveProperty(license::getName, license::name, propertyResolverUtils);
            resolveProperty(license::getUrl, license::url, propertyResolverUtils);
        }

        Contact contact = info.getContact();
        if (contact != null) {
            resolveProperty(contact::getName, contact::name, propertyResolverUtils);
            resolveProperty(contact::getEmail, contact::email, propertyResolverUtils);
            resolveProperty(contact::getUrl, contact::url, propertyResolverUtils);
        }
        return info;
    }

    /**
     * Resolve property.
     *
     * @param getProperty           the get property
     * @param setProperty           the set property
     * @param propertyResolverUtils the property resolver utils
     */
    private void resolveProperty(Supplier<String> getProperty, Consumer<String> setProperty,
                                 PropertyResolverUtils propertyResolverUtils) {
        String value = getProperty.get();
        if (StringUtils.isNotBlank(value)) {
            setProperty.accept(propertyResolverUtils.resolve(value));
        }
    }

    /**
     * Calculate security schemes.
     *
     * @param components the components
     */
    private void calculateSecuritySchemes(Components components) {
        // Look for SecurityScheme in a spring managed bean
        Map<String, Object> securitySchemeBeans = context
                .getControllerBeansWithAnnotation(io.swagger.v3.oas.annotations.security.SecurityScheme.class);
        if (securitySchemeBeans.size() > 0) {
            for (Map.Entry<String, Object> entry : securitySchemeBeans.entrySet()) {
                Class<?> objClz = entry.getValue().getClass();
                Set<io.swagger.v3.oas.annotations.security.SecurityScheme> apiSecurityScheme = AnnotatedElementUtils.findMergedRepeatableAnnotations(objClz, io.swagger.v3.oas.annotations.security.SecurityScheme.class);
                this.addSecurityScheme(apiSecurityScheme, components);
            }
        }

        // Look for SecurityScheme in the spring classpath
//        else {
//            ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(
//                    false);
//            scanner.addIncludeFilter(
//                    new AnnotationTypeFilter(io.swagger.v3.oas.annotations.security.SecurityScheme.class));
//            if (AutoConfigurationPackages.has(context)) {
//                List<String> packagesToScan = AutoConfigurationPackages.get(context);
//                Set<io.swagger.v3.oas.annotations.security.SecurityScheme> apiSecurityScheme = getSecuritySchemesClasses(
//                        scanner, packagesToScan);
//                this.addSecurityScheme(apiSecurityScheme, components);
//            }
//
//        }
    }

    /**
     * Add security scheme.
     *
     * @param apiSecurityScheme the api security scheme
     * @param components        the components
     */
    private void addSecurityScheme(Set<io.swagger.v3.oas.annotations.security.SecurityScheme> apiSecurityScheme,
                                   Components components) {
        for (io.swagger.v3.oas.annotations.security.SecurityScheme securitySchemeAnnotation : apiSecurityScheme) {
            Optional<SecuritySchemePair> securityScheme = securityParser.getSecurityScheme(securitySchemeAnnotation);
            if (securityScheme.isPresent()) {
                Map<String, SecurityScheme> securitySchemeMap = new HashMap<>();
                if (StringUtils.isNotBlank(securityScheme.get().getKey())) {
                    securitySchemeMap.put(securityScheme.get().getKey(), securityScheme.get().getSecurityScheme());
                    if (!CollectionUtils.isEmpty(components.getSecuritySchemes())) {
                        components.getSecuritySchemes().putAll(securitySchemeMap);
                    } else {
                        components.setSecuritySchemes(securitySchemeMap);
                    }
                }
            }
        }
    }

    /**
     * Gets api def class.
     *
     * @param scanner        the scanner
     * @param packagesToScan the packages to scan
     * @return the api def class
     */
//    private OpenAPIDefinition getApiDefClass(ClassPathScanningCandidateComponentProvider scanner,
//                                             List<String> packagesToScan) {
//        for (String pack : packagesToScan) {
//            for (BeanDefinition bd : scanner.findCandidateComponents(pack)) {
//                // first one found is ok
//                try {
//                    return AnnotationUtils.findAnnotation(Class.forName(bd.getBeanClassName()),
//                            OpenAPIDefinition.class);
//                } catch (ClassNotFoundException e) {
//                    LOGGER.error("Class Not Found in classpath : {}", e.getMessage());
//                }
//            }
//        }
//        return null;
//    }

    /**
     * Is auto tag classes boolean.
     *
     * @param operation the operation
     * @return the boolean
     */
    public boolean isAutoTagClasses(Operation operation) {
        return CollectionUtils.isEmpty(operation.getTags()) && ninjaDocConfigProperties.isAutoTagClasses();
    }

    /**
     * Gets security schemes classes.
     *
     * @param scanner        the scanner
     * @param packagesToScan the packages to scan
     * @return the security schemes classes
     */
//    private Set<io.swagger.v3.oas.annotations.security.SecurityScheme> getSecuritySchemesClasses(
//            ClassPathScanningCandidateComponentProvider scanner, List<String> packagesToScan) {
//        Set<io.swagger.v3.oas.annotations.security.SecurityScheme> apiSecurityScheme = new HashSet<>();
//        for (String pack : packagesToScan) {
//            for (BeanDefinition bd : scanner.findCandidateComponents(pack)) {
//                try {
//                    apiSecurityScheme.add(AnnotationUtils.findAnnotation(Class.forName(bd.getBeanClassName()),
//                            io.swagger.v3.oas.annotations.security.SecurityScheme.class));
//                } catch (ClassNotFoundException e) {
//                    LOGGER.error("Class Not Found in classpath : {}", e.getMessage());
//                }
//            }
//        }
//        return apiSecurityScheme;
//    }

    /**
     * Add tag.
     *
     * @param handlerMethods the handler methods
     * @param tag            the tag
     */
    public void addTag(Set<HandlerMethod> handlerMethods, io.swagger.v3.oas.models.tags.Tag tag) {
        handlerMethods.forEach(handlerMethod -> springdocTags.put(handlerMethod, tag));
    }

    /**
     * Gets mappings map.
     *
     * @return the mappings map
     */
    public Map<String, Object> getMappingsMap() {
        return this.mappingsMap;
    }

    /**
     * Add mappings.
     *
     * @param mappings the mappings
     */
    public void addMappings(Map<String, Object> mappings) {
        this.mappingsMap.putAll(mappings);
    }

    /**
     * Gets controller advice map.
     *
     * @return the controller advice map
     */
//    public Map<String, Object> getControllerAdviceMap() {
//        Map<String, Object> controllerAdviceMap = context.getBeansWithAnnotation(ControllerAdvice.class);
//        return Stream.of(controllerAdviceMap).flatMap(mapEl -> mapEl.entrySet().stream()).filter(
//                controller -> (AnnotationUtils.findAnnotation(controller.getValue().getClass(), Hidden.class) == null))
//                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a1, a2) -> a1));
//    }

    /**
     * Gets cached open api.
     *
     * @return the cached open api
     */
    public OpenAPI getCachedOpenAPI() {
        return cachedOpenAPI;
    }

    /**
     * Sets cached open api.
     *
     * @param cachedOpenAPI the cached open api
     */
    public void setCachedOpenAPI(OpenAPI cachedOpenAPI) {
        this.cachedOpenAPI = cachedOpenAPI;
    }

    /**
     * Gets calculated open api.
     *
     * @return the calculated open api
     */
    public OpenAPI getCalculatedOpenAPI() {
        return calculatedOpenAPI;
    }

    /**
     * Reset calculated open api.
     */
    public void resetCalculatedOpenAPI() {
        this.calculatedOpenAPI = null;
    }

    /**
     * Gets context.
     *
     * @return the context
     */
    public ApplicationContext getContext() {
        return context;
    }
}
