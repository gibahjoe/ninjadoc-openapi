
package com.devappliance.ninjadoc;


import com.devappliance.ninjadoc.util.Constants;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The type Swagger ui config properties.
 *
 * @author bnasslahsen
 */
//@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
public class SwaggerUiConfigProperties extends AbstractSwaggerUiConfigProperties {

    /**
     * The Disable swagger default url.
     */
    @JsonProperty("disable-swagger-default-url")
    private boolean disableSwaggerDefaultUrl;

    /**
     * The Display query params.
     */
    @JsonProperty("display-query-params")
    private boolean displayQueryParams;

    /**
     * The Display query params without oauth 2.
     */
    @JsonProperty("display-query-params-without-oauth2")
    private boolean displayQueryParamsWithoutOauth2;

    /**
     * If swagger-ui is enabled or not
     */
    private Boolean enabled = true;

    /**
     * The Csrf configuration.
     */
    private Csrf csrf = new Csrf();

    /**
     * Is disable swagger default url boolean.
     *
     * @return the boolean
     */
    public boolean isDisableSwaggerDefaultUrl() {
        return disableSwaggerDefaultUrl;
    }

    /**
     * Sets disable swagger default url.
     *
     * @param disableSwaggerDefaultUrl the disable swagger default url
     */
    public void setDisableSwaggerDefaultUrl(boolean disableSwaggerDefaultUrl) {
        this.disableSwaggerDefaultUrl = disableSwaggerDefaultUrl;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public SwaggerUiConfigProperties setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * Is display query params boolean.
     *
     * @return the boolean
     */
    public boolean isDisplayQueryParams() {
        return displayQueryParams;
    }

    /**
     * Sets display query params.
     *
     * @param displayQueryParams the display query params
     */
    public void setDisplayQueryParams(boolean displayQueryParams) {
        this.displayQueryParams = displayQueryParams;
    }

    /**
     * Is display query params without oauth 2 boolean.
     *
     * @return the boolean
     */
    public boolean isDisplayQueryParamsWithoutOauth2() {
        return displayQueryParamsWithoutOauth2;
    }

    /**
     * Sets display query params without oauth 2.
     *
     * @param displayQueryParamsWithoutOauth2 the display query params without oauth 2
     */
    public void setDisplayQueryParamsWithoutOauth2(boolean displayQueryParamsWithoutOauth2) {
        this.displayQueryParamsWithoutOauth2 = displayQueryParamsWithoutOauth2;
    }

    /**
     * Gets csrf.
     *
     * @return the csrf
     */
    public Csrf getCsrf() {
        return csrf;
    }

    /**
     * Sets csrf.
     *
     * @param csrf the csrf
     */
    public void setCsrf(Csrf csrf) {
        this.csrf = csrf;
    }

    /**
     * Is csrf enabled boolean.
     *
     * @return the boolean
     */
    public boolean isCsrfEnabled() {
        return csrf.isEnabled();
    }

    /**
     * The type Csrf.
     */
    public static class Csrf {

        /**
         * The Enabled.
         */
        private boolean enabled;

        /**
         * The Cookie name.
         */
        private String cookieName = Constants.CSRF_DEFAULT_COOKIE_NAME;

        /**
         * The Header name.
         */
        private String headerName = Constants.CSRF_DEFAULT_HEADER_NAME;

        /**
         * Is enabled boolean.
         *
         * @return the boolean
         */
        public boolean isEnabled() {
            return enabled;
        }

        /**
         * Sets enabled.
         *
         * @param enabled the enabled
         */
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        /**
         * Gets cookie name.
         *
         * @return the cookie name
         */
        public String getCookieName() {
            return cookieName;
        }

        /**
         * Sets cookie name.
         *
         * @param cookieName the cookie name
         */
        public void setCookieName(String cookieName) {
            this.cookieName = cookieName;
        }

        /**
         * Gets header name.
         *
         * @return the header name
         */
        public String getHeaderName() {
            return headerName;
        }

        /**
         * Sets header name.
         *
         * @param headerName the header name
         */
        public void setHeaderName(String headerName) {
            this.headerName = headerName;
        }
    }

}
