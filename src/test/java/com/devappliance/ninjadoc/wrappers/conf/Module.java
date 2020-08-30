package com.devappliance.ninjadoc.wrappers.conf;

import com.devappliance.ninjadoc.config.NinjaDocModule;
import com.devappliance.ninjadoc.customizers.OperationCustomizer;
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
    private OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            operation.setDeprecated(true);
            return operation;
        };
    }
}
