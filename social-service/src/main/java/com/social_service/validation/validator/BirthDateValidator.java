package com.social_service.validation.validator;

import com.social_service.validation.constraint.BirthDate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class BirthDateValidator implements ConstraintValidator<BirthDate, LocalDate> {

    private int min;

    private int max;

    @Override
    public void initialize(BirthDate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        min = constraintAnnotation.min();
        max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (Objects.isNull(localDate)) {
            return false;
        }

        long years = ChronoUnit.YEARS.between(localDate, LocalDate.now());

        return years >= min && years <= max;
    }

}
