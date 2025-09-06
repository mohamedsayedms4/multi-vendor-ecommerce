package org.example.ecommerce.infrastructure.utils;

import java.util.regex.Pattern;

public class IsEmail {
    private static final String EMAIL_REGEX =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    public static boolean isEmail(String input) {
        return Pattern.matches(EMAIL_REGEX, input);
    }

}
