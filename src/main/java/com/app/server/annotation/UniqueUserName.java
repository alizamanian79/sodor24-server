package com.app.server.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueUserNameValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueUserName {
    String message() default "کاربری با این نام کاربری وجود دارد";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
