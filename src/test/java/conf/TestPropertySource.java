package conf;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Gibah Joseph
 * email: gibahjoe@gmail.com
 * Aug, 2020
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TestPropertySource {
    String[] properties() default {};
}
