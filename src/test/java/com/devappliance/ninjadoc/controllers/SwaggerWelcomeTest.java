package com.devappliance.ninjadoc.controllers;

import conf.NinjaDocUiTest;
import io.restassured.response.Response;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;

/**
 * @author Gibah Joseph
 * email: gibahjoe@gmail.com
 * Jul, 2020
 **/
public class SwaggerWelcomeTest extends NinjaDocUiTest {

    @Test
    public void testApp() {
        String swaggerUiPath = swaggerUiConfigProperties.getPath();
        Response response = given()
                .when()
                .get(swaggerUiPath);
        response.then().statusCode(200);
        String string = response.body().asString();
        assertNotNull(string);
    }

    @Override
    protected String getConfigBasePackage() {
        return null;
    }
}
