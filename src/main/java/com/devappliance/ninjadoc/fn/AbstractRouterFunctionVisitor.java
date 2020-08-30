
package com.devappliance.ninjadoc.fn;

import com.google.common.net.HttpHeaders;
import io.swagger.v3.oas.models.PathItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AbstractRouterFunctionVisitor {

	/**
	 * The Router function datas.
	 */
	protected List<RouterFunctionData> routerFunctionDatas = new ArrayList<>();

	/**
	 * The Router function data.
	 */
	protected RouterFunctionData routerFunctionData;

	/**
	 * Method.
	 *
	 * @param methods the methods
	 */
	public void method(Set<PathItem.HttpMethod> methods) {
		routerFunctionData.setMethods(methods);
	}

	/**
	 * Path.
	 *
	 * @param pattern the pattern
	 */
	public void path(String pattern) {
		routerFunctionData.setPath(pattern);
	}

	/**
	 * Header.
	 *
	 * @param name  the name
	 * @param value the value
	 */
	public void header(String name, String value) {
		if (HttpHeaders.ACCEPT.equals(name))
			routerFunctionData.addProduces(value);
		else if (HttpHeaders.CONTENT_TYPE.equals(name))
			routerFunctionData.addConsumes(value);
		else
			routerFunctionData.addHeaders(name + "=" + value);
	}

	/**
	 * Gets router function datas.
	 *
	 * @return the router function datas
	 */
	public List<RouterFunctionData> getRouterFunctionDatas() {
		return routerFunctionDatas;
	}

	/**
	 * Query param.
	 *
	 * @param name  the name
	 * @param value the value
	 */
	public void queryParam(String name, String value) {
		routerFunctionData.addQueryParams(name, value);
	}

	/**
	 * Path extension.
	 *
	 * @param extension the extension
	 */
	public void pathExtension(String extension) {
		// Not yet needed
	}

	/**
	 * Param.
	 *
	 * @param name  the name
	 * @param value the value
	 */
	public void param(String name, String value) {
		// Not yet needed
	}

	/**
	 * Start and.
	 */
	public void startAnd() {
		// Not yet needed
	}

	/**
	 * And.
	 */
	public void and() {
		// Not yet needed
	}

	/**
	 * End and.
	 */
	public void endAnd() {
		// Not yet needed
	}

	/**
	 * Start or.
	 */
	public void startOr() {
		// Not yet needed
	}

	/**
	 * Or.
	 */
	public void or() {
		// Not yet needed
	}

	/**
	 * End or.
	 */
	public void endOr() {
		// Not yet needed
	}

	/**
	 * Start negate.
	 */
	public void startNegate() {
		// Not yet needed
	}

	/**
	 * End negate.
	 */
	public void endNegate() {
		// Not yet needed
	}

}
