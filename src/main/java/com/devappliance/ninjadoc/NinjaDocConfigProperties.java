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

import com.devappliance.ninjadoc.config.NinjaDocDefaultConfig;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import static com.devappliance.ninjadoc.util.Constants.DEFAULT_WEB_JARS_PREFIX_URL;

/**
 * Helper class for ninjadoc configuration in application.conf with prefix 'ninjadoc.'
 */
public class NinjaDocConfigProperties {

    /**
     * The Webjars.
     */
    private Webjars webjars = new Webjars();

    /**
     * The Api docs.
     */
    @JsonProperty("api-docs")
    private ApiDocs apiDocs = new ApiDocs();

    /**
     * The Packages to scan.
     */
    private List<String> packagesToScan;

    /**
     * The Packages to exclude.
     */
    private List<String> packagesToExclude;

    /**
     * The Paths to match.
     */
    private List<String> pathsToMatch;

    /**
     * The Paths to exclude.
     */
    private List<String> pathsToExclude;

    /**
     * The Cache.
     */
    private Cache cache = new Cache();

    /**
     * The Group configs.
     */
    private List<GroupConfig> groupConfigs = new ArrayList<>();

    /**
     * The Auto tag classes.
     */
    private boolean autoTagClasses = true;

    /**
     * The Model and view allowed.
     */
    private boolean modelAndViewAllowed;

    /**
     * The Override with generic response.
     */
    private boolean overrideWithGenericResponse = true;

    /**
     * The Remove broken reference definitions.
     */
    private boolean removeBrokenReferenceDefinitions = true;

    /**
     * The Writer with default pretty printer.
     */
    private boolean writerWithDefaultPrettyPrinter;

    /**
     * The Default consumes media type.
     */
    private String defaultConsumesMediaType = "application/json";

    /**
     * The Default produces media type.
     */
    private String defaultProducesMediaType = "*/*";

    /**
     * Use fully qualified name
     */
    private boolean useFqn;

    /**
     * Is use fqn boolean.
     *
     * @return the boolean
     */
    public boolean isUseFqn() {
        return useFqn;
    }

    /**
     * Sets use fqn.
     *
     * @param useFqn the use fqn
     */
    public void setUseFqn(boolean useFqn) {
        this.useFqn = useFqn;
    }

    /**
     * Is auto tag classes boolean.
     *
     * @return the boolean
     */
    public boolean isAutoTagClasses() {
        return autoTagClasses;
    }

    /**
     * Sets auto tag classes.
     *
     * @param autoTagClasses the auto tag classes
     */
    public void setAutoTagClasses(boolean autoTagClasses) {
        this.autoTagClasses = autoTagClasses;
    }

    /**
     * Is model and view allowed boolean.
     *
     * @return the boolean
     */
    public boolean isModelAndViewAllowed() {
        return modelAndViewAllowed;
    }

    /**
     * Sets model and view allowed.
     *
     * @param modelAndViewAllowed the model and view allowed
     */
    public void setModelAndViewAllowed(boolean modelAndViewAllowed) {
        this.modelAndViewAllowed = modelAndViewAllowed;
    }

    /**
     * Gets packages to exclude.
     *
     * @return the packages to exclude
     */
    public List<String> getPackagesToExclude() {
        return packagesToExclude;
    }

    /**
     * Sets packages to exclude.
     *
     * @param packagesToExclude the packages to exclude
     */
    public void setPackagesToExclude(List<String> packagesToExclude) {
        this.packagesToExclude = packagesToExclude;
    }

    /**
     * Gets paths to exclude.
     *
     * @return the paths to exclude
     */
    public List<String> getPathsToExclude() {
        return pathsToExclude;
    }

    /**
     * Sets paths to exclude.
     *
     * @param pathsToExclude the paths to exclude
     */
    public void setPathsToExclude(List<String> pathsToExclude) {
        this.pathsToExclude = pathsToExclude;
    }

    /**
     * Gets packages to scan.
     *
     * @return the packages to scan
     */
    public List<String> getPackagesToScan() {
        return packagesToScan;
    }

    /**
     * Sets packages to scan.
     *
     * @param packagesToScan the packages to scan
     */
    public void setPackagesToScan(List<String> packagesToScan) {
        this.packagesToScan = packagesToScan;
    }


    /**
     * Gets webjars.
     *
     * @return the webjars
     */
    public Webjars getWebjars() {
        return webjars;
    }

    /**
     * Sets webjars.
     *
     * @param webjars the webjars
     */
    public void setWebjars(Webjars webjars) {
        this.webjars = webjars;
    }

    /**
     * Gets api docs.
     *
     * @return the api docs
     */
    public ApiDocs getApiDocs() {
        return apiDocs;
    }

    /**
     * Sets api docs.
     *
     * @param apiDocs the api docs
     */
    public void setApiDocs(ApiDocs apiDocs) {
        this.apiDocs = apiDocs;
    }

    /**
     * Gets paths to match.
     *
     * @return the paths to match
     */
    public List<String> getPathsToMatch() {
        return pathsToMatch;
    }

    /**
     * Sets paths to match.
     *
     * @param pathsToMatch the paths to match
     */
    public void setPathsToMatch(List<String> pathsToMatch) {
        this.pathsToMatch = pathsToMatch;
    }

    /**
     * Gets cache.
     *
     * @return the cache
     */
    public Cache getCache() {
        return cache;
    }

    /**
     * Sets cache.
     *
     * @param cache the cache
     */
    public void setCache(Cache cache) {
        this.cache = cache;
    }

    /**
     * Is cache disabled boolean.
     *
     * @return the boolean
     */
    public boolean isCacheDisabled() {
        return cache.isDisabled();
    }

    /**
     * Gets group configs.
     *
     * @return the group configs
     */
    public List<GroupConfig> getGroupConfigs() {
        return groupConfigs;
    }

    /**
     * Sets group configs.
     *
     * @param groupConfigs the group configs
     */
    public void setGroupConfigs(List<GroupConfig> groupConfigs) {
        this.groupConfigs = groupConfigs;
    }

    /**
     * Add group config.
     *
     * @param groupConfigs the group configs
     */
    public void addGroupConfig(GroupConfig groupConfigs) {
        this.groupConfigs.add(groupConfigs);
    }

    /**
     * Gets default consumes media type.
     *
     * @return the default consumes media type
     */
    public String getDefaultConsumesMediaType() {
        return defaultConsumesMediaType;
    }

    /**
     * Sets default consumes media type.
     *
     * @param defaultConsumesMediaType the default consumes media type
     */
    public void setDefaultConsumesMediaType(String defaultConsumesMediaType) {
        this.defaultConsumesMediaType = defaultConsumesMediaType;
    }

    /**
     * Gets default produces media type.
     *
     * @return the default produces media type
     */
    public String getDefaultProducesMediaType() {
        return defaultProducesMediaType;
    }

    /**
     * Sets default produces media type.
     *
     * @param defaultProducesMediaType the default produces media type
     */
    public void setDefaultProducesMediaType(String defaultProducesMediaType) {
        this.defaultProducesMediaType = defaultProducesMediaType;
    }

    /**
     * Is override with generic response boolean.
     *
     * @return the boolean
     */
    public boolean isOverrideWithGenericResponse() {
        return overrideWithGenericResponse;
    }

    /**
     * Sets override with generic response.
     *
     * @param overrideWithGenericResponse the override with generic response
     */
    public void setOverrideWithGenericResponse(boolean overrideWithGenericResponse) {
        this.overrideWithGenericResponse = overrideWithGenericResponse;
    }

    /**
     * Is remove broken reference definitions boolean.
     *
     * @return the boolean
     */
    public boolean isRemoveBrokenReferenceDefinitions() {
        return removeBrokenReferenceDefinitions;
    }

    /**
     * Sets remove broken reference definitions.
     *
     * @param removeBrokenReferenceDefinitions the remove broken reference definitions
     */
    public void setRemoveBrokenReferenceDefinitions(boolean removeBrokenReferenceDefinitions) {
        this.removeBrokenReferenceDefinitions = removeBrokenReferenceDefinitions;
    }

    /**
     * Is writer with default pretty printer boolean.
     *
     * @return the boolean
     */
    public boolean isWriterWithDefaultPrettyPrinter() {
        return writerWithDefaultPrettyPrinter;
    }

    /**
     * Sets writer with default pretty printer.
     *
     * @param writerWithDefaultPrettyPrinter the writer with default pretty printer
     */
    public void setWriterWithDefaultPrettyPrinter(boolean writerWithDefaultPrettyPrinter) {
        this.writerWithDefaultPrettyPrinter = writerWithDefaultPrettyPrinter;
    }

    /**
     * The type Model converters.
     */
    public static class ModelConverters {

        /**
         * The Deprecating converter.
         */
        private DeprecatingConverter deprecatingConverter = new DeprecatingConverter();

        /**
         * Gets deprecating converter.
         *
         * @return the deprecating converter
         */
        public DeprecatingConverter getDeprecatingConverter() {
            return deprecatingConverter;
        }

        /**
         * Sets deprecating converter.
         *
         * @param deprecatingConverter the deprecating converter
         */
        public void setDeprecatingConverter(DeprecatingConverter deprecatingConverter) {
            this.deprecatingConverter = deprecatingConverter;
        }

        /**
         * The type Deprecating converter.
         */
        public static class DeprecatingConverter {

            /**
             * The Enabled.
             */
            private boolean enabled;

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
        }
    }

    /**
     * The type Webjars.
     *
     * @author bnasslahsen
     */
    public static class Webjars {
        /**
         * The Prefix.
         */
        private String prefix = DEFAULT_WEB_JARS_PREFIX_URL;

        /**
         * Gets prefix.
         *
         * @return the prefix
         */
        public String getPrefix() {
            return prefix;
        }

        /**
         * Sets prefix.
         *
         * @param prefix the prefix
         */
        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }
    }

    /**
     * The type Api docs.
     *
     * @author bnasslahsen
     */
    public static class ApiDocs {
        /**
         * Path to the generated OpenAPI documentation. For a yaml file, append ".yaml" to the path.
         */
        private String path = NinjaDocDefaultConfig.DEFAULT_API_DOCS_URL;

        /**
         * Whether to generate and serve an OpenAPI document.
         */
        private boolean enabled = true;

        /**
         * The Resolve schema properties.
         */
        private boolean resolveSchemaProperties;

        /**
         * The Groups.
         */
        private Groups groups = new Groups();

        /**
         * Gets path.
         *
         * @return the path
         */
        public String getPath() {
            return path;
        }

        /**
         * Sets path.
         *
         * @param path the path
         */
        public void setPath(String path) {
            this.path = path;
        }

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
         * Gets groups.
         *
         * @return the groups
         */
        public Groups getGroups() {
            return groups;
        }

        /**
         * Sets groups.
         *
         * @param groups the groups
         */
        public void setGroups(Groups groups) {
            this.groups = groups;
        }

        /**
         * Is resolve schema properties boolean.
         *
         * @return the boolean
         */
        public boolean isResolveSchemaProperties() {
            return resolveSchemaProperties;
        }

        /**
         * Sets resolve schema properties.
         *
         * @param resolveSchemaProperties the resolve schema properties
         */
        public void setResolveSchemaProperties(boolean resolveSchemaProperties) {
            this.resolveSchemaProperties = resolveSchemaProperties;
        }
    }

    /**
     * The type Groups.
     *
     * @author bnasslahsen
     */
    public static class Groups {
        /**
         * The Enabled.
         */
        private boolean enabled;

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
    }

    /**
     * The type Cache.
     *
     * @author bnasslahsen
     */
    public static class Cache {
        /**
         * The Disabled.
         */
        private boolean disabled;

        /**
         * Is disabled boolean.
         *
         * @return the boolean
         */
        public boolean isDisabled() {
            return disabled;
        }

        /**
         * Sets disabled.
         *
         * @param disabled the disabled
         */
        public void setDisabled(boolean disabled) {
            this.disabled = disabled;
        }
    }

    /**
     * The type Group config.
     *
     * @author bnasslahsen
     */
    public static class GroupConfig {

        /**
         * The Paths to match.
         */
        private List<String> pathsToMatch;

        /**
         * The Packages to scan.
         */
        private List<String> packagesToScan;

        /**
         * The Packages to exclude.
         */
        private List<String> packagesToExclude;

        /**
         * The Paths to exclude.
         */
        private List<String> pathsToExclude;

        /**
         * The Group.
         */
        private String group;

        /**
         * Instantiates a new Group config.
         */
        public GroupConfig() {
        }

        /**
         * Instantiates a new Group config.
         *
         * @param group             the group
         * @param pathsToMatch      the paths to match
         * @param packagesToScan    the packages to scan
         * @param packagesToExclude the packages to exclude
         * @param pathsToExclude    the paths to exclude
         */
        public GroupConfig(String group, List<String> pathsToMatch, List<String> packagesToScan, List<String> packagesToExclude, List<String> pathsToExclude) {
            this.pathsToMatch = pathsToMatch;
            this.pathsToExclude = pathsToExclude;
            this.packagesToExclude = packagesToExclude;
            this.packagesToScan = packagesToScan;
            this.group = group;
        }

        /**
         * Gets paths to match.
         *
         * @return the paths to match
         */
        public List<String> getPathsToMatch() {
            return pathsToMatch;
        }

        /**
         * Sets paths to match.
         *
         * @param pathsToMatch the paths to match
         */
        public void setPathsToMatch(List<String> pathsToMatch) {
            this.pathsToMatch = pathsToMatch;
        }

        /**
         * Gets packages to scan.
         *
         * @return the packages to scan
         */
        public List<String> getPackagesToScan() {
            return packagesToScan;
        }

        /**
         * Sets packages to scan.
         *
         * @param packagesToScan the packages to scan
         */
        public void setPackagesToScan(List<String> packagesToScan) {
            this.packagesToScan = packagesToScan;
        }

        /**
         * Gets group.
         *
         * @return the group
         */
        public String getGroup() {
            return group;
        }

        /**
         * Sets group.
         *
         * @param group the group
         */
        public void setGroup(String group) {
            this.group = group;
        }

        /**
         * Gets packages to exclude.
         *
         * @return the packages to exclude
         */
        public List<String> getPackagesToExclude() {
            return packagesToExclude;
        }

        /**
         * Sets packages to exclude.
         *
         * @param packagesToExclude the packages to exclude
         */
        public void setPackagesToExclude(List<String> packagesToExclude) {
            this.packagesToExclude = packagesToExclude;
        }

        /**
         * Gets paths to exclude.
         *
         * @return the paths to exclude
         */
        public List<String> getPathsToExclude() {
            return pathsToExclude;
        }

        /**
         * Sets paths to exclude.
         *
         * @param pathsToExclude the paths to exclude
         */
        public void setPathsToExclude(List<String> pathsToExclude) {
            this.pathsToExclude = pathsToExclude;
        }
    }
}
