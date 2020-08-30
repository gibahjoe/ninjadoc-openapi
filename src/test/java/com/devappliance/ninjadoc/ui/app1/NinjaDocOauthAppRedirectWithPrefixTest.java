/*
 *
 *  * Copyright 2019-2020 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.devappliance.ninjadoc.ui.app1;

import com.devappliance.ninjadoc.config.ConfigKeys;
import conf.AbstractNinjaDocTest;
import io.restassured.response.Response;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class NinjaDocOauthAppRedirectWithPrefixTest extends AbstractNinjaDocTest {
    @Override
    protected Map<String, String> getConfigsForTest() {
        Map<String, String> conf = super.getConfigsForTest();
        conf.put(ConfigKeys.SWAGGER_UI_PATH, "/documentation/swagger-ui.html");
        conf.put(ConfigKeys.API_DOCS_URL, "/documentation/v3/api-docs");
//        conf.put(ConfigKeys.NINJADOC_WEBJARS_PREFIX, "/webjars-pref");
        return conf;
    }

    @Test
    public void shouldRedirectWithPrefix() throws Exception {

        String swaggerUiPath = "/documentation/v3/api-docs/swagger-config";
        Response response = given()
                .when()
                .get(swaggerUiPath).peek();
        response.then().statusCode(200);
        response.then().assertThat()
                .body("validatorUrl", CoreMatchers.equalTo(""))
                .body("oauth2RedirectUrl", CoreMatchers.equalTo(getServerAddress() + "documentation/swagger-ui/oauth2-redirect.html"));
    }

}
