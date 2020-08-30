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

import ninja.params.Header;
import ninja.params.Param;
import ninja.params.PathParam;
import ninja.params.SessionParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

/**
 * The type Parameter info.
 *
 * @author bnasslahsen
 */
public class ParameterInfo {

    /**
     * The Method parameter.
     */
    private final MethodParameter methodParameter;

    /**
     * The P name.
     */
    private String pName;

    /**
     * The Parameter model.
     */
    private io.swagger.v3.oas.models.parameters.Parameter parameterModel;

    /**
     * The Request header.
     */
    private Header requestHeader;

    /**
     * The Request param.
     */
    private Param requestParam;

    /**
     * The Path var.
     */
    private PathParam pathVar;

    /**
     * The Cookie value.
     */
    private SessionParam cookieValue;
    private List<? extends Annotation> annotations;

    /**
     * Instantiates a new Parameter info.
     *
     * @param pName           the p name
     * @param methodParameter the method parameter
     */
    public ParameterInfo(String pName, MethodParameter methodParameter) {
        this.methodParameter = methodParameter;
        this.requestHeader = methodParameter.getParameterAnnotation(Header.class);
        this.requestParam = methodParameter.getParameterAnnotation(Param.class);
        this.pathVar = methodParameter.getParameterAnnotation(PathParam.class);
        this.cookieValue = methodParameter.getParameterAnnotation(SessionParam.class);
        this.pName = calculateName(pName, requestHeader, requestParam, pathVar);
        this.annotations = Arrays.asList(methodParameter.getParameterAnnotations());
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getpName() {
        return pName;
    }

    /**
     * Sets name.
     *
     * @param pName the p name
     */
    public void setpName(String pName) {
        this.pName = pName;
    }

    /**
     * Gets method parameter.
     *
     * @return the method parameter
     */
    public MethodParameter getMethodParameter() {
        return methodParameter;
    }

    /**
     * Gets parameter.
     *
     * @return the parameter
     */
    public Parameter getParameter() {
        return methodParameter.getParameter();
    }

    /**
     * Gets parameter model.
     *
     * @return the parameter model
     */
    public io.swagger.v3.oas.models.parameters.Parameter getParameterModel() {
        return parameterModel;
    }

    /**
     * Sets parameter model.
     *
     * @param parameterModel the parameter model
     */
    public void setParameterModel(io.swagger.v3.oas.models.parameters.Parameter parameterModel) {
        this.parameterModel = parameterModel;
    }

    /**
     * Gets request header.
     *
     * @return the request header
     */
    public Header getRequestHeader() {
        return requestHeader;
    }

    /**
     * Gets request param.
     *
     * @return the request param
     */
    public Param getRequestParam() {
        return requestParam;
    }

    /**
     * Gets path var.
     *
     * @return the path var
     */
    public PathParam getPathVar() {
        return pathVar;
    }

    /**
     * Gets cookie value.
     *
     * @return the cookie value
     */
    public SessionParam getCookieValue() {
        return cookieValue;
    }

    /**
     * Calculate name string.
     *
     * @param pName         the p name
     * @param requestHeader the request header
     * @param requestParam  the request param
     * @param pathVar       the path var
     * @return the string
     */
    private String calculateName(String pName, Header requestHeader, Param requestParam, PathParam pathVar) {
        String name = pName;
        if (requestHeader != null && StringUtils.isNotEmpty(requestHeader.value()))
            name = requestHeader.value();
        else if (requestParam != null && StringUtils.isNotEmpty(requestParam.value()))
            name = requestParam.value();
        else if (pathVar != null && StringUtils.isNotEmpty(pathVar.value()))
            name = pathVar.value();
//		else if (cookieValue != null && StringUtils.isNotEmpty(cookieValue.value()))
//			name = cookieValue.value();
        return name;
    }

    public List<? extends Annotation> getAnnotations() {
        return annotations;
    }

    public ParameterInfo setAnnotations(List<? extends Annotation> annotations) {
        this.annotations = annotations;
        return this;
    }
}
