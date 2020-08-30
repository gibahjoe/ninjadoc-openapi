package conf;

import io.restassured.response.Response;
import org.hamcrest.CoreMatchers;
import org.json.JSONException;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

/**
 * @author Gibah Joseph
 * Email: gibahjoe@gmail.com
 * Oct, 2019
 **/

public abstract class NinjaDocUiTest extends AbstractNinjaDocTest {

    @Test
    public void testApp() throws IOException, URISyntaxException, JSONException {
        className = getClass().getSimpleName();
        Response response = given()
                .when()
                .get(ninjaDocConfig.getApiDocsUrl());
        response.then().statusCode(200)
                .body("openapi", CoreMatchers.equalTo("3.0.1"));
        String oapi = response.getBody().prettyPeek().asString();
        assertNotNull(oapi);
        URL testResultResource = getClass().getClassLoader().getResource("results/" + className + ".json");
        if (testResultResource == null) {

        }
        Path path = Paths.get(testResultResource.toURI());
        byte[] fileBytes = Files.readAllBytes(path);
        String expected = new String(fileBytes);
        assertEquals(expected, oapi, true);
    }
}
