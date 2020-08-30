package com.devappliance.ninjadoc.ui.app1.conf;

import com.devappliance.ninjadoc.config.NinjaDocModule;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
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
}
