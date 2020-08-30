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

package com.devappliance.ninjadoc;

import com.devappliance.ninjadoc.converters.AdditionalModelsConverter;
import com.devappliance.ninjadoc.converters.ConverterUtils;
import com.devappliance.ninjadoc.converters.SchemaPropertyDeprecatingConverter;
import io.swagger.v3.oas.models.media.Schema;

import java.lang.annotation.Annotation;
import java.util.function.Predicate;

/**
 * The type Spring doc utils.
 *
 * @author bnasslahsen
 */
public class NinjaDocUtils {

    /**
     * The constant springDocConfig.
     */
    private static final NinjaDocUtils springDocConfig = new NinjaDocUtils();

    /**
     * Instantiates a new Spring doc utils.
     */
    private NinjaDocUtils() {
    }

    /**
     * Gets config.
     *
     * @return the config
     */
    public static NinjaDocUtils getConfig() {
        return springDocConfig;
    }

    /**
     * Add deprecated type spring doc utils.
     *
     * @param cls the cls
     * @return the spring doc utils
     */
    public NinjaDocUtils addDeprecatedType(Class<? extends Annotation> cls) {
        SchemaPropertyDeprecatingConverter.addDeprecatedType(cls);
        return this;
    }

    /**
     * Add rest controllers spring doc utils.
     *
     * @param classes the classes
     * @return the spring doc utils
     */
    public NinjaDocUtils addRestControllers(Class<?>... classes) {
        AbstractOpenApiResource.addRestControllers(classes);
        return this;
    }

    /**
     * Add controller classes to hide from generated spec. Spec will not be generated for these controllers
     *
     * @param classes The controllers to hide
     * @return builder
     */
    public NinjaDocUtils addHiddenRestControllers(Class<?>... classes) {
        AbstractOpenApiResource.addHiddenRestControllers(classes);
        return this;
    }

    /**
     * Add controller classes to hide from generated spec. Spec will not be generated for these controllers
     *
     * @param classes The name of controllers to hide
     * @return builder
     */
    public NinjaDocUtils addHiddenRestControllers(String... classes) {
        AbstractOpenApiResource.addHiddenRestControllers(classes);
        return this;
    }

    /**
     * Replace with class spring doc utils.
     *
     * @param source the source
     * @param target the target
     * @return the spring doc utils
     */
    public NinjaDocUtils replaceWithClass(Class source, Class target) {
        AdditionalModelsConverter.replaceWithClass(source, target);
        return this;
    }

    /**
     * Replace with schema spring doc utils.
     *
     * @param source the source
     * @param target the target
     * @return the spring doc utils
     */
    public NinjaDocUtils replaceWithSchema(Class source, Schema target) {
        AdditionalModelsConverter.replaceWithSchema(source, target);
        return this;
    }

    /**
     * Add request wrapper to ignore spring doc utils.
     *
     * @param classes the classes
     * @return the spring doc utils
     */
    public NinjaDocUtils addRequestWrapperToIgnore(Class<?>... classes) {
        AbstractRequestBuilder.addRequestWrapperToIgnore(classes);
        return this;
    }

    /**
     * Remove request wrapper to ignore spring doc utils.
     *
     * @param classes the classes
     * @return the spring doc utils
     */
    public NinjaDocUtils removeRequestWrapperToIgnore(Class<?>... classes) {
        AbstractRequestBuilder.removeRequestWrapperToIgnore(classes);
        return this;
    }

    /**
     * Add file type spring doc utils.
     *
     * @param classes the classes
     * @return the spring doc utils
     */
    public NinjaDocUtils addFileType(Class<?>... classes) {
        GenericParameterBuilder.addFileType(classes);
        return this;
    }

    /**
     * Add response wrapper to ignore spring doc utils.
     *
     * @param cls the cls
     * @return the spring doc utils
     */
    public NinjaDocUtils addResponseWrapperToIgnore(Class<?> cls) {
        ConverterUtils.addResponseWrapperToIgnore(cls);
        return this;
    }

    /**
     * Remove response wrapper to ignore spring doc utils.
     *
     * @param cls the cls
     * @return the spring doc utils
     */
    public NinjaDocUtils removeResponseWrapperToIgnore(Class<?> cls) {
        ConverterUtils.removeResponseWrapperToIgnore(cls);
        return this;
    }

    /**
     * Add response type to ignore spring doc utils.
     *
     * @param cls the cls
     * @return the spring doc utils
     */
    public NinjaDocUtils addResponseTypeToIgnore(Class<?> cls) {
        ConverterUtils.addResponseTypeToIgnore(cls);
        return this;
    }

    /**
     * Remove response type to ignore spring doc utils.
     *
     * @param cls the cls
     * @return the spring doc utils
     */
    public NinjaDocUtils removeResponseTypeToIgnore(Class<?> cls) {
        ConverterUtils.removeResponseTypeToIgnore(cls);
        return this;
    }

    /**
     * Add annotations to ignore spring doc utils.
     *
     * @param classes the classes
     * @return the spring doc utils
     */
    public NinjaDocUtils addAnnotationsToIgnore(Class<?>... classes) {
        GenericParameterBuilder.addAnnotationsToIgnore(classes);
        return this;
    }

    /**
     * Remove annotations to ignore spring doc utils.
     *
     * @param classes the classes
     * @return the spring doc utils
     */
    public NinjaDocUtils removeAnnotationsToIgnore(Class<?>... classes) {
        GenericParameterBuilder.removeAnnotationsToIgnore(classes);
        return this;
    }

    /**
     * Add flux wrapper to ignore spring doc utils.
     *
     * @param cls the cls
     * @return the spring doc utils
     */
    public NinjaDocUtils addFluxWrapperToIgnore(Class<?> cls) {
        ConverterUtils.addFluxWrapperToIgnore(cls);
        return this;
    }

    /**
     * Remove flux wrapper to ignore spring doc utils.
     *
     * @param cls the cls
     * @return the spring doc utils
     */
    public NinjaDocUtils removeFluxWrapperToIgnore(Class<?> cls) {
        ConverterUtils.removeFluxWrapperToIgnore(cls);
        return this;
    }

    /**
     * Add simple types for parameter object spring doc utils.
     *
     * @param classes the classes
     * @return the spring doc utils
     */
    public NinjaDocUtils addSimpleTypesForParameterObject(Class<?>... classes) {
        MethodParameterPojoExtractor.addSimpleTypes(classes);
        return this;
    }

    /**
     * Remove simple types for parameter object spring doc utils.
     *
     * @param classes the classes
     * @return the spring doc utils
     */
    public NinjaDocUtils removeSimpleTypesForParameterObject(Class<?>... classes) {
        MethodParameterPojoExtractor.removeSimpleTypes(classes);
        return this;
    }

    /**
     * Add simple type predicate for parameter object spring doc utils.
     *
     * @param predicate the predicate
     * @return the spring doc utils
     */
    public NinjaDocUtils addSimpleTypePredicateForParameterObject(Predicate<Class<?>> predicate) {
        MethodParameterPojoExtractor.addSimpleTypePredicate(predicate);
        return this;
    }

    /**
     * Disable replacement spring doc utils.
     *
     * @param source the source
     * @return the spring doc utils
     */
    public NinjaDocUtils disableReplacement(Class source) {
        AdditionalModelsConverter.disableReplacement(source);
        return this;
    }

}

