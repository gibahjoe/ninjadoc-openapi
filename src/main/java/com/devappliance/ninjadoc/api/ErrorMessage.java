
package com.devappliance.ninjadoc.api;

import java.util.UUID;

public class ErrorMessage {

	private UUID id;

	private String message;

	/**
	 * Instantiates a new Error message.
	 *
	 * @param message the message
	 */
	public ErrorMessage(String message) {
		this.id = UUID.randomUUID();
		this.message = message;
	}

	/**
	 * Gets id.
	 *
	 * @return the id
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * Sets id.
	 *
	 * @param id the id
	 */
	public void setId(UUID id) {
		this.id = id;
	}

	/**
	 * Gets message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets message.
	 *
	 * @param message the message
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}
