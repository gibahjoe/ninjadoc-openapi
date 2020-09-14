/**
 * Copyright (C) 2012-2018 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.devappliance.ninjadoc.config;

import com.devappliance.ninjadoc.*;
import com.devappliance.ninjadoc.controllers.MultipleOpenApiResource;
import com.devappliance.ninjadoc.converters.FileSupportConverter;
import com.devappliance.ninjadoc.converters.ModelConverterRegistrar;
import com.devappliance.ninjadoc.converters.ResponseSupportConverter;
import com.devappliance.ninjadoc.converters.SchemaPropertyDeprecatingConverter;
import com.devappliance.ninjadoc.customizers.OpenApiBuilderCustomiser;
import com.devappliance.ninjadoc.customizers.OpenApiCustomiser;
import com.devappliance.ninjadoc.customizers.OperationCustomizer;
import com.devappliance.ninjadoc.customizers.ParameterCustomizer;
import com.devappliance.ninjadoc.util.AfterInjectionListener;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.google.inject.*;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.OptionalBinder;
import com.google.inject.multibindings.ProvidesIntoSet;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.oas.models.OpenAPI;
import ninja.AssetsController;
import ninja.utils.NinjaProperties;

import java.io.IOException;
import java.util.*;

import static com.devappliance.ninjadoc.NinjaDocUtils.getConfig;

@Singleton
public class NinjaDocModule extends AbstractModule {
    static {
        getConfig().addHiddenRestControllers(AssetsController.class);
    }

    private NinjaProperties ninjaProperties;

    @Inject
    public NinjaDocModule(NinjaProperties ninjaProperties) {
        super();
        this.ninjaProperties = ninjaProperties;
    }

    protected void configure() {
        bindListener(new AbstractMatcher<TypeLiteral<?>>() {
            public boolean matches(TypeLiteral<?> typeLiteral) {
                return AfterInjectionListener.class.isAssignableFrom(typeLiteral.getRawType());
            }
        }, new TypeListener() {
            @Override
            public <I> void hear(final TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
                typeEncounter.register((InjectionListener<I>) i -> {
                    AfterInjectionListener m = (AfterInjectionListener) i;
                    m.postInject();
                });
            }
        });
        bind(NinjaDocConfig.class);
        Multibinder.newSetBinder(binder(), ReturnTypeParser.class)
                .addBinding()
                .toInstance(new ReturnTypeParser() {
                });
        Multibinder.newSetBinder(binder(), ModelConverter.class)
                .addBinding()
                .to(FileSupportConverter.class);
        OptionalBinder.newOptionalBinder(binder(), OpenAPI.class);
        OptionalBinder.newOptionalBinder(binder(), new TypeLiteral<Set<ModelConverter>>() {
        });
        OptionalBinder.newOptionalBinder(binder(), new TypeLiteral<Set<OpenApiBuilderCustomiser>>() {
        });
        OptionalBinder.newOptionalBinder(binder(), new TypeLiteral<Set<OpenApiCustomiser>>() {
        });
        OptionalBinder.newOptionalBinder(binder(), new TypeLiteral<Set<OperationCustomizer>>() {
        });
        OptionalBinder.newOptionalBinder(binder(), new TypeLiteral<Set<OpenApiBuilderCustomiser>>() {
        });
        OptionalBinder.newOptionalBinder(binder(), new TypeLiteral<Set<ReturnTypeParser>>() {
        });
        OptionalBinder.newOptionalBinder(binder(), new TypeLiteral<Set<ReturnTypeParser>>() {
        });
        OptionalBinder.newOptionalBinder(binder(), new TypeLiteral<Set<ParameterCustomizer>>() {
        });
        bind(AbstractRequestBuilder.class).to(RequestBuilder.class);
        if (ninjaProperties.getBooleanWithDefault(ConfigKeys.NINJADOC_DEPRECATING_CONVERTER_ENABLED, NinjaDocDefaultConfig.NINJADOC_DEPRECATING_CONVERTER_ENABLED)) {
            bind(ModelConverter.class).toInstance(new SchemaPropertyDeprecatingConverter());
        }
        if (ninjaProperties.getBooleanWithDefault(ConfigKeys.NINJADOC_SWAGGER_UI_ENABLED, NinjaDocDefaultConfig.SWAGGER_UI_ENABLED)) {
            install(new NinjaDocSwaggerUiModule(ninjaProperties));
        }
        try {
            if (getPropertiesAsType("ninjadoc.", NinjaDocConfigProperties.class).getApiDocs().getGroups().isEnabled()) {
                bind(MultipleOpenApiResource.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ProvidesIntoSet
    public ResponseSupportConverter responseSupportConverter() {
        return new ResponseSupportConverter();
    }

    @Provides
    @Singleton
    public GenericResponseBuilder responseBuilder(OperationBuilder operationBuilder, Set<ReturnTypeParser> returnTypeParsers,
                                                  NinjaDocConfigProperties ninjaDocConfigProperties, PropertyResolverUtils propertyResolverUtils) {
        return new GenericResponseBuilder(operationBuilder, new ArrayList<>(returnTypeParsers), ninjaDocConfigProperties, propertyResolverUtils);
    }

    @Provides
    @Singleton
    public NinjaDocConfigProperties responseBuilder() throws IOException {
        return getPropertiesAsType("ninjadoc.", NinjaDocConfigProperties.class);
    }

    @Provides
    @Singleton
    public ModelConverterRegistrar modelConverterRegistrar(Optional<Set<ModelConverter>> modelConverters) {
        return new ModelConverterRegistrar(modelConverters.orElse(Collections.emptySet()));
    }

    @Provides
    @Singleton
    public OperationBuilder operationBuilder(GenericParameterBuilder parameterBuilder, RequestBodyBuilder requestBodyBuilder,
                                             SecurityParser securityParser, PropertyResolverUtils propertyResolverUtils) {
        return new OperationBuilder(parameterBuilder, requestBodyBuilder,
                securityParser, propertyResolverUtils);
    }

    private <T> T getPropertiesAsType(String propertyPrefix, Class<T> tClass) throws IOException {
        Properties builtProps = new Properties();
        Properties properties = ninjaProperties.getAllCurrentNinjaProperties();
        properties.forEach((key1, value) -> {
            String key = (String) key1;
            if (key.startsWith(propertyPrefix)) {
                builtProps.put(key.replace(propertyPrefix, ""), value);
            }
        });
        JavaPropsMapper mapper = new JavaPropsMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
//        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);
        return mapper.readPropertiesAs(builtProps, tClass);
    }
}
