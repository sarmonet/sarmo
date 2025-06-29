package com.sarmo.authservice.validation; // Или ваш пакет для валидации

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class IsEmailOrPhoneValidator implements ConstraintValidator<IsEmailOrPhone, String> {

    // Очень простой regex для email (можно использовать более строгий, если нужно)
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");

    // Очень простой regex для номера телефона (допускает +, цифры, пробелы, дефисы)
    // Это БАЗОВАЯ проверка, которая НЕ гарантирует валидность реального номера,
    // но проверяет формат. Для строгой проверки используйте библиотеки вроде Google libphonenumber.
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?[0-9\\s-]+[0-9]$");


    @Override
    public void initialize(IsEmailOrPhone constraintAnnotation) {
        // Можно инициализировать что-то, если нужно, из аннотации
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Если значение null или пустое, можно либо считать его валидным (если поле не @NotNull),
        // либо невалидным. Обычно для необязательных полей валидность true для null/empty.
        // Если поле обязательное, используйте @NotNull дополнительно.
        if (value == null || value.trim().isEmpty()) {
            return true; // Или false, если пустая строка не допускается
        }

        // Проверяем, соответствует ли строка формату email ИЛИ формату телефона
        boolean isEmail = EMAIL_PATTERN.matcher(value).matches();
        boolean isPhone = PHONE_PATTERN.matcher(value).matches();

        // Валидно, если это email ИЛИ номер телефона
        return isEmail || isPhone;
    }
}