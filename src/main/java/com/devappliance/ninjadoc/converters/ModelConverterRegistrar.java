
package com.devappliance.ninjadoc.converters;

import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverters;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Wrapper for model converters to only register converters once
 *
 * @author bnasslahsen
 */
public class ModelConverterRegistrar {

	/**
	 * The constant modelConvertersInstance.
	 */
	private static final ModelConverters modelConvertersInstance = ModelConverters.getInstance();

	/**
	 * The constant LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ModelConverterRegistrar.class);

	/**
	 * Instantiates a new Model converter registrar.
	 *
	 * @param modelConverters spring registered model converter beans which have to be                        registered in {@link ModelConverters} instance
	 */
	public ModelConverterRegistrar(Set<ModelConverter> modelConverters) {
		for (ModelConverter modelConverter : modelConverters) {
			Optional<ModelConverter> registeredConverterOptional = getRegisteredConverterSameAs(modelConverter);
			registeredConverterOptional.ifPresent(modelConvertersInstance::removeConverter);
			modelConvertersInstance.addConverter(modelConverter);
		}
	}

	/**
	 * Gets registered converter same as.
	 *
	 * @param modelConverter the model converter
	 * @return the registered converter same as
	 */
	@SuppressWarnings("unchecked")
	private Optional<ModelConverter> getRegisteredConverterSameAs(ModelConverter modelConverter) {
		try {
			Field convertersField = FieldUtils.getDeclaredField(ModelConverters.class, "converters", true);
			List<ModelConverter> modelConverters = (List<ModelConverter>) convertersField.get(modelConvertersInstance);
			return modelConverters.stream()
					.filter(registeredModelConverter -> isSameConverter(registeredModelConverter, modelConverter))
					.findFirst();
		} catch (IllegalAccessException exception) {
			LOGGER.warn(exception.getMessage());
		}
		return Optional.empty();
	}

	/**
	 * Is same converter boolean.
	 *
	 * @param modelConverter1 the model converter 1
	 * @param modelConverter2 the model converter 2
	 * @return the boolean
	 */
	private boolean isSameConverter(ModelConverter modelConverter1, ModelConverter modelConverter2) {
		// comparing by the converter type
		Class<? extends ModelConverter> modelConverter1Class = modelConverter1.getClass();
		Class<? extends ModelConverter> modelConverter2Class = modelConverter2.getClass();
		return modelConverter1Class.equals(modelConverter2Class);
	}
}
