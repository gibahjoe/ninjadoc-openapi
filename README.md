[![](https://jitpack.io/v/gibahjoe/ninjadoc-openapi.svg)](https://jitpack.io/#gibahjoe/ninjadoc-openapi) ![Maven Central](https://img.shields.io/maven-central/v/com.devappliance.ninjadoc/ninjadoc-openapi)


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

## Installation
NinjaDoc can be installed in as few as the simple steps outlined below.

Add the dependency to your pom.xml
```xml
   <dependency>
      <groupId>com.devappliance.ninjadoc</groupId>
      <artifactId>ninjadoc-openapi</artifactId>
      <version>last-release-version</version>
   </dependency>
```
Register the routes exposed by NinjaDoc in your Routes.java
```java
public class Routes implements ApplicationRoutes {

    @Inject
    private NinjaDocRoutes ninjaDocRoutes;

    @Override
    public void init(Router router) {  
        ninjaDocRoutes.register(router);
        // Other application routes
    }

}
```

Install Ninjadoc module in your Module.java

```java
@Singleton
public class Module extends AbstractModule {
    private final NinjaProperties ninjaProperties;

    @Inject
    public Module(NinjaProperties ninjaProperties) {
        this.ninjaProperties = ninjaProperties;
    }

    protected void configure() {
        install(new NinjaDocModule(ninjaProperties));
        // Other application modules
    }

}
```

In order to correctly document return objects in your controllers, instead of the default _Result.java_, NinjaDoc provides a custom annotation to do this for you.
Annotate controller methods with this annotation so that correct return types are generated. This is optional but recommended.
```java
 @Singleton
 public class ApplicationController {
 
     public Result index() {
 
         return Results.html();
 
     }
//    Add this annotation to generate correct return types
     @DocumentReturnType(type = SimplePojo.class)
     public Result helloWorldJson() {
         
         SimplePojo simplePojo = new SimplePojo();
         simplePojo.content = "Hello World! Hello Json!";
 
         return Results.json().render(simplePojo);
 
     }
     
     public static class SimplePojo {
 
         public String content;
         
     }
 }
```

This step is optional: For custom path of the swagger documentation in HTML format, add a custom ninjadoc property, in your application.conf file:

```properties
# swagger-ui custom path
ninjadoc.swagger-ui.path=/swagger-ui.html
```
Enjoy.

## Disclaimer
NinjaDoc was inspired by SpringDoc. In fact, it is a clone of SpringDoc but heavily modified to suit ninja.
This means that all features and configuration supported by SpringDoc, as at the releases between March 2020 and August 2020 (I honestly cannot remember when), are also supported by NinjaDoc.
**If there are any features that were missed or not fully implemented, please raise an issue.**

Checkout the example project for implementation details.
