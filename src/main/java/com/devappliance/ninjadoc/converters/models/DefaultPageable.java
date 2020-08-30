
package com.devappliance.ninjadoc.converters.models;

import java.util.List;

public class DefaultPageable extends Pageable {

	/**
	 * Instantiates a new Default pageable.
	 *
	 * @param page the page
	 * @param size the size
	 * @param sort the sort
	 */
	public DefaultPageable(int page, int size, List<String> sort) {
		super(page, size, sort);
	}
}
