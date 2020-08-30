
package com.devappliance.ninjadoc;

import com.devappliance.ninjadoc.annotations.RouterOperation;
import com.devappliance.ninjadoc.annotations.RouterOperations;
import com.devappliance.ninjadoc.customizers.OpenApiCustomiser;
import com.devappliance.ninjadoc.customizers.OperationCustomizer;
import com.devappliance.ninjadoc.fn.AbstractRouterFunctionVisitor;
import com.devappliance.ninjadoc.fn.RouterFunctionData;
import com.devappliance.ninjadoc.wrappers.ApplicationContext;
import com.devappliance.ninjadoc.wrappers.HandlerMethod;
import com.devappliance.ninjadoc.wrappers.RequestMethod;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;
import com.google.inject.Injector;
import io.swagger.v3.core.filter.SpecFilter;
import io.swagger.v3.core.util.ReflectionUtils;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.callbacks.Callback;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.PathItem.HttpMethod;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;

import javax.inject.Provider;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.devappliance.ninjadoc.converters.SchemaPropertyDeprecatingConverter.isDeprecated;

public abstract class AbstractOpenApiResource extends SpecFilter {

	/**
	 * The constant LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractOpenApiResource.class);

	/**
	 * The constant ADDITIONAL_REST_CONTROLLERS.
	 */
	private static final List<Class<?>> ADDITIONAL_REST_CONTROLLERS = new ArrayList<>();

	/**
	 * The constant HIDDEN_REST_CONTROLLERS.
	 */
	private static final List<Class<?>> HIDDEN_REST_CONTROLLERS = new ArrayList<>();
	/**
	 * The Spring doc config properties.
	 */
	protected final NinjaDocConfigProperties ninjaDocConfigProperties;
	/**
	 * The open api builder object factory.
	 */
	private final Provider<OpenAPIBuilder> openAPIBuilderProvider;
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
	 * The Open api customisers.
	 */
	private final Optional<Set<OpenApiCustomiser>> openApiCustomisers;
	/**
	 * The Operation customizers.
	 */
	private final Optional<Set<OperationCustomizer>> operationCustomizers;
	/**
	 * The Ant path matcher.
	 */
	private final AntPathMatcher antPathMatcher = new AntPathMatcher();
	/**
	 * The Group name.
	 */
	private final String groupName;
	/**
	 * The Open api builder.
	 */
	protected OpenAPIBuilder openAPIBuilder;
	protected ApplicationContext applicationContext;

	/**
	 * Instantiates a new Abstract open api resource.
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
	protected AbstractOpenApiResource(String groupName, Provider<OpenAPIBuilder> openAPIBuilderObjectFactory,
									  AbstractRequestBuilder requestBuilder,
									  GenericResponseBuilder responseBuilder, OperationBuilder operationParser,
									  Optional<Set<OperationCustomizer>> operationCustomizers,
									  Optional<Set<OpenApiCustomiser>> openApiCustomisers,
									  NinjaDocConfigProperties ninjaDocConfigProperties, Injector injector) {
		super();
		this.groupName = Objects.requireNonNull(groupName, "groupName");
		this.openAPIBuilderProvider = openAPIBuilderObjectFactory;
		this.openAPIBuilder = openAPIBuilderObjectFactory.get();
		this.requestBuilder = requestBuilder;
		this.responseBuilder = responseBuilder;
		this.operationParser = operationParser;
		this.openApiCustomisers = openApiCustomisers;
		this.ninjaDocConfigProperties = ninjaDocConfigProperties;
		if (operationCustomizers.isPresent())
			operationCustomizers.get().removeIf(Objects::isNull);
		this.operationCustomizers = operationCustomizers;
		this.applicationContext = ApplicationContext.from(injector);
	}

	/**
	 * Add rest controllers.
	 *
	 * @param classes the classes
	 */
	public static void addRestControllers(Class<?>... classes) {
		ADDITIONAL_REST_CONTROLLERS.addAll(Arrays.asList(classes));
	}

	/**
	 * Add hidden rest controllers.
	 *
	 * @param classes the classes
	 */
	public static void addHiddenRestControllers(Class<?>... classes) {
		HIDDEN_REST_CONTROLLERS.addAll(Arrays.asList(classes));
	}

	/**
	 * Add hidden rest controllers.
	 *
	 * @param classes the classes
	 */
	public static void addHiddenRestControllers(String... classes) {
		Set<Class<?>> hiddenClasses = new HashSet<>();
		for (String aClass : classes) {
			try {
				hiddenClasses.add(Class.forName(aClass));
			} catch (ClassNotFoundException e) {
				LOGGER.warn("The following class doesn't exist and cannot be hidden: {}", aClass);
			}
		}
		HIDDEN_REST_CONTROLLERS.addAll(hiddenClasses);
	}

	/**
	 * Is hidden rest controllers boolean.
	 *
	 * @param rawClass the raw class
	 * @return the boolean
	 */
	public static boolean isHiddenRestControllers(Class<?> rawClass) {
		return HIDDEN_REST_CONTROLLERS.stream().anyMatch(clazz -> clazz.isAssignableFrom(rawClass));
	}

	/**
	 * Gets open api.
	 *
	 * @return the open api
	 */
	protected synchronized OpenAPI getOpenApi() {
		OpenAPI openApi;
		if (openAPIBuilder.getCachedOpenAPI() == null || ninjaDocConfigProperties.isCacheDisabled()) {
			Instant start = Instant.now();
			openAPIBuilder.build();
			Map<String, Object> mappingsMap = openAPIBuilder.getMappingsMap().entrySet().stream()
					.filter(controller -> (AnnotationUtils.findAnnotation(controller.getValue().getClass(),
							Hidden.class) == null))
					.filter(controller -> !isHiddenRestControllers(controller.getValue().getClass()))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a1, a2) -> a1));
			// calculate generic responses
			openApi = openAPIBuilder.getCalculatedOpenAPI();
			getPaths(mappingsMap);
			if (!CollectionUtils.isEmpty(openApi.getServers()))
				openAPIBuilder.setServersPresent(true);
			openAPIBuilder.updateServers(openApi);

			if (ninjaDocConfigProperties.isRemoveBrokenReferenceDefinitions())
				this.removeBrokenReferenceDefinitions(openApi);

			// run the optional customisers
			openApiCustomisers.ifPresent(apiCustomisers -> apiCustomisers.forEach(openApiCustomiser -> openApiCustomiser.customise(openApi)));

			openAPIBuilder.setCachedOpenAPI(openApi);
			openAPIBuilder.resetCalculatedOpenAPI();

			LOGGER.info("NinjaDoc initialized in: {} ms",
					Duration.between(start, Instant.now()).toMillis());
		} else {
			if (!CollectionUtils.isEmpty(openAPIBuilder.getCachedOpenAPI().getServers()))
				openAPIBuilder.setServersPresent(true);
			openApi = openAPIBuilder.updateServers(openAPIBuilder.getCachedOpenAPI());
		}
		return openApi;
	}

	/**
	 * Gets paths.
	 *
	 * @param findRestControllers the find rest controllers
	 */
	protected abstract void getPaths(Map<String, Object> findRestControllers);

	/**
	 * Calculate path.
	 *
	 * @param handlerMethod   the handler method
	 * @param routerOperation the router operation
	 */
	protected void calculatePath(HandlerMethod handlerMethod, com.devappliance.ninjadoc.fn.RouterOperation routerOperation) {
		String operationPath = routerOperation.getPath();
		Set<RequestMethod> requestMethods = new HashSet<>(Arrays.asList(routerOperation.getMethods()));
		io.swagger.v3.oas.annotations.Operation apiOperation = routerOperation.getOperation();
		String[] methodConsumes = routerOperation.getConsumes();
		String[] methodProduces = routerOperation.getProduces();
		String[] headers = routerOperation.getHeaders();
		Map<String, String> queryParams = routerOperation.getQueryParams();

		OpenAPI openAPI = openAPIBuilder.getCalculatedOpenAPI();
		Components components = openAPI.getComponents();
		Paths paths = openAPI.getPaths();

		Map<HttpMethod, Operation> operationMap = null;
		if (paths.containsKey(operationPath)) {
			PathItem pathItem = paths.get(operationPath);
			operationMap = pathItem.readOperationsMap();
		}

		for (RequestMethod requestMethod : requestMethods) {
			Operation existingOperation = getExistingOperation(operationMap, requestMethod);
			Method method = handlerMethod.getMethod();
			// skip hidden operations
			if (operationParser.isHidden(method))
				continue;

//			RequestMapping reqMappingClass = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getBeanType(),
//					RequestMapping.class);

			MethodAttributes methodAttributes = new MethodAttributes(ninjaDocConfigProperties.getDefaultConsumesMediaType(),
					ninjaDocConfigProperties.getDefaultProducesMediaType(), methodConsumes, methodProduces, headers);
			methodAttributes.setMethodOverloaded(existingOperation != null);

//			if (reqMappingClass != null) {
//				methodAttributes.setClassConsumes(reqMappingClass.consumes());
//				methodAttributes.setClassProduces(reqMappingClass.produces());
//			}

			methodAttributes.calculateConsumesProduces(method);

			Operation operation = (existingOperation != null) ? existingOperation : new Operation();

			if (isDeprecated(method))
				operation.setDeprecated(true);

			// Add documentation from operation annotation
			if (apiOperation == null || StringUtils.isBlank(apiOperation.operationId()))
				apiOperation = AnnotatedElementUtils.findMergedAnnotation(method,
						io.swagger.v3.oas.annotations.Operation.class);

			calculateJsonView(apiOperation, methodAttributes, method);
			if (apiOperation != null)
				openAPI = operationParser.parse(apiOperation, operation, openAPI, methodAttributes);
			fillParametersList(operation, queryParams, methodAttributes);

			// compute tags
			operation = openAPIBuilder.buildTags(handlerMethod, operation, openAPI);

			io.swagger.v3.oas.annotations.parameters.RequestBody requestBodyDoc = AnnotatedElementUtils.findMergedAnnotation(method,
					io.swagger.v3.oas.annotations.parameters.RequestBody.class);

			// RequestBody in Operation
			requestBuilder.getRequestBodyBuilder()
					.buildRequestBodyFromDoc(requestBodyDoc, methodAttributes, components,
							methodAttributes.getJsonViewAnnotationForRequestBody())
					.ifPresent(operation::setRequestBody);
			// requests
			operation = requestBuilder.build(handlerMethod, requestMethod, operation, methodAttributes, openAPI);

			// responses
			ApiResponses apiResponses = responseBuilder.build(components, handlerMethod, operation, methodAttributes);
			operation.setResponses(apiResponses);

			Set<Callback> apiCallbacks = AnnotatedElementUtils.findMergedRepeatableAnnotations(method, Callback.class);

			// callbacks
			buildCallbacks(openAPI, methodAttributes, operation, apiCallbacks);

			// allow for customisation
			customiseOperation(operation, handlerMethod);

			PathItem pathItemObject = buildPathItem(requestMethod, operation, operationPath, paths);
			paths.addPathItem(operationPath, pathItemObject);
		}
	}

	/**
	 * Build callbacks.
	 *
	 * @param openAPI          the open api
	 * @param methodAttributes the method attributes
	 * @param operation        the operation
	 * @param apiCallbacks     the api callbacks
	 */
	private void buildCallbacks(OpenAPI openAPI, MethodAttributes methodAttributes, Operation operation, Set<Callback> apiCallbacks) {
		if (!CollectionUtils.isEmpty(apiCallbacks))
			operationParser.buildCallbacks(apiCallbacks, openAPI, methodAttributes)
					.ifPresent(operation::setCallbacks);
	}

	/**
	 * Calculate path.
	 *
	 * @param routerOperationList the router operation list
	 */
	protected void calculatePath(List<com.devappliance.ninjadoc.fn.RouterOperation> routerOperationList) {
		ApplicationContext applicationContext = openAPIBuilder.getContext();
		if (!CollectionUtils.isEmpty(routerOperationList)) {
			Collections.sort(routerOperationList);
			for (com.devappliance.ninjadoc.fn.RouterOperation routerOperation : routerOperationList) {
				if (routerOperation.getBeanClass() != null && !Void.class.equals(routerOperation.getBeanClass())) {
					Object handlerBean = applicationContext.getBean(routerOperation.getBeanClass());
					HandlerMethod handlerMethod = null;
					if (StringUtils.isNotBlank(routerOperation.getBeanMethod())) {
						try {
							if (ArrayUtils.isEmpty(routerOperation.getParameterTypes())) {
								Optional<Method> methodOptional = Arrays.stream(handlerBean.getClass().getDeclaredMethods())
										.filter(method -> routerOperation.getBeanMethod().equals(method.getName()) && method.getParameters().length == 0)
										.findAny();
								if (!methodOptional.isPresent())
									methodOptional = Arrays.stream(handlerBean.getClass().getDeclaredMethods())
											.filter(method1 -> routerOperation.getBeanMethod().equals(method1.getName()))
											.findAny();
								if (methodOptional.isPresent())
									handlerMethod = new HandlerMethod(handlerBean, methodOptional.get());
							} else
								handlerMethod = new HandlerMethod(handlerBean, routerOperation.getBeanMethod(), routerOperation.getParameterTypes());
						} catch (NoSuchMethodException e) {
							LOGGER.error(e.getMessage());
						}
						if (handlerMethod != null && isPackageToScan(handlerMethod.getBeanType().getPackage()) && isPathToMatch(routerOperation.getPath()))
							calculatePath(handlerMethod, routerOperation);
					}
				} else if (routerOperation.getOperation() != null && StringUtils.isNotBlank(routerOperation.getOperation().operationId()) && isPathToMatch(routerOperation.getPath())) {
					calculatePath(routerOperation);
				} else if (routerOperation.getOperationModel() != null && StringUtils.isNotBlank(routerOperation.getOperationModel().getOperationId()) && isPathToMatch(routerOperation.getPath())) {
					calculatePath(routerOperation);
				}
			}
		}
	}

	/**
	 * Calculate path.
	 *
	 * @param routerOperation the router operation
	 */
	protected void calculatePath(com.devappliance.ninjadoc.fn.RouterOperation routerOperation) {
		String operationPath = routerOperation.getPath();
		io.swagger.v3.oas.annotations.Operation apiOperation = routerOperation.getOperation();
		String[] methodConsumes = routerOperation.getConsumes();
		String[] methodProduces = routerOperation.getProduces();
		String[] headers = routerOperation.getHeaders();
		Map<String, String> queryParams = routerOperation.getQueryParams();

		OpenAPI openAPI = openAPIBuilder.getCalculatedOpenAPI();
		Paths paths = openAPI.getPaths();
		Map<HttpMethod, Operation> operationMap = null;
		if (paths.containsKey(operationPath)) {
			PathItem pathItem = paths.get(operationPath);
			operationMap = pathItem.readOperationsMap();
		}
		for (RequestMethod requestMethod : routerOperation.getMethods()) {
			Operation existingOperation = getExistingOperation(operationMap, requestMethod);
			MethodAttributes methodAttributes = new MethodAttributes(ninjaDocConfigProperties.getDefaultConsumesMediaType(), ninjaDocConfigProperties.getDefaultProducesMediaType(), methodConsumes, methodProduces, headers);
			methodAttributes.setMethodOverloaded(existingOperation != null);
			Operation operation = getOperation(routerOperation, existingOperation);
			if (apiOperation != null)
				openAPI = operationParser.parse(apiOperation, operation, openAPI, methodAttributes);

			String operationId = operationParser.getOperationId(operation.getOperationId(), openAPI);
			operation.setOperationId(operationId);

			fillParametersList(operation, queryParams, methodAttributes);
			if (!CollectionUtils.isEmpty(operation.getParameters()))
				operation.getParameters().forEach(parameter -> {
							if (parameter.getSchema() == null)
								parameter.setSchema(new StringSchema());
							if (parameter.getIn() == null)
								parameter.setIn(ParameterIn.QUERY.toString());
						}
				);
			PathItem pathItemObject = buildPathItem(requestMethod, operation, operationPath, paths);
			paths.addPathItem(operationPath, pathItemObject);
		}
	}

	/**
	 * Calculate path.
	 *
	 * @param handlerMethod  the handler method
	 * @param operationPath  the operation path
	 * @param requestMethods the request methods
	 */
	protected void calculatePath(HandlerMethod handlerMethod, String operationPath,
								 Set<RequestMethod> requestMethods) {
		this.calculatePath(handlerMethod, new com.devappliance.ninjadoc.fn.RouterOperation(operationPath, requestMethods.toArray(new RequestMethod[requestMethods.size()])));
	}

	/**
	 * Gets router function paths.
	 *
	 * @param beanName              the bean name
	 * @param routerFunctionVisitor the router function visitor
	 */
	protected void getRouterFunctionPaths(String beanName, AbstractRouterFunctionVisitor routerFunctionVisitor) {
		List<RouterOperation> routerOperationList = new ArrayList<>();
		ApplicationContext applicationContext = openAPIBuilder.getContext();
		RouterOperations routerOperations = applicationContext.findAnnotationOnBean(beanName, RouterOperations.class);
		if (routerOperations == null) {
			RouterOperation routerOperation = applicationContext.findAnnotationOnBean(beanName, RouterOperation.class);
			if (routerOperation != null)
				routerOperationList.add(routerOperation);
		} else
			routerOperationList.addAll(Arrays.asList(routerOperations.value()));
		if (routerOperationList.size() == 1)
			calculatePath(routerOperationList.stream().map(routerOperation -> new com.devappliance.ninjadoc.fn.RouterOperation(routerOperation, routerFunctionVisitor.getRouterFunctionDatas().get(0))).collect(Collectors.toList()));
		else {
			List<com.devappliance.ninjadoc.fn.RouterOperation> operationList = routerOperationList.stream().map(com.devappliance.ninjadoc.fn.RouterOperation::new).collect(Collectors.toList());
			mergeRouters(routerFunctionVisitor.getRouterFunctionDatas(), operationList);
			calculatePath(operationList);
		}
	}

	/**
	 * Is package to scan boolean.
	 *
	 * @param aPackage the a package
	 * @return the boolean
	 */
	protected boolean isPackageToScan(Package aPackage) {
		if (aPackage == null)
			return true;
		final String packageName = aPackage.getName();
		List<String> packagesToScan = ninjaDocConfigProperties.getPackagesToScan();
		List<String> packagesToExclude = ninjaDocConfigProperties.getPackagesToExclude();
		if (CollectionUtils.isEmpty(packagesToScan)) {
			Optional<NinjaDocConfigProperties.GroupConfig> optionalGroupConfig = ninjaDocConfigProperties.getGroupConfigs().stream().filter(groupConfig -> this.groupName.equals(groupConfig.getGroup())).findAny();
			if (optionalGroupConfig.isPresent())
				packagesToScan = optionalGroupConfig.get().getPackagesToScan();
		}
		if (CollectionUtils.isEmpty(packagesToExclude)) {
			Optional<NinjaDocConfigProperties.GroupConfig> optionalGroupConfig = ninjaDocConfigProperties.getGroupConfigs().stream().filter(groupConfig -> this.groupName.equals(groupConfig.getGroup())).findAny();
			if (optionalGroupConfig.isPresent())
				packagesToExclude = optionalGroupConfig.get().getPackagesToExclude();
		}
		boolean include = CollectionUtils.isEmpty(packagesToScan)
				|| packagesToScan.stream().anyMatch(pack -> packageName.equals(pack)
				|| packageName.startsWith(pack + "."));
		boolean exclude = !CollectionUtils.isEmpty(packagesToExclude)
				&& (packagesToExclude.stream().anyMatch(pack -> packageName.equals(pack)
				|| packageName.startsWith(pack + ".")));

		return include && !exclude;
	}

	/**
	 * Is path to match boolean.
	 *
	 * @param operationPath the operation path
	 * @return the boolean
	 */
	protected boolean isPathToMatch(String operationPath) {
		List<String> pathsToMatch = ninjaDocConfigProperties.getPathsToMatch();
		List<String> pathsToExclude = ninjaDocConfigProperties.getPathsToExclude();
		if (CollectionUtils.isEmpty(pathsToMatch)) {
			Optional<NinjaDocConfigProperties.GroupConfig> optionalGroupConfig = ninjaDocConfigProperties.getGroupConfigs().stream().filter(groupConfig -> this.groupName.equals(groupConfig.getGroup())).findAny();
			if (optionalGroupConfig.isPresent())
				pathsToMatch = optionalGroupConfig.get().getPathsToMatch();
		}
		if (CollectionUtils.isEmpty(pathsToExclude)) {
			Optional<NinjaDocConfigProperties.GroupConfig> optionalGroupConfig = ninjaDocConfigProperties.getGroupConfigs().stream().filter(groupConfig -> this.groupName.equals(groupConfig.getGroup())).findAny();
			if (optionalGroupConfig.isPresent())
				pathsToExclude = optionalGroupConfig.get().getPathsToExclude();
		}
		boolean include = CollectionUtils.isEmpty(pathsToMatch) || pathsToMatch.stream().anyMatch(pattern -> antPathMatcher.match(pattern, operationPath));
		boolean exclude = !CollectionUtils.isEmpty(pathsToExclude) && pathsToExclude.stream().anyMatch(pattern -> antPathMatcher.match(pattern, operationPath));
		return include && !exclude;
	}

	/**
	 * Decode string.
	 *
	 * @param requestURI the request uri
	 * @return the string
	 */
	protected String decode(String requestURI) {
		try {
			return URLDecoder.decode(requestURI, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			return requestURI;
		}
	}

	/**
	 * Is additional rest controller boolean.
	 *
	 * @param rawClass the raw class
	 * @return the boolean
	 */
	protected boolean isAdditionalRestController(Class<?> rawClass) {
		return ADDITIONAL_REST_CONTROLLERS.stream().anyMatch(clazz -> clazz.isAssignableFrom(rawClass));
	}

	/**
	 * Gets default allowed http methods.
	 *
	 * @return the default allowed http methods
	 */
	protected Set<RequestMethod> getDefaultAllowedHttpMethods() {
		RequestMethod[] allowedRequestMethods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS, RequestMethod.HEAD};
		return new HashSet<>(Arrays.asList(allowedRequestMethods));
	}


	/**
	 * Customise operation operation.
	 *
	 * @param operation     the operation
	 * @param handlerMethod the handler method
	 * @return the operation
	 */
	protected Operation customiseOperation(Operation operation, HandlerMethod handlerMethod) {
		operationCustomizers.ifPresent(customizers -> customizers.forEach(customizer -> customizer.customize(operation, handlerMethod)));
		return operation;
	}

	/**
	 * Merge routers.
	 *
	 * @param routerFunctionDatas the router function datas
	 * @param routerOperationList the router operation list
	 */
	protected void mergeRouters(List<RouterFunctionData> routerFunctionDatas, List<com.devappliance.ninjadoc.fn.RouterOperation> routerOperationList) {
		for (com.devappliance.ninjadoc.fn.RouterOperation routerOperation : routerOperationList) {
			if (StringUtils.isNotBlank(routerOperation.getPath())) {
				// PATH
				List<RouterFunctionData> routerFunctionDataList = routerFunctionDatas.stream()
						.filter(routerFunctionData1 -> routerFunctionData1.getPath().equals(routerOperation.getPath()))
						.collect(Collectors.toList());
				if (routerFunctionDataList.size() == 1)
					fillRouterOperation(routerFunctionDataList.get(0), routerOperation);
				else if (routerFunctionDataList.size() > 1 && ArrayUtils.isNotEmpty(routerOperation.getMethods())) {
					// PATH + METHOD
					routerFunctionDataList = routerFunctionDatas.stream()
							.filter(routerFunctionData1 -> routerFunctionData1.getPath().equals(routerOperation.getPath())
									&& isEqualMethods(routerOperation.getMethods(), routerFunctionData1.getMethods()))
							.collect(Collectors.toList());
					if (routerFunctionDataList.size() == 1)
						fillRouterOperation(routerFunctionDataList.get(0), routerOperation);
					else if (routerFunctionDataList.size() > 1 && ArrayUtils.isNotEmpty(routerOperation.getProduces())) {
						// PATH + METHOD + PRODUCES
						routerFunctionDataList = routerFunctionDatas.stream()
								.filter(routerFunctionData1 -> routerFunctionData1.getPath().equals(routerOperation.getPath())
										&& isEqualMethods(routerOperation.getMethods(), routerFunctionData1.getMethods())
										&& isEqualArrays(routerFunctionData1.getProduces(), routerOperation.getProduces()))
								.collect(Collectors.toList());
						if (routerFunctionDataList.size() == 1)
							fillRouterOperation(routerFunctionDataList.get(0), routerOperation);
						else if (routerFunctionDataList.size() > 1 && ArrayUtils.isNotEmpty(routerOperation.getConsumes())) {
							// PATH + METHOD + PRODUCES + CONSUMES
							routerFunctionDataList = routerFunctionDatas.stream()
									.filter(routerFunctionData1 -> routerFunctionData1.getPath().equals(routerOperation.getPath())
											&& isEqualMethods(routerOperation.getMethods(), routerFunctionData1.getMethods())
											&& isEqualArrays(routerFunctionData1.getProduces(), routerOperation.getProduces())
											&& isEqualArrays(routerFunctionData1.getConsumes(), routerOperation.getConsumes()))
									.collect(Collectors.toList());
							if (routerFunctionDataList.size() == 1)
								fillRouterOperation(routerFunctionDataList.get(0), routerOperation);
						}
					} else if (routerFunctionDataList.size() > 1 && ArrayUtils.isNotEmpty(routerOperation.getConsumes())) {
						// PATH + METHOD + CONSUMES
						routerFunctionDataList = routerFunctionDatas.stream()
								.filter(routerFunctionData1 -> routerFunctionData1.getPath().equals(routerOperation.getPath())
										&& isEqualMethods(routerOperation.getMethods(), routerFunctionData1.getMethods())
										&& isEqualArrays(routerFunctionData1.getConsumes(), routerOperation.getConsumes()))
								.collect(Collectors.toList());
						if (routerFunctionDataList.size() == 1)
							fillRouterOperation(routerFunctionDataList.get(0), routerOperation);
					}
				} else if (routerFunctionDataList.size() > 1 && ArrayUtils.isNotEmpty(routerOperation.getProduces())) {
					// PATH + PRODUCES
					routerFunctionDataList = routerFunctionDatas.stream()
							.filter(routerFunctionData1 -> routerFunctionData1.getPath().equals(routerOperation.getPath())
									&& isEqualArrays(routerFunctionData1.getProduces(), routerOperation.getProduces()))
							.collect(Collectors.toList());
					if (routerFunctionDataList.size() == 1)
						fillRouterOperation(routerFunctionDataList.get(0), routerOperation);
					else if (routerFunctionDataList.size() > 1 && ArrayUtils.isNotEmpty(routerOperation.getConsumes())) {
						// PATH + PRODUCES + CONSUMES
						routerFunctionDataList = routerFunctionDatas.stream()
								.filter(routerFunctionData1 -> routerFunctionData1.getPath().equals(routerOperation.getPath())
										&& isEqualMethods(routerOperation.getMethods(), routerFunctionData1.getMethods())
										&& isEqualArrays(routerFunctionData1.getConsumes(), routerOperation.getConsumes())
										&& isEqualArrays(routerFunctionData1.getProduces(), routerOperation.getProduces()))
								.collect(Collectors.toList());
						if (routerFunctionDataList.size() == 1)
							fillRouterOperation(routerFunctionDataList.get(0), routerOperation);
					}
				} else if (routerFunctionDataList.size() > 1 && ArrayUtils.isNotEmpty(routerOperation.getConsumes())) {
					// PATH + CONSUMES
					routerFunctionDataList = routerFunctionDatas.stream()
							.filter(routerFunctionData1 -> routerFunctionData1.getPath().equals(routerOperation.getPath())
									&& isEqualArrays(routerFunctionData1.getConsumes(), routerOperation.getConsumes()))
							.collect(Collectors.toList());
					if (routerFunctionDataList.size() == 1)
						fillRouterOperation(routerFunctionDataList.get(0), routerOperation);
				}
			}
		}
	}

	/**
	 * Calculate json view.
	 *
	 * @param apiOperation     the api operation
	 * @param methodAttributes the method attributes
	 * @param method           the method
	 */
	private void calculateJsonView(io.swagger.v3.oas.annotations.Operation apiOperation,
								   MethodAttributes methodAttributes, Method method) {
		JsonView jsonViewAnnotation;
		JsonView jsonViewAnnotationForRequestBody;
		if (apiOperation != null && apiOperation.ignoreJsonView()) {
			jsonViewAnnotation = null;
			jsonViewAnnotationForRequestBody = null;
		} else {
			jsonViewAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, JsonView.class);
			/*
			 * If one and only one exists, use the @JsonView annotation from the method
			 * parameter annotated with @RequestBody. Otherwise fall back to the @JsonView
			 * annotation for the method itself.
			 */
			jsonViewAnnotationForRequestBody = (JsonView) Arrays.stream(ReflectionUtils.getParameterAnnotations(method))
					.flatMap(Arrays::stream).filter(annotation -> annotation.annotationType().equals(JsonView.class))
					.reduce((a, b) -> null).orElse(jsonViewAnnotation);
		}
		methodAttributes.setJsonViewAnnotation(jsonViewAnnotation);
		methodAttributes.setJsonViewAnnotationForRequestBody(jsonViewAnnotationForRequestBody);
	}

	/**
	 * Is equal arrays boolean.
	 *
	 * @param array1 the array 1
	 * @param array2 the array 2
	 * @return the boolean
	 */
	private boolean isEqualArrays(String[] array1, String[] array2) {
		Arrays.sort(array1);
		Arrays.sort(array2);
		return Arrays.equals(array1, array2);
	}

	/**
	 * Is equal methods boolean.
	 *
	 * @param requestMethods1 the request methods 1
	 * @param requestMethods2 the request methods 2
	 * @return the boolean
	 */
	private boolean isEqualMethods(RequestMethod[] requestMethods1, RequestMethod[] requestMethods2) {
		Arrays.sort(requestMethods1);
		Arrays.sort(requestMethods2);
		return Arrays.equals(requestMethods1, requestMethods2);
	}

	/**
	 * Fill parameters list.
	 *
	 * @param operation        the operation
	 * @param queryParams      the query params
	 * @param methodAttributes the method attributes
	 */
	private void fillParametersList(Operation operation, Map<String, String> queryParams, MethodAttributes methodAttributes) {
		List<Parameter> parametersList = operation.getParameters();
		if (parametersList == null)
			parametersList = new ArrayList<>();
		Collection<Parameter> headersMap = AbstractRequestBuilder.getHeaders(methodAttributes, new LinkedHashMap<>());
		parametersList.addAll(headersMap);
		if (!CollectionUtils.isEmpty(queryParams)) {
			for (Map.Entry<String, String> entry : queryParams.entrySet()) {
				Parameter parameter = new Parameter();
				parameter.setName(entry.getKey());
				parameter.setSchema(new StringSchema()._default(entry.getValue()));
				parameter.setRequired(true);
				parameter.setIn(ParameterIn.QUERY.toString());
				GenericParameterBuilder.mergeParameter(parametersList, parameter);
			}
			operation.setParameters(parametersList);
		}
	}

	/**
	 * Fill router operation.
	 *
	 * @param routerFunctionData the router function data
	 * @param routerOperation    the router operation
	 */
	private void fillRouterOperation(RouterFunctionData routerFunctionData, com.devappliance.ninjadoc.fn.RouterOperation routerOperation) {
		if (ArrayUtils.isEmpty(routerOperation.getConsumes()))
			routerOperation.setConsumes(routerFunctionData.getConsumes());
		if (ArrayUtils.isEmpty(routerOperation.getProduces()))
			routerOperation.setProduces(routerFunctionData.getProduces());
		if (ArrayUtils.isEmpty(routerOperation.getHeaders()))
			routerOperation.setHeaders(routerFunctionData.getHeaders());
		if (ArrayUtils.isEmpty(routerOperation.getMethods()))
			routerOperation.setMethods(routerFunctionData.getMethods());
		if (CollectionUtils.isEmpty(routerOperation.getQueryParams()))
			routerOperation.setQueryParams(routerFunctionData.getQueryParams());
	}

	/**
	 * Build path item path item.
	 *
	 * @param requestMethod the request method
	 * @param operation     the operation
	 * @param operationPath the operation path
	 * @param paths         the paths
	 * @return the path item
	 */
	private PathItem buildPathItem(RequestMethod requestMethod, Operation operation, String operationPath,
								   Paths paths) {
		PathItem pathItemObject;
		if (paths.containsKey(operationPath))
			pathItemObject = paths.get(operationPath);
		else
			pathItemObject = new PathItem();

		switch (requestMethod) {
			case POST:
				pathItemObject.post(operation);
				break;
			case GET:
				pathItemObject.get(operation);
				break;
			case DELETE:
				pathItemObject.delete(operation);
				break;
			case PUT:
				pathItemObject.put(operation);
				break;
			case PATCH:
				pathItemObject.patch(operation);
				break;
			case TRACE:
				pathItemObject.trace(operation);
				break;
			case HEAD:
				pathItemObject.head(operation);
				break;
			case OPTIONS:
				pathItemObject.options(operation);
				break;
			default:
				// Do nothing here
				break;
		}
		return pathItemObject;
	}

	/**
	 * Gets existing operation.
	 *
	 * @param operationMap  the operation map
	 * @param requestMethod the request method
	 * @return the existing operation
	 */
	private Operation getExistingOperation(Map<HttpMethod, Operation> operationMap, RequestMethod requestMethod) {
		Operation existingOperation = null;
		if (!CollectionUtils.isEmpty(operationMap)) {
			// Get existing operation definition
			switch (requestMethod) {
				case GET:
					existingOperation = operationMap.get(HttpMethod.GET);
					break;
				case POST:
					existingOperation = operationMap.get(HttpMethod.POST);
					break;
				case PUT:
					existingOperation = operationMap.get(HttpMethod.PUT);
					break;
				case DELETE:
					existingOperation = operationMap.get(HttpMethod.DELETE);
					break;
				case PATCH:
					existingOperation = operationMap.get(HttpMethod.PATCH);
					break;
				case HEAD:
					existingOperation = operationMap.get(HttpMethod.HEAD);
					break;
				case OPTIONS:
					existingOperation = operationMap.get(HttpMethod.OPTIONS);
					break;
				default:
					// Do nothing here
					break;
			}
		}
		return existingOperation;
	}

	/**
	 * Gets operation.
	 *
	 * @param routerOperation   the router operation
	 * @param existingOperation the existing operation
	 * @return the operation
	 */
	private Operation getOperation(com.devappliance.ninjadoc.fn.RouterOperation routerOperation, Operation existingOperation) {
		Operation operationModel = routerOperation.getOperationModel();
		Operation operation;
		if (existingOperation != null && operationModel == null) {
			operation = existingOperation;
		} else if (existingOperation == null && operationModel != null) {
			operation = operationModel;
		} else if (existingOperation != null) {
			operation = operationParser.mergeOperation(existingOperation, operationModel);
		} else {
			operation = new Operation();
		}
		return operation;
	}

	/**
	 * Init open api builder.
	 */
	protected void initOpenAPIBuilder() {
		if (openAPIBuilder.getCachedOpenAPI() != null && ninjaDocConfigProperties.isCacheDisabled()) {
			openAPIBuilder = openAPIBuilderProvider.get();
		}
	}

	/**
	 * Gets yaml mapper.
	 *
	 * @return the yaml mapper
	 */
	protected ObjectMapper getYamlMapper() {
		ObjectMapper objectMapper = Yaml.mapper();
		YAMLFactory factory = (YAMLFactory) objectMapper.getFactory();
		factory.configure(Feature.USE_NATIVE_TYPE_ID, false);
		return objectMapper;
	}
}
