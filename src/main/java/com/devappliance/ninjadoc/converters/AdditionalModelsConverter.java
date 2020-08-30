
package com.devappliance.ninjadoc.converters;

import com.fasterxml.jackson.databind.JavaType;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.media.Schema;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AdditionalModelsConverter implements ModelConverter {

	/**
	 * The constant modelToClassMap.
	 */
	private static final Map<Class, Class> modelToClassMap = new HashMap<>();

	/**
	 * The constant modelToSchemaMap.
	 */
	private static final Map<Class, Schema> modelToSchemaMap = new HashMap<>();

	/**
	 * Replace with class.
	 *
	 * @param source the source
	 * @param target the target
	 */
	public static void replaceWithClass(Class source, Class target) {
		modelToClassMap.put(source, target);
	}

	/**
	 * Replace with schema.
	 *
	 * @param source the source
	 * @param target the target
	 */
	public static void replaceWithSchema(Class source, Schema target) {
		modelToSchemaMap.put(source, target);
	}

	/**
	 * Gets replacement.
	 *
	 * @param clazz the clazz
	 * @return the replacement
	 */
	public static Class getReplacement(Class clazz) {
		return modelToClassMap.getOrDefault(clazz, clazz);
	}

	/**
	 * Disable replacement.
	 *
	 * @param clazz the clazz
	 */
	public static void disableReplacement(Class clazz) {
		if (modelToClassMap.containsKey(clazz))
			modelToClassMap.remove(clazz);
	}

	@Override
	public Schema resolve(AnnotatedType type, ModelConverterContext context, Iterator<ModelConverter> chain) {
		JavaType javaType = Json.mapper().constructType(type.getType());
		if (javaType != null) {
			Class<?> cls = javaType.getRawClass();
			if (modelToSchemaMap.containsKey(cls))
				return modelToSchemaMap.get(cls);
			if (modelToClassMap.containsKey(cls))
				type = new AnnotatedType(modelToClassMap.get(cls)).resolveAsRef(true);
		}
		return (chain.hasNext()) ? chain.next().resolve(type, context, chain) : null;
	}

}
