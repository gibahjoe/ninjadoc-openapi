package conf;

import com.devappliance.ninjadoc.config.NinjaDocRoutes;
import com.google.inject.Inject;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import ninja.Context;
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
        @ApiResponse(content = @Content(schema = @Schema(implementation = NewSchoolDto.class)))
        public Result createSchool(NewSchoolDto newSchoolDto, Context context) {
            return Results.json();
        }

        class NewSchoolDto {
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
}
