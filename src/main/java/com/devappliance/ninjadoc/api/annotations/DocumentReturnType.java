package com.devappliance.ninjadoc.api.annotations;

import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Because Ninja returns {@link ninja.Result} for all its controller method, this causes swagger to generate
 * wrong response object.
 * <p>
 * This class is a shorthand way of informing the OpenApi the actual return type you want to document instead
 * of document {@link ninja.Result}. While you could still document using swagger's the {@link ApiResponse},
 * its not flexible and doesnt take generic information into account.
 * </p>
 * <p>Usage is as simple as annotating your controller method with below</p>
 * <code>
 * {@literal @DocumentReturnType(type = List.class, genericTypes = {NewSchoolDto.class})}
 * public Result createSchool(@Param("valueName") String value) {
 * return Results.json();
 * }
 * </code>
 *
 * @author Gibah Joseph
 * email: gibahjoe@gmail.com
 * Jul, 2020
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DocumentReturnType {
    /**
     * The class of the return object
     *
     * @return The class of the return object
     */
    Class<?> type();

    /**
     * The generic types of the class
     *
     * @return the generic types of the class
     */
    Class<?>[] genericTypes() default {};
}
