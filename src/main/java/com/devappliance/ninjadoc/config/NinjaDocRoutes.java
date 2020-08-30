package com.devappliance.ninjadoc.config;

import com.devappliance.ninjadoc.NinjaDocConfigProperties;
import com.devappliance.ninjadoc.SwaggerUiConfigProperties;
import com.devappliance.ninjadoc.controllers.MultipleOpenApiResource;
import com.devappliance.ninjadoc.controllers.OpenApiResource;
import com.devappliance.ninjadoc.controllers.SwaggerWelcome;
import com.devappliance.ninjadoc.util.Constants;
import com.google.inject.Inject;
import ninja.Router;
import ninja.utils.NinjaProperties;

import javax.inject.Named;

/**
 * @author Gibah Joseph
 * email: gibahjoe@gmail.com
 * Jul, 2020
 **/
@Named
public class NinjaDocRoutes {
    @Inject
    private NinjaProperties ninjaProperties;
    @Inject
    private NinjaDocConfigProperties ninjaDocConfigProperties;
    @Inject
    private SwaggerUiConfigProperties swaggerUiConfigProperties;

    public void register(Router router) {
        router.GET().route(ninjaDocConfigProperties.getApiDocs().getPath()).with(OpenApiResource::openapiJson);
        router.GET().route(String.format("%s.yaml", ninjaDocConfigProperties.getApiDocs().getPath())).with(OpenApiResource::openapiYaml);
        router.GET().route(String.format("%s/%s", ninjaDocConfigProperties.getApiDocs().getPath(), Constants.SWAGGGER_CONFIG_FILE)).with(SwaggerWelcome::getSwaggerUiConfig);
        if (swaggerUiConfigProperties.isEnabled()) {
            router.GET().route(swaggerUiConfigProperties.getPath()).with(SwaggerWelcome::regirectUi);
            router.GET().route("/swagger-ui/index.html").with(SwaggerWelcome::swaggerUi);
            router.GET().route("/swagger-ui/{fileName: .*}").with(SwaggerWelcome::swaggerResources);
        }
        if (ninjaDocConfigProperties.getApiDocs().getGroups().isEnabled()) {
            router.GET().route(ninjaDocConfigProperties.getApiDocs().getPath() + "/{group}").with(MultipleOpenApiResource::openapiJson);
            router.GET().route(String.format("%s.yaml%s", ninjaDocConfigProperties.getApiDocs().getPath(), "/{group}")).with(MultipleOpenApiResource::openapiYaml);
        }
    }
}
