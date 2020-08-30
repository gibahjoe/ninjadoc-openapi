package com.devappliance.ninjadoc.testCases.app1.conf;

import com.devappliance.ninjadoc.config.NinjaDocRoutes;
import com.google.inject.Inject;
import ninja.Result;
import ninja.Results;
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
        router.POST().route("/api/v1/schools").with(TestController::createSchool);
    }

    public static class TestController {
        public Result createSchool() {
            return Results.json();
        }
    }
}
