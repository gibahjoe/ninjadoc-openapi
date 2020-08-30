
package com.devappliance.ninjadoc;

import com.devappliance.ninjadoc.util.Constants;
import com.devappliance.ninjadoc.util.NinjaDocAnnotationsUtils;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.inject.Inject;
import io.swagger.v3.core.util.AnnotationsUtils;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.FileSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import ninja.params.WithArgumentExtractor;
import ninja.params.WithArgumentExtractors;
import ninja.uploads.FileItem;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.*;

@SuppressWarnings("rawtypes")
public class GenericParameterBuilder {

    /**
     * The constant FILE_TYPES.
     */
    private static final List<Class<?>> FILE_TYPES = new ArrayList<>();

    /**
     * The constant ANNOTATIOSN_TO_IGNORE.
     */
    private static final List<Class> ANNOTATIOSN_TO_IGNORE = new ArrayList<>();

    /**
     * The constant LOGGER.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericParameterBuilder.class);

    static {
        FILE_TYPES.add(FileItem.class);
        FILE_TYPES.add(File.class);
        FILE_TYPES.add(InputStream.class);
        ANNOTATIOSN_TO_IGNORE.add(Hidden.class);
//		ANNOTATIOSN_TO_IGNORE.add(RequestAttribute.class);
    }

    /**
     * The Property resolver utils.
     */
    private final PropertyResolverUtils propertyResolverUtils;

    /**
     * Instantiates a new Generic parameter builder.
     *
     * @param propertyResolverUtils the property resolver utils
     */
    @Inject
    public GenericParameterBuilder(PropertyResolverUtils propertyResolverUtils) {
        this.propertyResolverUtils = propertyResolverUtils;
    }

    /**
     * Add file type.
     *
     * @param classes the classes
     */
    public static void addFileType(Class<?>... classes) {
        FILE_TYPES.addAll(Arrays.asList(classes));
    }

    /**
     * Add annotations to ignore.
     *
     * @param classes the classes
     */
    public static void addAnnotationsToIgnore(Class<?>... classes) {
        ANNOTATIOSN_TO_IGNORE.addAll(Arrays.asList(classes));
    }

    /**
     * Remove annotations to ignore.
     *
     * @param classes the classes
     */
    public static void removeAnnotationsToIgnore(Class<?>... classes) {
        List classesToIgnore = Arrays.asList(classes);
        if (ANNOTATIOSN_TO_IGNORE.containsAll(classesToIgnore))
            ANNOTATIOSN_TO_IGNORE.removeAll(Arrays.asList(classes));
    }

    /**
     * Is file boolean.
     *
     * @param type the type
     * @return the boolean
     */
    public static boolean isFile(Class type) {
        return FILE_TYPES.stream().anyMatch(clazz -> clazz.isAssignableFrom(type));
    }

    /**
     * Merge parameter parameter.
     *
     * @param existingParamDoc the existing param doc
     * @param paramCalcul      the param calcul
     * @return the parameter
     */
    public static Parameter mergeParameter(List<Parameter> existingParamDoc, Parameter paramCalcul) {
        Parameter result = paramCalcul;
        if (paramCalcul != null && paramCalcul.getName() != null) {
            final String name = paramCalcul.getName();
            Parameter paramDoc = existingParamDoc.stream().filter(p -> name.equals(p.getName())).findAny().orElse(null);
            if (paramDoc != null) {
                mergeParameter(paramCalcul, paramDoc);
                result = paramDoc;
            } else
                existingParamDoc.add(result);
        }
        return result;
    }

    /**
     * Merge parameter.
     *
     * @param paramCalcul the param calcul
     * @param paramDoc    the param doc
     */
    private static void mergeParameter(Parameter paramCalcul, Parameter paramDoc) {
        if (StringUtils.isBlank(paramDoc.getDescription()))
            paramDoc.setDescription(paramCalcul.getDescription());

        if (StringUtils.isBlank(paramDoc.getIn()))
            paramDoc.setIn(paramCalcul.getIn());

        if (paramDoc.getExample() == null)
            paramDoc.setExample(paramCalcul.getExample());

        if (paramDoc.getDeprecated() == null)
            paramDoc.setDeprecated(paramCalcul.getDeprecated());

        if (paramDoc.getRequired() == null)
            paramDoc.setRequired(paramCalcul.getRequired());

        if (paramDoc.getAllowEmptyValue() == null)
            paramDoc.setAllowEmptyValue(paramCalcul.getAllowEmptyValue());

        if (paramDoc.getAllowReserved() == null)
            paramDoc.setAllowReserved(paramCalcul.getAllowReserved());

        if (StringUtils.isBlank(paramDoc.get$ref()))
            paramDoc.set$ref(paramDoc.get$ref());

        if (paramDoc.getSchema() == null)
            paramDoc.setSchema(paramCalcul.getSchema());

        if (paramDoc.getExamples() == null)
            paramDoc.setExamples(paramCalcul.getExamples());

        if (paramDoc.getExtensions() == null)
            paramDoc.setExtensions(paramCalcul.getExtensions());

        if (paramDoc.getStyle() == null)
            paramDoc.setStyle(paramCalcul.getStyle());

        if (paramDoc.getExplode() == null)
            paramDoc.setExplode(paramCalcul.getExplode());
    }

    /**
     * Build parameter from doc parameter.
     *
     * @param parameterDoc the parameter doc
     * @param components   the components
     * @param jsonView     the json view
     * @return the parameter
     */
    public Parameter buildParameterFromDoc(io.swagger.v3.oas.annotations.Parameter parameterDoc,
                                           Components components, JsonView jsonView) {
        Parameter parameter = new Parameter();
        if (StringUtils.isNotBlank(parameterDoc.description()))
            parameter.setDescription(propertyResolverUtils.resolve(parameterDoc.description()));
        if (StringUtils.isNotBlank(parameterDoc.name()))
            parameter.setName(propertyResolverUtils.resolve(parameterDoc.name()));
        if (StringUtils.isNotBlank(parameterDoc.in().toString()))
            parameter.setIn(parameterDoc.in().toString());
        if (StringUtils.isNotBlank(parameterDoc.example())) {
            try {
                parameter.setExample(Json.mapper().readTree(parameterDoc.example()));
            } catch (IOException e) {
                parameter.setExample(parameterDoc.example());
            }
        }
        if (parameterDoc.deprecated())
            parameter.setDeprecated(parameterDoc.deprecated());
        if (parameterDoc.required())
            parameter.setRequired(parameterDoc.required());
        if (parameterDoc.allowEmptyValue())
            parameter.setAllowEmptyValue(parameterDoc.allowEmptyValue());
        if (parameterDoc.allowReserved())
            parameter.setAllowReserved(parameterDoc.allowReserved());

        setSchema(parameterDoc, components, jsonView, parameter);
        setExamples(parameterDoc, parameter);
        setExtensions(parameterDoc, parameter);
        setParameterStyle(parameter, parameterDoc);
        setParameterExplode(parameter, parameterDoc);

        return parameter;
    }

    /**
     * Sets schema.
     *
     * @param parameterDoc the parameter doc
     * @param components   the components
     * @param jsonView     the json view
     * @param parameter    the parameter
     */
    private void setSchema(io.swagger.v3.oas.annotations.Parameter parameterDoc, Components components, JsonView jsonView, Parameter parameter) {
        if (StringUtils.isNotBlank(parameterDoc.ref()))
            parameter.$ref(parameterDoc.ref());
        else {
            Schema schema = null;
            try {
                schema = AnnotationsUtils.getSchema(parameterDoc.schema(), null, false, parameterDoc.schema().implementation(), components, jsonView).orElse(null);
            } catch (Exception e) {
                LOGGER.warn(Constants.GRACEFUL_EXCEPTION_OCCURRED, e);
            }
            if (schema == null) {
                if (parameterDoc.content().length > 0)
                    schema = AnnotationsUtils.getSchema(parameterDoc.content()[0], components, jsonView).orElse(null);
                else
                    schema = AnnotationsUtils.getArraySchema(parameterDoc.array(), components, jsonView).orElse(null);
            }
            parameter.setSchema(schema);
        }
    }

    /**
     * Calculate schema schema.
     *
     * @param components      the components
     * @param parameterInfo   the parameter info
     * @param requestBodyInfo the request body info
     * @param jsonView        the json view
     * @return the schema
     */
    Schema calculateSchema(Components components, ParameterInfo parameterInfo, RequestBodyInfo requestBodyInfo, JsonView jsonView) {
        Schema schemaN;
        String paramName = parameterInfo.getpName();
        MethodParameter methodParameter = parameterInfo.getMethodParameter();

        if (parameterInfo.getParameterModel() == null || parameterInfo.getParameterModel().getSchema() == null) {
            Type type = ReturnTypeParser.getType(methodParameter);
            // if its not a file type or a string but has an argument extractor annotation, default type to string
            if (!isFile(parameterInfo.getMethodParameter().getParameterType()) && !String.class.isAssignableFrom(parameterInfo.getMethodParameter().getParameterType())) {
                if (hasContentExtractorAnnotation(parameterInfo)) {
                    type = String.class;
                }
            }
            schemaN = NinjaDocAnnotationsUtils.extractSchema(components, type, jsonView, methodParameter.getParameterAnnotations());
        } else
            schemaN = parameterInfo.getParameterModel().getSchema();

        if (requestBodyInfo != null) {
            schemaN = calculateRequestBodySchema(components, parameterInfo, requestBodyInfo, schemaN, paramName);
        }

        return schemaN;
    }

    private boolean hasContentExtractorAnnotation(ParameterInfo parameterInfo) {
        List<? extends Annotation> annotations = parameterInfo.getAnnotations();
        if (annotations.isEmpty()) {
            return false;
        } else {
            for (Annotation annotation : annotations) {
                return annotation.annotationType().isAnnotationPresent(WithArgumentExtractor.class)
                        || annotation.annotationType().isAnnotationPresent(WithArgumentExtractors.class);
            }
        }
        return false;
    }

    /**
     * Calculate request body schema schema.
     *
     * @param components      the components
     * @param parameterInfo   the parameter info
     * @param requestBodyInfo the request body info
     * @param schemaN         the schema n
     * @param paramName       the param name
     * @return the schema
     */
    private Schema calculateRequestBodySchema(Components components, ParameterInfo parameterInfo, RequestBodyInfo requestBodyInfo, Schema schemaN, String paramName) {
        if (schemaN != null && StringUtils.isEmpty(schemaN.getDescription()) && parameterInfo.getParameterModel() != null) {
            String description = parameterInfo.getParameterModel().getDescription();
            if (schemaN.get$ref() != null && schemaN.get$ref().contains(AnnotationsUtils.COMPONENTS_REF)) {
                String key = schemaN.get$ref().substring(21);
                Schema existingSchema = components.getSchemas().get(key);
                existingSchema.setDescription(description);
            } else
                schemaN.setDescription(description);
        }

        if (requestBodyInfo.getMergedSchema() != null) {
            requestBodyInfo.getMergedSchema().addProperties(paramName, schemaN);
            schemaN = requestBodyInfo.getMergedSchema();
        } else if (schemaN instanceof FileSchema || schemaN instanceof ArraySchema && ((ArraySchema) schemaN).getItems() instanceof FileSchema) {
            schemaN = new ObjectSchema().addProperties(paramName, schemaN);
            requestBodyInfo.setMergedSchema(schemaN);
        } else
            requestBodyInfo.addProperties(paramName, schemaN);
        return schemaN;
    }

    /**
     * Is annotation to ignore boolean.
     *
     * @param parameter the parameter
     * @return the boolean
     */
    @SuppressWarnings("unchecked")
    public boolean isAnnotationToIgnore(MethodParameter parameter) {
        return ANNOTATIOSN_TO_IGNORE.stream().anyMatch(
                annotation -> parameter.getParameterAnnotation(annotation) != null
                        || AnnotationUtils.findAnnotation(parameter.getParameterType(), annotation) != null);
    }

    /**
     * Sets examples.
     *
     * @param parameterDoc the parameter doc
     * @param parameter    the parameter
     */
    private void setExamples(io.swagger.v3.oas.annotations.Parameter parameterDoc, Parameter parameter) {
        Map<String, Example> exampleMap = new HashMap<>();
        if (parameterDoc.examples().length == 1 && StringUtils.isBlank(parameterDoc.examples()[0].name())) {
            Optional<Example> exampleOptional = AnnotationsUtils.getExample(parameterDoc.examples()[0]);
            exampleOptional.ifPresent(parameter::setExample);
        } else {
            for (ExampleObject exampleObject : parameterDoc.examples()) {
                AnnotationsUtils.getExample(exampleObject)
                        .ifPresent(example -> exampleMap.put(exampleObject.name(), example));
            }
        }
        if (exampleMap.size() > 0) {
            parameter.setExamples(exampleMap);
        }
    }

    /**
     * Sets extensions.
     *
     * @param parameterDoc the parameter doc
     * @param parameter    the parameter
     */
    private void setExtensions(io.swagger.v3.oas.annotations.Parameter parameterDoc, Parameter parameter) {
        if (parameterDoc.extensions().length > 0) {
            Map<String, Object> extensionMap = AnnotationsUtils.getExtensions(parameterDoc.extensions());
            extensionMap.forEach(parameter::addExtension);
        }
    }

    /**
     * Sets parameter explode.
     *
     * @param parameter the parameter
     * @param p         the p
     */
    private void setParameterExplode(Parameter parameter, io.swagger.v3.oas.annotations.Parameter p) {
        if (isExplodable(p)) {
            if (Explode.TRUE.equals(p.explode())) {
                parameter.setExplode(Boolean.TRUE);
            } else if (Explode.FALSE.equals(p.explode())) {
                parameter.setExplode(Boolean.FALSE);
            }
        }
    }

    /**
     * Sets parameter style.
     *
     * @param parameter the parameter
     * @param p         the p
     */
    private void setParameterStyle(Parameter parameter, io.swagger.v3.oas.annotations.Parameter p) {
        if (StringUtils.isNotBlank(p.style().toString())) {
            parameter.setStyle(Parameter.StyleEnum.valueOf(p.style().toString().toUpperCase()));
        }
    }

    /**
     * Is explodable boolean.
     *
     * @param p the p
     * @return the boolean
     */
    private boolean isExplodable(io.swagger.v3.oas.annotations.Parameter p) {
        io.swagger.v3.oas.annotations.media.Schema schema = p.schema();
        boolean explode = true;
        Class<?> implementation = schema.implementation();
        if (implementation == Void.class && !schema.type().equals("object") && !schema.type().equals("array")) {
            explode = false;
        }
        return explode;
    }

    /**
     * Is file boolean.
     *
     * @param methodParameter the method parameter
     * @return the boolean
     */
    public boolean isFile(MethodParameter methodParameter) {
        if (methodParameter.getGenericParameterType() instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) methodParameter.getGenericParameterType();
            return isFile(parameterizedType);
        } else {
            Class type = methodParameter.getParameterType();
            return isFile(type);
        }
    }

    /**
     * Is file boolean.
     *
     * @param parameterizedType the parameterized type
     * @return the boolean
     */
    private boolean isFile(ParameterizedType parameterizedType) {
        Type type = parameterizedType.getActualTypeArguments()[0];
        Class fileClass = ResolvableType.forType(type).getRawClass();
        if (fileClass != null && isFile(fileClass))
            return true;
        else if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) type;
            Type[] upperBounds = wildcardType.getUpperBounds();
            return FileItem.class.getName().equals(upperBounds[0].getTypeName());
        }
        return false;
    }
}
