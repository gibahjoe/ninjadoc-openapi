
package com.devappliance.ninjadoc.fn;

import com.devappliance.ninjadoc.wrappers.RequestMethod;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;


public class RouterOperation implements Comparable<RouterOperation> {

	/**
	 * The Path.
	 */
	private String path;

	/**
	 * The Methods.
	 */
	private RequestMethod[] methods;

	/**
	 * The Consumes.
	 */
	private String[] consumes;

	/**
	 * The Produces.
	 */
	private String[] produces;

	/**
	 * The Headers.
	 */
	private String[] headers;

	/**
	 * The Bean class.
	 */
	private Class<?> beanClass;

	/**
	 * The Bean method.
	 */
	private String beanMethod;

	/**
	 * The Parameter types.
	 */
	private Class<?>[] parameterTypes;

	/**
	 * The Query params.
	 */
	private Map<String, String> queryParams;

	/**
	 * The Operation.
	 */
	private Operation operation;

	/**
	 * The Operation model.
	 */
	private io.swagger.v3.oas.models.Operation operationModel;

	/**
	 * Instantiates a new Router operation.
	 *
	 * @param routerOperationAnnotation the router operation annotation
	 */
	public RouterOperation(com.devappliance.ninjadoc.annotations.RouterOperation routerOperationAnnotation) {
		this.path = routerOperationAnnotation.path();
		this.methods = routerOperationAnnotation.method();
		this.consumes = routerOperationAnnotation.consumes();
		this.produces = routerOperationAnnotation.produces();
		this.beanClass = routerOperationAnnotation.beanClass();
		this.beanMethod = routerOperationAnnotation.beanMethod();
		this.parameterTypes = routerOperationAnnotation.parameterTypes();
		this.operation = routerOperationAnnotation.operation();
		this.headers = routerOperationAnnotation.headers();
	}

	/**
	 * Instantiates a new Router operation.
	 *
	 * @param routerOperationAnnotation the router operation annotation
	 * @param routerFunctionData        the router function data
	 */
	public RouterOperation(com.devappliance.ninjadoc.annotations.RouterOperation routerOperationAnnotation, RouterFunctionData routerFunctionData) {
		this.path = StringUtils.isBlank(routerOperationAnnotation.path()) ? routerFunctionData.getPath() : routerOperationAnnotation.path();
		this.methods = ArrayUtils.isEmpty(routerOperationAnnotation.method()) ? routerFunctionData.getMethods() : routerOperationAnnotation.method();
		this.consumes = ArrayUtils.isEmpty(routerOperationAnnotation.consumes()) ? routerFunctionData.getConsumes() : routerOperationAnnotation.consumes();
		this.produces = ArrayUtils.isEmpty(routerOperationAnnotation.produces()) ? routerFunctionData.getProduces() : routerOperationAnnotation.produces();
		this.beanClass = routerOperationAnnotation.beanClass();
		this.beanMethod = routerOperationAnnotation.beanMethod();
		this.parameterTypes = routerOperationAnnotation.parameterTypes();
		this.operation = routerOperationAnnotation.operation();
		this.headers = ArrayUtils.isEmpty(routerOperationAnnotation.headers()) ? routerFunctionData.getHeaders() : routerOperationAnnotation.headers();
		this.queryParams = routerFunctionData.getQueryParams();
	}

	/**
	 * Instantiates a new Router operation.
	 *
	 * @param path    the path
	 * @param methods the methods
	 */
	public RouterOperation(String path, RequestMethod[] methods) {
		this.path = path;
		this.methods = methods;
	}

	/**
	 * Gets path.
	 *
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Sets path.
	 *
	 * @param path the path
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Get methods request method [ ].
	 *
	 * @return the request method [ ]
	 */
	public RequestMethod[] getMethods() {
		return methods;
	}

	/**
	 * Sets methods.
	 *
	 * @param methods the methods
	 */
	public void setMethods(RequestMethod[] methods) {
		this.methods = methods;
	}

	/**
	 * Get consumes string [ ].
	 *
	 * @return the string [ ]
	 */
	public String[] getConsumes() {
		return consumes;
	}

	/**
	 * Sets consumes.
	 *
	 * @param consumes the consumes
	 */
	public void setConsumes(String[] consumes) {
		this.consumes = consumes;
	}

	/**
	 * Get produces string [ ].
	 *
	 * @return the string [ ]
	 */
	public String[] getProduces() {
		return produces;
	}

	/**
	 * Sets produces.
	 *
	 * @param produces the produces
	 */
	public void setProduces(String[] produces) {
		this.produces = produces;
	}

	/**
	 * Gets bean class.
	 *
	 * @return the bean class
	 */
	public Class<?> getBeanClass() {
		return beanClass;
	}

	/**
	 * Sets bean class.
	 *
	 * @param beanClass the bean class
	 */
	public void setBeanClass(Class<?> beanClass) {
		this.beanClass = beanClass;
	}

	/**
	 * Gets bean method.
	 *
	 * @return the bean method
	 */
	public String getBeanMethod() {
		return beanMethod;
	}

	/**
	 * Sets bean method.
	 *
	 * @param beanMethod the bean method
	 */
	public void setBeanMethod(String beanMethod) {
		this.beanMethod = beanMethod;
	}

	/**
	 * Get parameter types class [ ].
	 *
	 * @return the class [ ]
	 */
	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	/**
	 * Sets parameter types.
	 *
	 * @param parameterTypes the parameter types
	 */
	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	/**
	 * Gets operation.
	 *
	 * @return the operation
	 */
	public Operation getOperation() {
		return operation;
	}

	/**
	 * Sets operation.
	 *
	 * @param operation the operation
	 */
	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	/**
	 * Get headers string [ ].
	 *
	 * @return the string [ ]
	 */
	public String[] getHeaders() {
		return headers;
	}

	/**
	 * Sets headers.
	 *
	 * @param headers the headers
	 */
	public void setHeaders(String[] headers) {
		this.headers = headers;
	}

	/**
	 * Gets query params.
	 *
	 * @return the query params
	 */
	public Map<String, String> getQueryParams() {
		return queryParams;
	}

	/**
	 * Sets query params.
	 *
	 * @param queryParams the query params
	 */
	public void setQueryParams(Map<String, String> queryParams) {
		this.queryParams = queryParams;
	}

	/**
	 * Gets operation model.
	 *
	 * @return the operation model
	 */
	public io.swagger.v3.oas.models.Operation getOperationModel() {
		return operationModel;
	}

	/**
	 * Sets operation model.
	 *
	 * @param operationModel the operation model
	 */
	public void setOperationModel(io.swagger.v3.oas.models.Operation operationModel) {
		this.operationModel = operationModel;
	}

	@Override
	public int compareTo(RouterOperation routerOperation) {
		int result = path.compareTo(routerOperation.getPath());
		if (result == 0)
			result = methods[0].compareTo(routerOperation.getMethods()[0]);
		if (result == 0 && operationModel != null && routerOperation.getOperationModel() != null)
			result = operationModel.getOperationId().compareTo(routerOperation.getOperationModel().getOperationId());
		if (result == 0 && operation != null && operation.operationId() != null && routerOperation.getOperation().operationId() != null)
			result = operation.operationId().compareTo(routerOperation.getOperation().operationId());
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RouterOperation that = (RouterOperation) o;
		return Objects.equals(path, that.path) &&
				Arrays.equals(methods, that.methods) &&
				Arrays.equals(consumes, that.consumes) &&
				Arrays.equals(produces, that.produces) &&
				Arrays.equals(headers, that.headers) &&
				Objects.equals(beanClass, that.beanClass) &&
				Objects.equals(beanMethod, that.beanMethod) &&
				Arrays.equals(parameterTypes, that.parameterTypes) &&
				Objects.equals(queryParams, that.queryParams) &&
				Objects.equals(operation, that.operation) &&
				Objects.equals(operationModel, that.operationModel);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(path, beanClass, beanMethod, queryParams, operation, operationModel);
		result = 31 * result + Arrays.hashCode(methods);
		result = 31 * result + Arrays.hashCode(consumes);
		result = 31 * result + Arrays.hashCode(produces);
		result = 31 * result + Arrays.hashCode(headers);
		result = 31 * result + Arrays.hashCode(parameterTypes);
		return result;
	}
}
