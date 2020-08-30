
package com.devappliance.ninjadoc.converters;

import ninja.Result;
import ninja.Results;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionStage;

public class ConverterUtils {

	/**
	 * The constant RESULT_WRAPPERS_TO_IGNORE.
	 */
	private static final List<Class<?>> RESULT_WRAPPERS_TO_IGNORE = new ArrayList<>();

	/**
	 * The constant RESPONSE_TYPES_TO_IGNORE.
	 */
	private static final List<Class<?>> RESPONSE_TYPES_TO_IGNORE = new ArrayList<>();

	/**
	 * The constant FLUX_WRAPPERS_TO_IGNORE.
	 */
	private static final List<Class<?>> FLUX_WRAPPERS_TO_IGNORE = new ArrayList<>();

	static {
		RESULT_WRAPPERS_TO_IGNORE.add(Callable.class);
		RESULT_WRAPPERS_TO_IGNORE.add(Results.class);
		RESULT_WRAPPERS_TO_IGNORE.add(Result.class);
		RESULT_WRAPPERS_TO_IGNORE.add(CompletionStage.class);
	}

	/**
	 * Instantiates a new Converter utils.
	 */
	private ConverterUtils() {
	}

	/**
	 * Add response wrapper to ignore.
	 *
	 * @param cls the cls
	 */
	public static void addResponseWrapperToIgnore(Class<?> cls) {
		RESULT_WRAPPERS_TO_IGNORE.add(cls);
	}

	/**
	 * Add response type to ignore.
	 *
	 * @param cls the cls
	 */
	public static void addResponseTypeToIgnore(Class<?> cls) {
		RESPONSE_TYPES_TO_IGNORE.add(cls);
	}

	/**
	 * Is response type wrapper boolean.
	 *
	 * @param rawClass the raw class
	 * @return the boolean
	 */
	public static boolean isResponseTypeWrapper(Class<?> rawClass) {
		return RESULT_WRAPPERS_TO_IGNORE.stream().anyMatch(clazz -> clazz.isAssignableFrom(rawClass));
	}

	/**
	 * Is response type to ignore boolean.
	 *
	 * @param rawClass the raw class
	 * @return the boolean
	 */
	public static boolean isResponseTypeToIgnore(Class<?> rawClass) {
		return RESPONSE_TYPES_TO_IGNORE.stream().anyMatch(clazz -> clazz.isAssignableFrom(rawClass));
	}

	/**
	 * Remove response wrapper to ignore.
	 *
	 * @param classes the classes
	 */
	public static void removeResponseWrapperToIgnore(Class<?> classes) {
		List classesToIgnore = Arrays.asList(classes);
		if (RESULT_WRAPPERS_TO_IGNORE.containsAll(classesToIgnore))
			RESULT_WRAPPERS_TO_IGNORE.removeAll(Arrays.asList(classes));
	}

	/**
	 * Remove response type to ignore.
	 *
	 * @param classes the classes
	 */
	public static void removeResponseTypeToIgnore(Class<?> classes) {
		List classesToIgnore = Arrays.asList(classes);
		if (RESPONSE_TYPES_TO_IGNORE.containsAll(classesToIgnore))
			RESPONSE_TYPES_TO_IGNORE.removeAll(Arrays.asList(classes));
	}

	/**
	 * Is flux type wrapper boolean.
	 *
	 * @param rawClass the raw class
	 * @return the boolean
	 */
	public static boolean isFluxTypeWrapper(Class<?> rawClass) {
		return FLUX_WRAPPERS_TO_IGNORE.stream().anyMatch(clazz -> clazz.isAssignableFrom(rawClass));
	}

	/**
	 * Remove flux wrapper to ignore.
	 *
	 * @param classes the classes
	 */
	public static void removeFluxWrapperToIgnore(Class<?> classes) {
		List classesToIgnore = Arrays.asList(classes);
		if (FLUX_WRAPPERS_TO_IGNORE.containsAll(classesToIgnore))
			FLUX_WRAPPERS_TO_IGNORE.removeAll(Arrays.asList(classes));
	}

	/**
	 * Add flux wrapper to ignore.
	 *
	 * @param cls the cls
	 */
	public static void addFluxWrapperToIgnore(Class<?> cls) {
		FLUX_WRAPPERS_TO_IGNORE.add(cls);
	}
}
