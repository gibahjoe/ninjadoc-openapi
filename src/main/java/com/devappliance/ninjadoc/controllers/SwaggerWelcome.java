
package com.devappliance.ninjadoc.controllers;

import com.devappliance.ninjadoc.NinjaDocConfigProperties;
import com.devappliance.ninjadoc.SwaggerUiConfigParameters;
import com.devappliance.ninjadoc.SwaggerUiConfigProperties;
import com.devappliance.ninjadoc.util.Constants;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import io.swagger.v3.oas.annotations.Operation;
import ninja.*;
import ninja.params.PathParam;
import ninja.utils.HttpCacheToolkit;
import ninja.utils.MimeTypes;
import ninja.utils.ResponseStreams;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.util.AntPathMatcher.DEFAULT_PATH_SEPARATOR;

/**
 * The type Swagger welcome.
 *
 * @author bnasslahsen
 */
public class SwaggerWelcome extends AbstractSwaggerWelcome {

    private final HttpCacheToolkit httpCacheToolkit;
    private final MimeTypes mimeTypes;
    private final ReverseRouter reverseRouter;
    /**
     * The Web jars prefix url.
     */
    private String webJarsPrefixUrl;
    /**
     * The Oauth prefix.
     */
    private URIBuilder oauthPrefix;
    private AssetsControllerHelper assetsControllerHelper;

    /**
     * Instantiates a new Swagger welcome.
     *
     * @param swaggerUiConfig           the swagger ui config
     * @param ninjaDocConfigProperties  the spring doc config properties
     * @param swaggerUiConfigParameters the swagger ui config parameters
     * @param assetsControllerHelper
     * @param httpCacheToolkit
     * @param mimeTypes
     * @param reverseRouter
     */
    @Inject
    public SwaggerWelcome(SwaggerUiConfigProperties swaggerUiConfig, NinjaDocConfigProperties ninjaDocConfigProperties,
                          SwaggerUiConfigParameters swaggerUiConfigParameters, AssetsControllerHelper assetsControllerHelper, HttpCacheToolkit httpCacheToolkit, MimeTypes mimeTypes, ReverseRouter reverseRouter) {
        super(swaggerUiConfig, ninjaDocConfigProperties, swaggerUiConfigParameters);
        this.webJarsPrefixUrl = ninjaDocConfigProperties.getWebjars().getPrefix();
        this.assetsControllerHelper = assetsControllerHelper;
        this.httpCacheToolkit = httpCacheToolkit;
        this.mimeTypes = mimeTypes;
        this.reverseRouter = reverseRouter;
    }

    /**
     * Service the swagger UI from the webjar
     *
     * @return
     */
    @Operation(hidden = true)
    public Result regirectUi(Context context) throws URISyntaxException {
        buildConfigUrl(context.getContextPath(), new URIBuilder()
                .setScheme(context.getScheme())
                .setHost(context.getHostname())
                .setPath(context.getContextPath()));
        String sbUrl = this.buildUrl("", new URIBuilder(reverseRouter.with(SwaggerWelcome::swaggerUi).build()).getPath());
        URIBuilder uriBuilder = getUriComponentsBuilder(sbUrl);
        return Results.redirect(uriBuilder.build().toString());
    }

    /**
     * Service the swagger UI from the webjar
     *
     * @return
     */
    @Operation(hidden = true)
    public Result swaggerUi() {
        Object renderable = (Renderable) (context1, result) -> {
            String fileName = Constants.SWAGGER_UI_URL;
            URL url = getStaticFileFromMetaInfResourcesDir(fileName);
            streamOutUrlEntity(url, context1, result);
        };
        return Results.ok().render(renderable);
    }

    /**
     * Service the swagger UI from the webjar
     *
     * @return
     */
    @Operation(hidden = true)
    public Result swaggerResources(@PathParam("fileName") String file) {
        Object renderable = (Renderable) (context1, result) -> {
            String fileName = Constants.SWAGGER_RES_BASE + file;
            URL url = getStaticFileFromMetaInfResourcesDir(fileName);
            streamOutUrlEntity(url, context1, result);
        };
        return Results.ok().render(renderable);
    }


    /**
     * Gets swagger ui config.
     *
     * @return the swagger ui config
     */
    @Operation(hidden = true)
    public Result getSwaggerUiConfig(Context context) throws URISyntaxException {
        buildConfigUrl(context.getContextPath(), new URIBuilder()
                .setScheme(context.getScheme())
                .setHost(context.getHostname())
                .setPath(context.getContextPath()));
        return Results.json().render(swaggerUiConfigParameters.getConfigParameters());
    }

    @Override
    protected void calculateUiRootPath(StringBuilder... sbUrls) {
        StringBuilder sbUrl = new StringBuilder();
        if (ArrayUtils.isNotEmpty(sbUrls))
            sbUrl = sbUrls[0];
        String swaggerPath = swaggerUiConfigParameters.getPath();
        if (swaggerPath.contains(DEFAULT_PATH_SEPARATOR))
            sbUrl.append(swaggerPath, 0, swaggerPath.lastIndexOf(DEFAULT_PATH_SEPARATOR));
        swaggerUiConfigParameters.setUiRootPath(sbUrl.toString());
    }

    @Override
    protected void calculateOauth2RedirectUrl(URIBuilder uriComponentsBuilder) throws URISyntaxException {
        if (!swaggerUiConfigParameters.isValidUrl(swaggerUiConfigParameters.getOauth2RedirectUrl())) {
            String path = constructPath(swaggerUiConfigParameters.getUiRootPath(), swaggerUiConfigParameters.getOauth2RedirectUrl());
            if (path.startsWith("/")) {
                path = path.replaceFirst("/", "");
            }
            swaggerUiConfigParameters.setOauth2RedirectUrl(uriComponentsBuilder
                    .setPath(path)
                    .build()
                    .toString());
        }
    }

    /**
     * From current context path string.
     *
     * @param request the request
     * @return the string
     */
    private String fromCurrentContextPath(ninja.Context request) throws URISyntaxException {
        String contextPath = request.getContextPath();
        String url = new URIBuilder()
                .setScheme(request.getScheme())
                .setHost(request.getHostname())
                .setPath(constructPath(request.getContextPath(), request.getRequestPath().replaceFirst("/", "")))
                .build()
                .toString();
        url = url.replace(request.getRequestPath(), "");
        buildConfigUrl(contextPath, new URIBuilder(url));
        return contextPath;
    }

    private String constructPath(String... segments) {
        return Stream.of(segments)
                .filter(StringUtils::isNotBlank)
                .map(s -> {
                    if (s.startsWith("/")) {
                        return s.replaceFirst("/", "");
                    }
                    return s;
                })
                .collect(Collectors.joining("/"));
    }

    private URL getStaticFileFromMetaInfResourcesDir(String fileName) {
        String finalNameWithoutLeadingSlash
                = assetsControllerHelper.normalizePathWithoutLeadingSlash(fileName, true);
        URL url = null;
        url = this.getClass().getClassLoader().getResource("META-INF/resources/webjars/" + finalNameWithoutLeadingSlash);
        return url;
    }

    private void streamOutUrlEntity(URL url, Context context, Result result) {
        // check if stream exists. if not print a notfound exception
        if (url == null) {
            context.finalizeHeadersWithoutFlashAndSessionCookie(Results.notFound());
        } else if (assetsControllerHelper.isDirectoryURL(url)) {
            // Disable listing of directory contents
            context.finalizeHeadersWithoutFlashAndSessionCookie(Results.notFound());
        } else {
            try {
                URLConnection urlConnection = url.openConnection();
                Long lastModified = urlConnection.getLastModified();
                httpCacheToolkit.addEtag(context, result, lastModified);

                if (result.getStatusCode() == Result.SC_304_NOT_MODIFIED) {
                    // Do not stream anything out. Simply return 304
                    context.finalizeHeadersWithoutFlashAndSessionCookie(result);
                } else {
                    result.status(200);

                    // Try to set the mimetype:
                    String mimeType = mimeTypes.getContentType(context,
                            url.getFile());

                    if (mimeType != null && !mimeType.isEmpty()) {
                        result.contentType(mimeType);
                    }

                    ResponseStreams responseStreams = context
                            .finalizeHeadersWithoutFlashAndSessionCookie(result);

                    try (InputStream inputStream = urlConnection.getInputStream();
                         OutputStream outputStream = responseStreams.getOutputStream()) {
                        ByteStreams.copy(inputStream, outputStream);
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
