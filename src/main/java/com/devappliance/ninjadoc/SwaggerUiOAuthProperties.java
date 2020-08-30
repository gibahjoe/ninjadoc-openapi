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


import com.devappliance.ninjadoc.util.NinjaDocPropertiesUtils;

import java.util.Map;
import java.util.TreeMap;

/**
 * Please refer to the swagger
 * <a href="https://github.com/swagger-api/swagger-ui/blob/master/docs/usage/oauth2.md">configuration.md</a>
 * to get the idea what each parameter does.
 *
 * @author bnasslahsen
 */

public class SwaggerUiOAuthProperties {

    /**
     * The Client id.
     */
    private String clientId;

    /**
     * The Client secret.
     */
    private String clientSecret;

    /**
     * The Realm.
     */
    private String realm;

    /**
     * The App name.
     */
    private String appName;

    /**
     * The Scope separator.
     */
    private String scopeSeparator;

    /**
     * The Additional query string params.
     */
    private Map<String, String> additionalQueryStringParams;

    /**
     * The Use basic authentication with access code grant.
     */
    private String useBasicAuthenticationWithAccessCodeGrant;

    /**
     * The Use pkce with authorization code grant.
     */
    private Boolean usePkceWithAuthorizationCodeGrant;

    /**
     * Gets config parameters.
     *
     * @return the config parameters
     */
    public Map<String, Object> getConfigParameters() {
        final Map<String, Object> params = new TreeMap<>();
        NinjaDocPropertiesUtils.put("clientId", clientId, params);
        NinjaDocPropertiesUtils.put("clientSecret", clientSecret, params);
        NinjaDocPropertiesUtils.put("realm", realm, params);
        NinjaDocPropertiesUtils.put("scopeSeparator", scopeSeparator, params);
        NinjaDocPropertiesUtils.put("appName", appName, params);
        NinjaDocPropertiesUtils.put("useBasicAuthenticationWithAccessCodeGrant", useBasicAuthenticationWithAccessCodeGrant, params);
        NinjaDocPropertiesUtils.put("usePkceWithAuthorizationCodeGrant", usePkceWithAuthorizationCodeGrant, params);
        NinjaDocPropertiesUtils.put("additionalQueryStringParams", additionalQueryStringParams, params);
        return params;
    }

    /**
     * Gets client id.
     *
     * @return the client id
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Sets client id.
     *
     * @param clientId the client id
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Gets client secret.
     *
     * @return the client secret
     */
    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * Sets client secret.
     *
     * @param clientSecret the client secret
     */
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    /**
     * Gets realm.
     *
     * @return the realm
     */
    public String getRealm() {
        return realm;
    }

    /**
     * Sets realm.
     *
     * @param realm the realm
     */
    public void setRealm(String realm) {
        this.realm = realm;
    }

    /**
     * Gets app name.
     *
     * @return the app name
     */
    public String getAppName() {
        return appName;
    }

    /**
     * Sets app name.
     *
     * @param appName the app name
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * Gets scope separator.
     *
     * @return the scope separator
     */
    public String getScopeSeparator() {
        return scopeSeparator;
    }

    /**
     * Sets scope separator.
     *
     * @param scopeSeparator the scope separator
     */
    public void setScopeSeparator(String scopeSeparator) {
        this.scopeSeparator = scopeSeparator;
    }

    /**
     * Gets additional query string params.
     *
     * @return the additional query string params
     */
    public Map<String, String> getAdditionalQueryStringParams() {
        return additionalQueryStringParams;
    }

    /**
     * Sets additional query string params.
     *
     * @param additionalQueryStringParams the additional query string params
     */
    public void setAdditionalQueryStringParams(Map<String, String> additionalQueryStringParams) {
        this.additionalQueryStringParams = additionalQueryStringParams;
    }

    /**
     * Gets use basic authentication with access code grant.
     *
     * @return the use basic authentication with access code grant
     */
    public String getUseBasicAuthenticationWithAccessCodeGrant() {
        return useBasicAuthenticationWithAccessCodeGrant;
    }

    /**
     * Sets use basic authentication with access code grant.
     *
     * @param useBasicAuthenticationWithAccessCodeGrant the use basic authentication with access code grant
     */
    public void setUseBasicAuthenticationWithAccessCodeGrant(String useBasicAuthenticationWithAccessCodeGrant) {
        this.useBasicAuthenticationWithAccessCodeGrant = useBasicAuthenticationWithAccessCodeGrant;
    }

    /**
     * Gets use pkce with authorization code grant.
     *
     * @return the use pkce with authorization code grant
     */
    public Boolean getUsePkceWithAuthorizationCodeGrant() {
        return usePkceWithAuthorizationCodeGrant;
    }

    /**
     * Sets use pkce with authorization code grant.
     *
     * @param usePkceWithAuthorizationCodeGrant the use pkce with authorization code grant
     */
    public void setUsePkceWithAuthorizationCodeGrant(Boolean usePkceWithAuthorizationCodeGrant) {
        this.usePkceWithAuthorizationCodeGrant = usePkceWithAuthorizationCodeGrant;
    }
}
