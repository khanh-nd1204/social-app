package com.social_service.validation.constraint;

import com.social_service.validation.validator.BirthDateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {BirthDateValidator.class})
public @interface BirthDate {

    String message() default "Invalid birth date";

    int min() default 2010;

    int max() default 1925;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}