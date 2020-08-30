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

package com.devappliance.ninjadoc.util;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.core.util.AnnotationsUtils;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The type Spring doc annotations utils.
 *
 * @author bnasslahsen
 */
@SuppressWarnings({"rawtypes"})
public class NinjaDocAnnotationsUtils extends AnnotationsUtils {

    /**
     * The constant LOGGER.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(NinjaDocAnnotationsUtils.class);

    /**
     * Resolve schema from type schema.
     *
     * @param schemaImplementation the schema implementation
     * @param components           the components
     * @param jsonView             the json view
     * @param annotations          the annotations
     * @return the schema
     */
    public static Schema resolveSchemaFromType(Class<?> schemaImplementation, Components components,
                                               JsonView jsonView, Annotation[] annotations) {
        Schema schemaObject = extractSchema(components, schemaImplementation, jsonView, annotations);
        if (schemaObject != null && StringUtils.isBlank(schemaObject.get$ref())
                && StringUtils.isBlank(schemaObject.getType()) && !(schemaObject instanceof ComposedSchema)) {
            // default to string
            schemaObject.setType("string");
        }
        return schemaObject;
    }

    /**
     * Extract schema schema.
     *
     * @param components  the components
     * @param returnType  the return type
     * @param jsonView    the json view
     * @param annotations the annotations
     * @return the schema
     */
    public static Schema extractSchema(Components components, Type returnType, JsonView jsonView, Annotation[] annotations) {
        Schema schemaN = null;
        ResolvedSchema resolvedSchema = null;
        try {
            resolvedSchema = ModelConverters.getInstance()
                    .resolveAsResolvedSchema(
                            new AnnotatedType(returnType).resolveAsRef(true).jsonViewAnnotation(jsonView).ctxAnnotations(annotations));
        } catch (Exception e) {
            LOGGER.warn(Constants.GRACEFUL_EXCEPTION_OCCURRED, e);
            return null;
        }
        if (resolvedSchema.schema != null) {
            schemaN = resolvedSchema.schema;
            Map<String, Schema> schemaMap = resolvedSchema.referencedSchemas;
            if (schemaMap != null) {
                for (Map.Entry<String, Schema> entry : schemaMap.entrySet()) {
                    Map<String, Schema> componentSchemas = components.getSchemas();
                    if (componentSchemas == null) {
                        componentSchemas = new LinkedHashMap<>();
                        componentSchemas.put(entry.getKey(), entry.getValue());
                    } else if (!componentSchemas.containsKey(entry.getKey())) {
                        componentSchemas.put(entry.getKey(), entry.getValue());
                    }
                    components.setSchemas(componentSchemas);
                }
            }
        }
        return schemaN;
    }

    /**
     * Extract schema schema.
     *
     * @param components           the components
     * @param genericParameterType the generic parameter type
     * @param jsonView             the json view
     * @return the schema
     */
    public static Schema extractSchema(Components components, Type genericParameterType, JsonView jsonView) {
        return extractSchema(components, genericParameterType, jsonView, null);
    }

    /**
     * Gets content.
     *
     * @param annotationContents the annotation contents
     * @param classTypes         the class types
     * @param methodTypes        the method types
     * @param schema             the schema
     * @param components         the components
     * @param jsonViewAnnotation the json view annotation
     * @return the content
     */
    public static Optional<Content> getContent(io.swagger.v3.oas.annotations.media.Content[] annotationContents,
                                               String[] classTypes, String[] methodTypes, Schema schema, Components components,
                                               JsonView jsonViewAnnotation) {
        if (ArrayUtils.isEmpty(annotationContents)) {
            return Optional.empty();
        }
        // Encapsulating Content model
        Content content = new Content();

        for (io.swagger.v3.oas.annotations.media.Content annotationContent : annotationContents) {
            MediaType mediaType = getMediaType(schema, components, jsonViewAnnotation, annotationContent);
            ExampleObject[] examples = annotationContent.examples();
            setExamples(mediaType, examples);
            addExtension(annotationContent, mediaType);
            io.swagger.v3.oas.annotations.media.Encoding[] encodings = annotationContent.encoding();
            addEncodingToMediaType(jsonViewAnnotation, mediaType, encodings);
            if (StringUtils.isNotBlank(annotationContent.mediaType())) {
                content.addMediaType(annotationContent.mediaType(), mediaType);
            } else {
                if (mediaType.getSchema() != null)
                    applyTypes(classTypes, methodTypes, content, mediaType);
            }
        }

        if (content.size() == 0 && annotationContents.length != 1) {
            return Optional.empty();
        }
        return Optional.of(content);
    }

    /**
     * Merge schema.
     *
     * @param existingContent the existing content
     * @param schemaN         the schema n
     * @param mediaTypeStr    the media type str
     */
    public static void mergeSchema(Content existingContent, Schema<?> schemaN, String mediaTypeStr) {
        if (existingContent.containsKey(mediaTypeStr)) {
            MediaType mediaType = existingContent.get(mediaTypeStr);
            if (!schemaN.equals(mediaType.getSchema())) {
                // Merge the two schemas for the same mediaType
                Schema firstSchema = mediaType.getSchema();
                ComposedSchema schemaObject;
                if (firstSchema instanceof ComposedSchema) {
                    schemaObject = (ComposedSchema) firstSchema;
                    List<Schema> listOneOf = schemaObject.getOneOf();
                    if (!CollectionUtils.isEmpty(listOneOf) && !listOneOf.contains(schemaN))
                        schemaObject.addOneOfItem(schemaN);
                } else {
                    schemaObject = new ComposedSchema();
                    schemaObject.addOneOfItem(schemaN);
                    schemaObject.addOneOfItem(firstSchema);
                }
                mediaType.setSchema(schemaObject);
                existingContent.addMediaType(mediaTypeStr, mediaType);
            }
        } else
            // Add the new schema for a different mediaType
            existingContent.addMediaType(mediaTypeStr, new MediaType().schema(schemaN));
    }

    /**
     * Add encoding to media type.
     *
     * @param jsonViewAnnotation the json view annotation
     * @param mediaType          the media type
     * @param encodings          the encodings
     */
    private static void addEncodingToMediaType(JsonView jsonViewAnnotation, MediaType mediaType,
                                               io.swagger.v3.oas.annotations.media.Encoding[] encodings) {
        for (io.swagger.v3.oas.annotations.media.Encoding encoding : encodings) {
            addEncodingToMediaType(mediaType, encoding, jsonViewAnnotation);
        }
    }

    /**
     * Add extension.
     *
     * @param annotationContent the annotation content
     * @param mediaType         the media type
     */
    private static void addExtension(io.swagger.v3.oas.annotations.media.Content annotationContent,
                                     MediaType mediaType) {
        if (annotationContent.extensions().length > 0) {
            Map<String, Object> extensions = AnnotationsUtils.getExtensions(annotationContent.extensions());
            extensions.forEach(mediaType::addExtension);
        }
    }

    /**
     * Sets examples.
     *
     * @param mediaType the media type
     * @param examples  the examples
     */
    private static void setExamples(MediaType mediaType, ExampleObject[] examples) {
        if (examples.length == 1 && StringUtils.isBlank(examples[0].name())) {
            getExample(examples[0], true).ifPresent(exampleObject -> mediaType.example(exampleObject.getValue()));
        } else {
            for (ExampleObject example : examples) {
                getExample(example).ifPresent(exampleObject -> {
                            if (exampleObject.get$ref() != null)
                                //Ignore description
                                exampleObject.setDescription(null);
                            mediaType.addExamples(example.name(), exampleObject);
                        }
                );
            }
        }
    }

    /**
     * Gets media type.
     *
     * @param schema             the schema
     * @param components         the components
     * @param jsonViewAnnotation the json view annotation
     * @param annotationContent  the annotation content
     * @return the media type
     */
    private static MediaType getMediaType(Schema schema, Components components, JsonView jsonViewAnnotation,
                                          io.swagger.v3.oas.annotations.media.Content annotationContent) {
        MediaType mediaType = new MediaType();
        if (!annotationContent.schema().hidden()) {
            if (components != null) {
                try {
                    getSchema(annotationContent, components, jsonViewAnnotation).ifPresent(mediaType::setSchema);
                } catch (Exception e) {
                    if (isArray(annotationContent))
                        mediaType.setSchema(new ArraySchema().items(new StringSchema()));
                    else
                        mediaType.setSchema(new StringSchema());
                }
            } else {
                mediaType.setSchema(schema);
            }
        }
        return mediaType;
    }

    /**
     * Is array boolean.
     *
     * @param annotationContent the annotation content
     * @return the boolean
     */
    private static boolean isArray(io.swagger.v3.oas.annotations.media.Content annotationContent) {
        Class<?> schemaImplementation = annotationContent.schema().implementation();
        boolean isArray = false;
        if (schemaImplementation == Void.class) {
            schemaImplementation = annotationContent.array().schema().implementation();
            if (schemaImplementation != Void.class) {
                isArray = true;
            }
        }
        return isArray;
    }

}
