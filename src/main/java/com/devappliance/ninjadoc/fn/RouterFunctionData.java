
package com.devappliance.ninjadoc.fn;

import com.devappliance.ninjadoc.wrappers.RequestMethod;
import io.swagger.v3.oas.models.PathItem;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * The type Router function data.
 *
 * @author bnasslahsen
 */
public class RouterFunctionData {

	/**
	 * The Path.
	 */
	private String path;

	/**
	 * The Consumes.
	 */
	private List<String> consumes = new ArrayList<>();

	/**
	 * The Produces.
	 */
	private List<String> produces = new ArrayList<>();

	/**
	 * The Headers.
	 */
	private List<String> headers = new ArrayList<>();

	/**
	 * The Query params.
	 */
	private Map<String, String> queryParams = new HashMap<>();

	/**
	 * The Methods.
	 */
	private RequestMethod[] methods;

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
	 * Gets query params.
	 *
	 * @return the query params
	 */
	public Map<String, String> getQueryParams() {
		return queryParams;
	}

	/**
	 * Add query params.
	 *
	 * @param name  the name
	 * @param value the value
	 */
	public void addQueryParams(String name, String value) {
		this.queryParams.put(name, value);
	}

	/**
	 * Get headers string [ ].
	 *
	 * @return the string [ ]
	 */
	public String[] getHeaders() {
		return headers.toArray(new String[headers.size()]);
	}

	/**
	 * Add headers.
	 *
	 * @param headers the headers
	 */
	public void addHeaders(String headers) {
		if (StringUtils.isNotBlank(headers))
			this.headers.add(headers);
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
	public void setMethods(Set<PathItem.HttpMethod> methods) {
		this.methods = getMethod(methods);
	}

	/**
	 * Get consumes string [ ].
	 *
	 * @return the string [ ]
	 */
	public String[] getConsumes() {
		return consumes.toArray(new String[consumes.size()]);
	}

	/**
	 * Add consumes.
	 *
	 * @param consumes the consumes
	 */
	public void addConsumes(String consumes) {
		if (StringUtils.isNotBlank(consumes))
			this.consumes.add(consumes);
	}

	/**
	 * Add produces.
	 *
	 * @param produces the produces
	 */
	public void addProduces(String produces) {
		if (StringUtils.isNotBlank(produces))
			this.produces.add(produces);
	}

	/**
	 * Get method request method [ ].
	 *
	 * @param methods the methods
	 * @return the request method [ ]
	 */
	private RequestMethod[] getMethod(Set<PathItem.HttpMethod> methods) {
		if (!CollectionUtils.isEmpty(methods)) {
			return methods.stream().map(this::getRequestMethod).toArray(RequestMethod[]::new);
		}
		return ArrayUtils.toArray();
	}

	/**
	 * Get produces string [ ].
	 *
	 * @return the string [ ]
	 */
	public String[] getProduces() {
		return produces.toArray(new String[produces.size()]);
	}

	/**
	 * Gets request method.
	 *
	 * @param httpMethod the http method
	 * @return the request method
	 */
	private RequestMethod getRequestMethod(PathItem.HttpMethod httpMethod) {
		RequestMethod requestMethod = null;
		switch (httpMethod) {
			case GET:
				requestMethod = RequestMethod.GET;
				break;
			case POST:
				requestMethod = RequestMethod.POST;
				break;
			case PUT:
				requestMethod = RequestMethod.PUT;
				break;
			case DELETE:
				requestMethod = RequestMethod.DELETE;
				break;
			case PATCH:
				requestMethod = RequestMethod.PATCH;
				break;
			case HEAD:
				requestMethod = RequestMethod.HEAD;
				break;
			case OPTIONS:
				requestMethod = RequestMethod.OPTIONS;
				break;
			default:
				// Do nothing here
				break;
		}
		return requestMethod;
	}
}
