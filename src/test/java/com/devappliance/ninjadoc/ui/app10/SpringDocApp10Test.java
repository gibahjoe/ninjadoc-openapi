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

package com.devappliance.ninjadoc.ui.app10;

import conf.AbstractNinjaDocTest;
import conf.TestPropertySource;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static io.restassured.RestAssured.given;


@TestPropertySource(properties = "springdoc.swagger-ui.disable-swagger-default-url=true")
public class SpringDocApp10Test extends AbstractNinjaDocTest {
    @Override
    protected String getConfigBasePackage() {
        return null;
    }

    @Test
    public void shouldDisplaySwaggerUiPage() throws Exception {
        given()
                .when()
                .get("/swagger-ui/index.html")
                .then().statusCode(200)
                .body(CoreMatchers.containsString("Swagger UI"));
    }

    @Test
    public void shouldFetchSwaggerRes() throws Exception {
        given()
                .when()
                .get("/swagger-ui/swagger-ui.js")
                .then().statusCode(200)
                .body(CoreMatchers.containsString("Function"));
    }

//    @Test
//    public void originalIndex() throws Exception {
//        MvcResult mvcResult = mockMvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk()).andReturn();
//        String transformedIndex = mvcResult.getResponse().getContentAsString();
//        assertTrue(transformedIndex.contains("Swagger UI"));
//        assertEquals(getExpectedResult(), transformedIndex);
//    }
}
