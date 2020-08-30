package com.devappliance.ninjadoc.ui.app4.conf;

import com.devappliance.ninjadoc.config.NinjaDocRoutes;
import com.devappliance.ninjadoc.ui.app1.HelloController;
import com.google.inject.Inject;
import ninja.Router;
import ninja.application.ApplicationRoutes;
import ninja.utils.NinjaProperties;

import javax.inject.Named;

/**
 * @author Gibah Joseph
 * email: gibahjoe@gmail.com
 * Jul, 2020
 **/
@Named
public class Routes implements ApplicationRoutes {

    @Inject
    private NinjaProperties ninjaProperties;
    @Inject
    private NinjaDocRoutes ninjaDocRoutes;

    @Override
    public void init(Router router) {
        ninjaDocRoutes.register(router);
        router.GET().route("/persons").with(HelloController::persons);
    }
}
