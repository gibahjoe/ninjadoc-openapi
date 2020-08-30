/*
 *
 *  *
 *  *  * Copyright 2019-2020 the original author or authors.
 *  *  *
 *  *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  * you may not use this file except in compliance with the License.
 *  *  * You may obtain a copy of the License at
 *  *  *
 *  *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *  *
 *  *  * Unless required by applicable law or agreed to in writing, software
 *  *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  * See the License for the specific language governing permissions and
 *  *  * limitations under the License.
 *  *
 *
 */

package com.devappliance.ninjadoc;

import com.devappliance.ninjadoc.customizers.ParameterCustomizer;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.Context;
import ninja.session.Session;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import java.util.Optional;
import java.util.Set;

import static com.devappliance.ninjadoc.NinjaDocUtils.getConfig;


/**
 * The type Request builder.
 *
 * @author bnasslahsen
 */
@Singleton
public class RequestBuilder extends AbstractRequestBuilder {

	static {
		getConfig().addRequestWrapperToIgnore(Context.class)
				.addRequestWrapperToIgnore(Session.class);
	}

	/**
	 * Instantiates a new Request builder.
	 *
	 * @param parameterBuilder                      the parameter builder
	 * @param requestBodyBuilder                    the request body builder
	 * @param operationBuilder                      the operation builder
	 * @param parameterCustomizers                  the parameter customizers
	 * @param localSpringDocParameterNameDiscoverer the local spring doc parameter name discoverer
	 */
	@Inject
	public RequestBuilder(GenericParameterBuilder parameterBuilder, RequestBodyBuilder requestBodyBuilder,
						  OperationBuilder operationBuilder, Optional<Set<ParameterCustomizer>> parameterCustomizers,
						  LocalVariableTableParameterNameDiscoverer localSpringDocParameterNameDiscoverer) {
		super(parameterBuilder, requestBodyBuilder, operationBuilder, parameterCustomizers, localSpringDocParameterNameDiscoverer);
	}
}
