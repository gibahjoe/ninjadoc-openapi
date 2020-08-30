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

package com.devappliance.ninjadoc.ui.app2;

import conf.AbstractNinjaDocTest;
import io.restassured.response.Response;
import org.junit.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class NinjaDocSwaggerUiEnabledTest extends AbstractNinjaDocTest {
    @Override
    protected Map<String, String> getConfigsForTest() {
        Map<String, String> configsForTest = super.getConfigsForTest();
        configsForTest.put("ninjadoc.swagger-ui.enabled", "false");
        return configsForTest;
    }

    @Test
    public void test() throws Exception {
        String swaggerUiPath = swaggerUiConfigProperties.getPath();
        Response response = given()
                .when()
                .get(swaggerUiPath);
        response.then().statusCode(404);
    }

    @Override
    protected String getConfigBasePackage() {
        return null;
    }
}
