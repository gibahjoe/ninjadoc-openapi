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
import org.junit.Assert;
import org.junit.Test;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class NinjaDocRedirectConfigUrlTest extends AbstractNinjaDocTest {
    @Override
    protected Map<String, String> getConfigsForTest() {
        Map<String, String> configsForTest = super.getConfigsForTest();
        configsForTest.put(ConfigKeys.SWAGGER_CONFIG_URL, "/foo/bar");
        configsForTest.put(ConfigKeys.NINJADOC_SWAGGERUI_URL, "/batz");
        return configsForTest;
    }

    @Test
    public void shouldRedirectWithConfigUrlIgnoringQueryParams() throws Exception {
        String swaggerUiPath = "/swagger-ui.html";
        Response response = given()
                .when()
                .redirects().follow(false)
                .get(swaggerUiPath);

        String locationHeader = response.header("Location");
        Assert.assertEquals("/swagger-ui/index.html?configUrl=/foo/bar", URLDecoder.decode(locationHeader, StandardCharsets.UTF_8.name()));
    }

}
