
package com.devappliance.ninjadoc;

import com.devappliance.ninjadoc.customizers.ParameterCustomizer;
import com.devappliance.ninjadoc.util.Constants;
import com.devappliance.ninjadoc.wrappers.HandlerMethod;
import com.devappliance.ninjadoc.wrappers.RequestMethod;
import com.devappliance.ninjadoc.wrappers.ValueConstants;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import ninja.Context;
import ninja.params.Header;
import ninja.params.Param;
import ninja.params.PathParam;
import ninja.params.SessionParam;
import ninja.validation.Validation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.devappliance.ninjadoc.converters.SchemaPropertyDeprecatingConverter.containsDeprecatedAnnotation;
import static com.devappliance.ninjadoc.util.Constants.OPENAPI_ARRAY_TYPE;
import static com.devappliance.ninjadoc.util.Constants.OPENAPI_STRING_TYPE;

public abstract class AbstractRequestBuilder {

    /**
     * The constant PARAM_TYPES_TO_IGNORE.
     */
    private static final List<Class<?>> PARAM_TYPES_TO_IGNORE = new ArrayList<>();

    /**
     * The constant ANNOTATIONS_FOR_REQUIRED.
     */
// using string litterals to support both validation-api v1 and v2
    private static final String[] ANNOTATIONS_FOR_REQUIRED = {NotNull.class.getName(), "javax.validation.constraints.NotBlank", "javax.validation.constraints.NotEmpty"};

    /**
     * The constant POSITIVE_OR_ZERO.
     */
    private static final String POSITIVE_OR_ZERO = "javax.validation.constraints.PositiveOrZero";

    /**
     * The constant NEGATIVE_OR_ZERO.
     */
    private static final String NEGATIVE_OR_ZERO = "javax.validation.constraints.NegativeOrZero";

    static {
        PARAM_TYPES_TO_IGNORE.add(Context.class);
        PARAM_TYPES_TO_IGNORE.add(Validation.class);
        PARAM_TYPES_TO_IGNORE.add(java.security.Principal.class);
        PARAM_TYPES_TO_IGNORE.add(java.util.Locale.class);
        PARAM_TYPES_TO_IGNORE.add(java.util.TimeZone.class);
        PARAM_TYPES_TO_IGNORE.add(java.io.InputStream.class);
        PARAM_TYPES_TO_IGNORE.add(java.time.ZoneId.class);
        PARAM_TYPES_TO_IGNORE.add(java.io.Reader.class);
        PARAM_TYPES_TO_IGNORE.add(java.io.OutputStream.class);
        PARAM_TYPES_TO_IGNORE.add(java.io.Writer.class);
        PARAM_TYPES_TO_IGNORE.add(Map.class);
    }

    /**
     * The Parameter builder.
     */
    private final GenericParameterBuilder parameterBuilder;

    /**
     * The Request body builder.
     */
    private final RequestBodyBuilder requestBodyBuilder;

    /**
     * The Operation builder.
     */
    private final OperationBuilder operationBuilder;

    /**
     * The Local spring doc parameter name discoverer.
     */
    private final LocalVariableTableParameterNameDiscoverer localSpringDocParameterNameDiscoverer;

    /**
     * The Parameter customizers.
     */
    private final Optional<Set<ParameterCustomizer>> parameterCustomizers;

    /**
     * Instantiates a new Abstract request builder.
     *
     * @param parameterBuilder                      the parameter builder
     * @param requestBodyBuilder                    the request body builder
     * @param operationBuilder                      the operation builder
     * @param parameterCustomizers                  the parameter customizers
     * @param localSpringDocParameterNameDiscoverer the local spring doc parameter name discoverer
     */
    protected AbstractRequestBuilder(GenericParameterBuilder parameterBuilder, RequestBodyBuilder requestBodyBuilder,
                                     OperationBuilder operationBuilder, Optional<Set<ParameterCustomizer>> parameterCustomizers,
                                     LocalVariableTableParameterNameDiscoverer localSpringDocParameterNameDiscoverer) {
        super();
        this.parameterBuilder = parameterBuilder;
        this.requestBodyBuilder = requestBodyBuilder;
        this.operationBuilder = operationBuilder;
        parameterCustomizers.ifPresent(customizers -> customizers.removeIf(Objects::isNull));
        this.parameterCustomizers = parameterCustomizers;
        this.localSpringDocParameterNameDiscoverer = localSpringDocParameterNameDiscoverer;
    }

    /**
     * Add request wrapper to ignore.
     *
     * @param classes the classes
     */
    public static void addRequestWrapperToIgnore(Class<?>... classes) {
        PARAM_TYPES_TO_IGNORE.addAll(Arrays.asList(classes));
    }

    /**
     * Remove request wrapper to ignore.
     *
     * @param classes the classes
     */
    public static void removeRequestWrapperToIgnore(Class<?>... classes) {
        List<Class<?>> classesToIgnore = Arrays.asList(classes);
        if (PARAM_TYPES_TO_IGNORE.containsAll(classesToIgnore))
            PARAM_TYPES_TO_IGNORE.removeAll(Arrays.asList(classes));
    }

    /**
     * Is request type to ignore boolean.
     *
     * @param rawClass the raw class
     * @return the boolean
     */
    public static boolean isRequestTypeToIgnore(Class<?> rawClass) {
        return PARAM_TYPES_TO_IGNORE.stream().anyMatch(clazz -> clazz.isAssignableFrom(rawClass));
    }

    /**
     * Gets headers.
     *
     * @param methodAttributes the method attributes
     * @param map              the map
     * @return the headers
     */
    @SuppressWarnings("unchecked")
    public static Collection<Parameter> getHeaders(MethodAttributes methodAttributes, Map<String, Parameter> map) {
        for (Map.Entry<String, String> entry : methodAttributes.getHeaders().entrySet()) {
            Parameter parameter = new Parameter().in(ParameterIn.HEADER.toString()).name(entry.getKey()).schema(new StringSchema().addEnumItem(entry.getValue()));
            if (map.containsKey(entry.getKey())) {
                parameter = map.get(entry.getKey());
                parameter.getSchema().addEnumItemObject(entry.getValue());
                parameter.setSchema(parameter.getSchema());
            }
            map.put(entry.getKey(), parameter);
        }
        return map.values();
    }

    /**
     * Build operation.
     *
     * @param handlerMethod    the handler method
     * @param requestMethod    the request method
     * @param operation        the operation
     * @param methodAttributes the method attributes
     * @param openAPI          the open api
     * @return the operation
     */
    public Operation build(HandlerMethod handlerMethod, RequestMethod requestMethod,
                           Operation operation, MethodAttributes methodAttributes, OpenAPI openAPI) {
        // Documentation
        String operationId = operationBuilder.getOperationId(handlerMethod.getMethod().getName(),
                operation.getOperationId(), openAPI);
        operation.setOperationId(operationId);
        // requests
        String[] pNames = this.localSpringDocParameterNameDiscoverer.getParameterNames(handlerMethod.getMethod());
        MethodParameter[] parameters = handlerMethod.getMethodParameters();
        String[] reflectionParametersNames = Arrays.stream(handlerMethod.getMethod().getParameters()).map(java.lang.reflect.Parameter::getName).toArray(String[]::new);
        if (pNames == null || Arrays.stream(pNames).anyMatch(Objects::isNull))
            pNames = reflectionParametersNames;
        parameters = DelegatingMethodParameter.customize(pNames, parameters);
        RequestBodyInfo requestBodyInfo = new RequestBodyInfo();
        List<Parameter> operationParameters = (operation.getParameters() != null) ? operation.getParameters() : new ArrayList<>();
        Map<String, io.swagger.v3.oas.annotations.Parameter> parametersDocMap = getApiParameters(handlerMethod.getMethod());
        Components components = openAPI.getComponents();

        for (MethodParameter methodParameter : parameters) {
            // check if query param
            Parameter parameter = null;
            io.swagger.v3.oas.annotations.Parameter parameterDoc = AnnotatedElementUtils.findMergedAnnotation(
                    AnnotatedElementUtils.forAnnotations(methodParameter.getParameterAnnotations()),
                    io.swagger.v3.oas.annotations.Parameter.class);

            final String pName = methodParameter.getParameterName();
            ParameterInfo parameterInfo = new ParameterInfo(pName, methodParameter);

            if (parameterDoc == null)
                parameterDoc = parametersDocMap.get(parameterInfo.getpName());
            // use documentation as reference
            if (parameterDoc != null) {
                if (parameterDoc.hidden() || parameterDoc.schema().hidden())
                    continue;
                parameter = parameterBuilder.buildParameterFromDoc(parameterDoc, components, methodAttributes.getJsonViewAnnotation());
                parameterInfo.setParameterModel(parameter);
            }

            if (!isParamToIgnore(methodParameter)) {
                parameter = buildParams(parameterInfo, components, requestMethod, methodAttributes.getJsonViewAnnotation());
                // Merge with the operation parameters
                parameter = GenericParameterBuilder.mergeParameter(operationParameters, parameter);
                List<Annotation> parameterAnnotations = Arrays.asList(methodParameter.getParameterAnnotations());
                if (isValidParameter(parameter))
                    applyBeanValidatorAnnotations(parameter, parameterAnnotations);
                else if (!RequestMethod.GET.equals(requestMethod)) {
                    if (operation.getRequestBody() != null)
                        requestBodyInfo.setRequestBody(operation.getRequestBody());
                    requestBodyBuilder.calculateRequestBodyInfo(components, methodAttributes,
                            parameterInfo, requestBodyInfo);
                    applyBeanValidatorAnnotations(requestBodyInfo.getRequestBody(), parameterAnnotations, methodParameter.isOptional());
                }
                customiseParameter(parameter, parameterInfo);
            }
        }

        LinkedHashMap<String, Parameter> map = getParameterLinkedHashMap(components, methodAttributes, operationParameters, parametersDocMap);
        setParams(operation, new ArrayList<>(map.values()), requestBodyInfo);
        if (StringUtils.isBlank(operation.getSummary())) {
            operation.setSummary(handlerMethod.getMethod().getName());
        }
        return operation;
    }

    /**
     * Gets parameter linked hash map.
     *
     * @param components          the components
     * @param methodAttributes    the method attributes
     * @param operationParameters the operation parameters
     * @param parametersDocMap    the parameters doc map
     * @return the parameter linked hash map
     */
    private LinkedHashMap<String, Parameter> getParameterLinkedHashMap(Components components, MethodAttributes methodAttributes, List<Parameter> operationParameters, Map<String, io.swagger.v3.oas.annotations.Parameter> parametersDocMap) {
        LinkedHashMap<String, Parameter> map = operationParameters.stream()
                .collect(Collectors.toMap(
                        parameter -> parameter.getName() != null ? parameter.getName() : Integer.toString(parameter.hashCode()),
                        parameter -> parameter,
                        (u, v) -> {
                            throw new IllegalStateException(String.format("Duplicate key %s", u));
                        },
                        LinkedHashMap::new
                ));

        for (Map.Entry<String, io.swagger.v3.oas.annotations.Parameter> entry : parametersDocMap.entrySet()) {
            if (entry.getKey() != null && !map.containsKey(entry.getKey()) && !entry.getValue().hidden()) {
                //Convert
                Parameter parameter = parameterBuilder.buildParameterFromDoc(entry.getValue(), components,
                        methodAttributes.getJsonViewAnnotation());
                map.put(entry.getKey(), parameter);
            }
        }

        getHeaders(methodAttributes, map);
        return map;
    }

    /**
     * Customise parameter parameter.
     *
     * @param parameter     the parameter
     * @param parameterInfo the parameter info
     * @return the parameter
     */
    protected Parameter customiseParameter(Parameter parameter, ParameterInfo parameterInfo) {
        parameterCustomizers.ifPresent(customizers -> customizers.forEach(customizer -> customizer.customize(parameter, parameterInfo.getMethodParameter())));
        return parameter;
    }

    /**
     * Is param to ignore boolean.
     *
     * @param parameter the parameter
     * @return the boolean
     */
    public boolean isParamToIgnore(MethodParameter parameter) {
        if (parameterBuilder.isAnnotationToIgnore(parameter))
            return true;
        if ((parameter.getParameterAnnotation(PathParam.class) != null)
                || (parameter.getParameterAnnotation(Param.class) != null))
            return false;
        return isRequestTypeToIgnore(parameter.getParameterType());
    }

    /**
     * Sets params.
     *
     * @param operation           the operation
     * @param operationParameters the operation parameters
     * @param requestBodyInfo     the request body info
     */
    private void setParams(Operation operation, List<Parameter> operationParameters, RequestBodyInfo requestBodyInfo) {
        if (!CollectionUtils.isEmpty(operationParameters))
            operation.setParameters(operationParameters);
        if (requestBodyInfo.getRequestBody() != null)
            operation.setRequestBody(requestBodyInfo.getRequestBody());
    }

    /**
     * Is valid parameter boolean.
     *
     * @param parameter the parameter
     * @return the boolean
     */
    public boolean isValidParameter(Parameter parameter) {
        return parameter != null && (parameter.getName() != null || parameter.get$ref() != null);
    }

    /**
     * Builds an OpenApi {@link Parameter} for a parameter in a controller method
     *
     * @param parameterInfo {@link ParameterInfo} An object holding information about this parameter
     * @param components    The OAPI component
     * @param requestMethod
     * @param jsonView
     * @return
     */
    public Parameter buildParams(ParameterInfo parameterInfo, Components components,
                                 RequestMethod requestMethod, JsonView jsonView) {
        MethodParameter methodParameter = parameterInfo.getMethodParameter();
        Header requestHeader = parameterInfo.getRequestHeader();
        Param requestParam = parameterInfo.getRequestParam();
        PathParam pathVar = parameterInfo.getPathVar();
        SessionParam cookieValue = parameterInfo.getCookieValue();

        RequestInfo requestInfo;

        if (requestHeader != null) {
            requestInfo = new RequestInfo(ParameterIn.HEADER.toString(), parameterInfo.getpName(), false,
                    null);
            return buildParam(parameterInfo, components, requestInfo, jsonView);

        } else if (requestParam != null && !parameterBuilder.isFile(parameterInfo.getMethodParameter())) {
            requestInfo = new RequestInfo(ParameterIn.QUERY.toString(), parameterInfo.getpName(), !methodParameter.isOptional(),
                    null);
            return buildParam(parameterInfo, components, requestInfo, jsonView);
        } else if (pathVar != null) {
            requestInfo = new RequestInfo(ParameterIn.PATH.toString(), parameterInfo.getpName(), !methodParameter.isOptional(), null);
            return buildParam(parameterInfo, components, requestInfo, jsonView);
        } else if (cookieValue != null) {
            requestInfo = new RequestInfo(ParameterIn.COOKIE.toString(), parameterInfo.getpName(), !methodParameter.isOptional(),
                    null);
            return buildParam(parameterInfo, components, requestInfo, jsonView);
        }
        // By default
        DelegatingMethodParameter delegatingMethodParameter = (DelegatingMethodParameter) methodParameter;
        if (RequestMethod.GET.equals(requestMethod)
                || (parameterInfo.getParameterModel() != null && (ParameterIn.PATH.toString().equals(parameterInfo.getParameterModel().getIn())))
                || delegatingMethodParameter.isParameterObject())
            return this.buildParam(Constants.QUERY_PARAM, components, parameterInfo, !methodParameter.isOptional(), null, jsonView);

        return null;
    }

    /**
     * Build param parameter.
     *
     * @param parameterInfo the parameter info
     * @param components    the components
     * @param requestInfo   the request info
     * @param jsonView      the json view
     * @return the parameter
     */
    private Parameter buildParam(ParameterInfo parameterInfo, Components components, RequestInfo requestInfo,
                                 JsonView jsonView) {
        Parameter parameter;
        String pName = parameterInfo.getpName();
        String name = StringUtils.isBlank(requestInfo.value()) ? pName : requestInfo.value();
        parameterInfo.setpName(name);

        if (!ValueConstants.DEFAULT_NONE.equals(requestInfo.defaultValue()))
            parameter = this.buildParam(requestInfo.type(), components, parameterInfo, false,
                    requestInfo.defaultValue(), jsonView);
        else
            parameter = this.buildParam(requestInfo.type(), components, parameterInfo, requestInfo.required(), null,
                    jsonView);
        return parameter;
    }

    /**
     * Build param parameter.
     *
     * @param in            the in
     * @param components    the components
     * @param parameterInfo the parameter info
     * @param required      the required
     * @param defaultValue  the default value
     * @param jsonView      the json view
     * @return the parameter
     */
    private Parameter buildParam(String in, Components components, ParameterInfo parameterInfo, Boolean required,
                                 String defaultValue, JsonView jsonView) {
        Parameter parameter = parameterInfo.getParameterModel();
        String name = parameterInfo.getpName();

        if (parameter == null) {
            parameter = new Parameter();
            parameterInfo.setParameterModel(parameter);
        }

        if (StringUtils.isBlank(parameter.getName()))
            parameter.setName(name);

        if (StringUtils.isBlank(parameter.getIn()))
            parameter.setIn(in);

        if (required != null && parameter.getRequired() == null)
            parameter.setRequired(required);

        if (containsDeprecatedAnnotation(parameterInfo.getMethodParameter().getParameterAnnotations()))
            parameter.setDeprecated(true);

        if (parameter.getSchema() == null) {
            Schema<?> schema = parameterBuilder.calculateSchema(components, parameterInfo, null,
                    jsonView);
            if (defaultValue != null)
                schema.setDefault(defaultValue);
            parameter.setSchema(schema);
        }
        return parameter;
    }

    /**
     * Apply bean validator annotations.
     *
     * @param parameter   the parameter
     * @param annotations the annotations
     */
    public void applyBeanValidatorAnnotations(final Parameter parameter, final List<Annotation> annotations) {
        Map<String, Annotation> annos = new HashMap<>();
        if (annotations != null)
            annotations.forEach(annotation -> annos.put(annotation.annotationType().getName(), annotation));
        boolean annotationExists = Arrays.stream(ANNOTATIONS_FOR_REQUIRED).anyMatch(annos::containsKey);
        if (annotationExists)
            parameter.setRequired(true);
        Schema<?> schema = parameter.getSchema();
        applyValidationsToSchema(annos, schema);
    }

    /**
     * Apply bean validator annotations.
     *
     * @param requestBody the request body
     * @param annotations the annotations
     * @param isOptional  the is optional
     */
    public void applyBeanValidatorAnnotations(final RequestBody requestBody, final List<Annotation> annotations, boolean isOptional) {
        Map<String, Annotation> annos = new HashMap<>();
        boolean requestBodyRequired = false;
        if (!CollectionUtils.isEmpty(annotations)) {
            annotations.forEach(annotation -> annos.put(annotation.annotationType().getName(), annotation));
            requestBodyRequired = true;
        }
        boolean validationExists = Arrays.stream(ANNOTATIONS_FOR_REQUIRED).anyMatch(annos::containsKey);

        if (validationExists || (!isOptional && requestBodyRequired))
            requestBody.setRequired(true);
        Content content = requestBody.getContent();
        for (MediaType mediaType : content.values()) {
            Schema<?> schema = mediaType.getSchema();
            applyValidationsToSchema(annos, schema);
        }
    }

    /**
     * Calculate size.
     *
     * @param annos  the annos
     * @param schema the schema
     */
    private void calculateSize(Map<String, Annotation> annos, Schema<?> schema) {
        if (annos.containsKey(Size.class.getName())) {
            Size size = (Size) annos.get(Size.class.getName());
            if (OPENAPI_ARRAY_TYPE.equals(schema.getType())) {
                schema.setMinItems(size.min());
                schema.setMaxItems(size.max());
            } else if (OPENAPI_STRING_TYPE.equals(schema.getType())) {
                schema.setMinLength(size.min());
                schema.setMaxLength(size.max());
            }
        }
    }

    /**
     * Gets request body builder.
     *
     * @return the request body builder
     */
    public RequestBodyBuilder getRequestBodyBuilder() {
        return requestBodyBuilder;
    }

    /**
     * Gets api parameters.
     *
     * @param method the method
     * @return the api parameters
     */
    private Map<String, io.swagger.v3.oas.annotations.Parameter> getApiParameters(Method method) {
        Class<?> declaringClass = method.getDeclaringClass();

        Set<io.swagger.v3.oas.annotations.Parameters> apiParametersDoc = AnnotatedElementUtils
                .findAllMergedAnnotations(method, io.swagger.v3.oas.annotations.Parameters.class);
        LinkedHashMap<String, io.swagger.v3.oas.annotations.Parameter> apiParametersMap = apiParametersDoc.stream()
                .flatMap(x -> Stream.of(x.value())).collect(Collectors.toMap(io.swagger.v3.oas.annotations.Parameter::name, x -> x, (e1, e2) -> e2,
                        LinkedHashMap::new));

        Set<io.swagger.v3.oas.annotations.Parameters> apiParametersDocDeclaringClass = AnnotatedElementUtils
                .findAllMergedAnnotations(declaringClass, io.swagger.v3.oas.annotations.Parameters.class);
        LinkedHashMap<String, io.swagger.v3.oas.annotations.Parameter> apiParametersDocDeclaringClassMap = apiParametersDocDeclaringClass.stream()
                .flatMap(x -> Stream.of(x.value())).collect(Collectors.toMap(io.swagger.v3.oas.annotations.Parameter::name, x -> x, (e1, e2) -> e2,
                        LinkedHashMap::new));
        apiParametersMap.putAll(apiParametersDocDeclaringClassMap);

        Set<io.swagger.v3.oas.annotations.Parameter> apiParameterDoc = AnnotatedElementUtils
                .findAllMergedAnnotations(method, io.swagger.v3.oas.annotations.Parameter.class);
        LinkedHashMap<String, io.swagger.v3.oas.annotations.Parameter> apiParameterDocMap = apiParameterDoc.stream()
                .collect(Collectors.toMap(io.swagger.v3.oas.annotations.Parameter::name, x -> x, (e1, e2) -> e2,
                        LinkedHashMap::new));
        apiParametersMap.putAll(apiParameterDocMap);

        Set<io.swagger.v3.oas.annotations.Parameter> apiParameterDocDeclaringClass = AnnotatedElementUtils
                .findAllMergedAnnotations(declaringClass, io.swagger.v3.oas.annotations.Parameter.class);
        LinkedHashMap<String, io.swagger.v3.oas.annotations.Parameter> apiParameterDocDeclaringClassMap = apiParameterDocDeclaringClass.stream()
                .collect(Collectors.toMap(io.swagger.v3.oas.annotations.Parameter::name, x -> x, (e1, e2) -> e2,
                        LinkedHashMap::new));
        apiParametersMap.putAll(apiParameterDocDeclaringClassMap);

        return apiParametersMap;
    }

    /**
     * Apply validations to schema.
     *
     * @param annos  the annos
     * @param schema the schema
     */
    private void applyValidationsToSchema(Map<String, Annotation> annos, Schema<?> schema) {
        if (annos.containsKey(Min.class.getName())) {
            Min min = (Min) annos.get(Min.class.getName());
            schema.setMinimum(BigDecimal.valueOf(min.value()));
        }
        if (annos.containsKey(Max.class.getName())) {
            Max max = (Max) annos.get(Max.class.getName());
            schema.setMaximum(BigDecimal.valueOf(max.value()));
        }
        calculateSize(annos, schema);
        if (annos.containsKey(DecimalMin.class.getName())) {
            DecimalMin min = (DecimalMin) annos.get(DecimalMin.class.getName());
            if (min.inclusive())
                schema.setMinimum(BigDecimal.valueOf(Double.parseDouble(min.value())));
            else
                schema.setExclusiveMinimum(!min.inclusive());
        }
        if (annos.containsKey(DecimalMax.class.getName())) {
            DecimalMax max = (DecimalMax) annos.get(DecimalMax.class.getName());
            if (max.inclusive())
                schema.setMaximum(BigDecimal.valueOf(Double.parseDouble(max.value())));
            else
                schema.setExclusiveMaximum(!max.inclusive());
        }
        if (annos.containsKey(POSITIVE_OR_ZERO))
            schema.setMinimum(BigDecimal.ZERO);
        if (annos.containsKey(NEGATIVE_OR_ZERO))
            schema.setMaximum(BigDecimal.ZERO);
        if (annos.containsKey(Pattern.class.getName())) {
            Pattern pattern = (Pattern) annos.get(Pattern.class.getName());
            schema.setPattern(pattern.regexp());
        }
    }

}
