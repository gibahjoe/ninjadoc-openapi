/*
 *
 *  *
 *  *  * Copyright 2019-2020 the original author or authors.
 *  *  *
 *  *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  * you may not use this file except in compliance with the License.
 *  *  * You may obtain a copy of the License at
 *  *  *
 *  *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *  *
 *  *  * Unless required by applicable law or agreed to in writing, software
 *  *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  * See the License for the specific language governing permissions and
 *  *  * limitations under the License.
 *  *
 *
 */

package com.devappliance.ninjadoc.converters;

import com.devappliance.ninjadoc.customizers.PropertyCustomizer;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.oas.models.media.Schema;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * The type Property customizing converter.
 *
 * @author bnasslahsen
 */
public class PropertyCustomizingConverter implements ModelConverter {

    /**
     * The Property customizers.
     */
    private final Optional<List<PropertyCustomizer>> propertyCustomizers;

    /**
     * Instantiates a new Property customizing converter.
     *
     * @param customizers the customizers
     */
    public PropertyCustomizingConverter(Optional<List<PropertyCustomizer>> customizers) {
        this.propertyCustomizers = customizers;
    }

    @Override
    public Schema resolve(AnnotatedType type, ModelConverterContext context, Iterator<ModelConverter> chain) {
        if (chain.hasNext()) {
            Schema<?> resolvedSchema = chain.next().resolve(type, context, chain);
            if (type.isSchemaProperty() && propertyCustomizers.isPresent()) {
                List<PropertyCustomizer> propertyCustomizerList = propertyCustomizers.get();
                for (PropertyCustomizer propertyCustomizer : propertyCustomizerList)
                    resolvedSchema = propertyCustomizer.customize(resolvedSchema, type);
            }
            return resolvedSchema;
        }
        return null;
    }
}
