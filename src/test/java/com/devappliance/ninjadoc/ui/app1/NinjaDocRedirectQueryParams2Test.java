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

import conf.AbstractNinjaDocTest;
import io.restassured.response.Response;
import org.junit.Test;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

public class NinjaDocRedirectQueryParams2Test extends AbstractNinjaDocTest {
	@Override
	protected Map<String, String> getConfigsForTest() {
		Map<String, String> configsForTest = super.getConfigsForTest();
		configsForTest.put("ninjadoc.swagger-ui.display-query-params", "true");
		return configsForTest;
	}

	@Test
	public void shouldRedirectWithQueryParams() throws Exception {
		String swaggerUiPath = swaggerUiConfigProperties.getPath();
		Response response = given()
				.when()
				.redirects()
				.follow(false)
				.get(swaggerUiPath);

		String locationHeader = response.getHeader("Location");
		assertEquals("/swagger-ui/index.html?oauth2RedirectUrl=" + getServerAddress() + "swagger-ui/oauth2-redirect.html&url=/v3/api-docs", URLDecoder.decode(locationHeader, StandardCharsets.UTF_8.name()));
	}

}
