
package com.devappliance.ninjadoc.converters;


import com.fasterxml.jackson.databind.JavaType;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.media.FileSchema;
import io.swagger.v3.oas.models.media.Schema;

import java.util.Iterator;

import static com.devappliance.ninjadoc.GenericParameterBuilder.isFile;

public class FileSupportConverter implements ModelConverter {

	@Override
	public Schema resolve(AnnotatedType type, ModelConverterContext context, Iterator<ModelConverter> chain) {
		JavaType javaType = Json.mapper().constructType(type.getType());
		if (javaType != null) {
			Class<?> cls = javaType.getRawClass();
			if (isFile(cls))
				return new FileSchema();
		}
		return (chain.hasNext()) ? chain.next().resolve(type, context, chain) : null;
	}

}
