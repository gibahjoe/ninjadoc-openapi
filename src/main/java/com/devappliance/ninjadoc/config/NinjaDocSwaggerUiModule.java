package com.devappliance.ninjadoc.config;

import com.devappliance.ninjadoc.SwaggerUiConfigParameters;
import com.devappliance.ninjadoc.SwaggerUiConfigProperties;
import com.devappliance.ninjadoc.SwaggerUiOAuthProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import ninja.utils.NinjaProperties;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Gibah Joseph
 * email: gibahjoe@gmail.com
 * Jul, 2020
 **/
public class NinjaDocSwaggerUiModule extends AbstractModule {
    private NinjaProperties ninjaProperties;

    public NinjaDocSwaggerUiModule(NinjaProperties ninjaProperties) {
        this.ninjaProperties = ninjaProperties;
    }

    @Override
    protected void configure() {

    }


    @Provides
    @Singleton
    public SwaggerUiConfigParameters swaggerUiConfigParameters(SwaggerUiConfigProperties swaggerUiConfigProperties) throws IOException {
        return new SwaggerUiConfigParameters(swaggerUiConfigProperties);
    }

    @Provides
    @Singleton
    public SwaggerUiConfigProperties swaggerUiConfigProperties() throws IOException {
        return getPropertiesAsType("ninjadoc.swagger-ui.", SwaggerUiConfigProperties.class);
    }

    @Provides
    @Singleton
    public SwaggerUiOAuthProperties swaggerUiOAuthProperties() throws IOException {
        return getPropertiesAsType("ninjadoc.swagger-ui.oauth.", SwaggerUiOAuthProperties.class);
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
