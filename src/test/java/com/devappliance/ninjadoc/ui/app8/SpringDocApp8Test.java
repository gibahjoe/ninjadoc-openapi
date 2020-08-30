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

package com.devappliance.ninjadoc.ui.app8;

import conf.AbstractNinjaDocTest;
import conf.TestPropertySource;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

@TestPropertySource(properties = {
        "ninjadoc.swagger-ui.urls[0].name=users",
        "ninjadoc.swagger-ui.urls[0].url=/api-docs.yaml"
})
public class SpringDocApp8Test extends AbstractNinjaDocTest {

    @Test
    public void swagger_config_for_multiple_groups() throws Exception {
        given()
                .when()
                .get("/v3/api-docs/swagger-config")
                .then().statusCode(200)
                .assertThat()
                .body("configUrl", is("/v3/api-docs/swagger-config"))
                .body(CoreMatchers.not(CoreMatchers.hasItem("url")))
                .body("urls[0].url", equalTo("/api-docs.yaml"))
                .body("urls[0].name", equalTo("users"));
        ;
    }

    @Override
    protected String getConfigBasePackage() {
        return null;
    }
}
