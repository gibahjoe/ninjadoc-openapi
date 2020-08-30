# **Introduction**

The ninjadoc-openapi Java library helps automating the generation of API documentation using Ninja projects.
ninjadoc-openapi works by examining an application at runtime to infer API semantics based on Ninja configurations, class structure and various annotations.

The library automatically generates documentation in JSON/YAML and HTML formatted pages. The generateed documentation can be complemented using `swagger-api` annotations.

This library supports:
*  OpenAPI 3
*  Ninja v6.5.0 minimum
*  JSR-303, specifically for @NotNull, @Min, @Max, and @Size.
*  Swagger-ui
*  Oauth 2 

This is a community-based project, not maintained by the Ninja Framework Contributors

# **Getting Started**

## Library for ninjadoc-openapi integration with ninja and swagger-ui 
*   Automatically deploys swagger-ui to a Ninja application
*   Documentation will be available in HTML format, using the official [swagger-ui jars](https://github.com/swagger-api/swagger-ui.git).
*   The Swagger UI page should then be available at http://server:port/context-path/swagger-ui.html and the OpenAPI description will be available at the following url for json format: http://server:port/context-path/v3/api-docs
    * `server`: The server name or IP
    * `port`: The server port
    * `context-path`: The context path of the application
*   Documentation can be available in yaml format as well, on the following path: /v3/api-docs.yaml

```xml
   <dependency>
      <groupId>com.devappliance.ninjadoc</groupId>
      <artifactId>ninjadoc-openapi</artifactId>
      <version>last-release-version</version>
   </dependency>
```
*   This step is optional: For custom path of the swagger documentation in HTML format, add a custom ninjadoc property, in your spring-boot configuration file:

```properties
# swagger-ui custom path
ninjadoc.swagger-ui.path=/swagger-ui.html
```
