package com.devappliance.ninjadoc.testCases.app4.conf;

import com.devappliance.ninjadoc.api.annotations.DocumentReturnType;
import com.devappliance.ninjadoc.config.NinjaDocRoutes;
import com.google.inject.Inject;
import ninja.Result;
import ninja.Results;
import ninja.Router;
import ninja.application.ApplicationRoutes;
import ninja.params.Param;
import ninja.utils.NinjaProperties;

import javax.inject.Named;
import java.util.List;

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
        @DocumentReturnType(type = List.class, genericTypes = {NewSchoolDto.class})
        public Result createSchool(@Param("valueName") String value) {
            return Results.json();
        }
    }

    public static class NewSchoolDto {
        private String name;
        private String age;

        public String getName() {
            return name;
        }

        public NewSchoolDto setName(String name) {
            this.name = name;
            return this;
        }

        public String getAge() {
            return age;
        }

        public NewSchoolDto setAge(String age) {
            this.age = age;
            return this;
        }
    }
}
