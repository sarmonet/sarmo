package com.sarmo.authservice.validation; // Или ваш пакет для валидации

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IsEmailOrPhoneValidator.class)
@Documented
public @interface IsEmailOrPhone {

    String message() default "Must be a valid email address or phone number"; // Сообщение об ошибке по умолчанию

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}