package com.app.server.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniquePhoneNumberValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniquePhoneNumber {

    String message() default "کاربر دیگری با این شماره ثبت نام کرده است";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
