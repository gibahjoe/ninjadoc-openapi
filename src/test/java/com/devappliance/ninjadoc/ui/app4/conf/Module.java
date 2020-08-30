package com.devappliance.ninjadoc.ui.app4.conf;

import com.devappliance.ninjadoc.GroupedOpenApi;
import com.devappliance.ninjadoc.config.NinjaDocModule;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.multibindings.ProvidesIntoSet;
import ninja.utils.NinjaProperties;

/**
 * @author Gibah Joseph
 * email: gibahjoe@gmail.com
 * Jul, 2020
 **/
public class Module extends AbstractModule {
    private NinjaProperties ninjaProperties;

    @Inject
    public Module(NinjaProperties ninjaProperties) {
        this.ninjaProperties = ninjaProperties;
    }

    @Override
    protected void configure() {
        install(new NinjaDocModule(ninjaProperties));

    }

    @ProvidesIntoSet
    public GroupedOpenApi storeOpenApi() {
        String paths[] = {"/store/**"};
        return GroupedOpenApi.builder()
                .group("stores")
                .pathsToMatch(paths)
                .build();
    }

    @ProvidesIntoSet
    public GroupedOpenApi groupOpenApi() {
        String paths[] = {"/pet/**"};
        return GroupedOpenApi.builder()
                .group("pets")
                .pathsToMatch(paths)
                .build();
    }
}
