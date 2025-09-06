package org.example.ecommerce.infrastructure.utils;

import java.util.regex.Pattern;

public class IsPhoneNumber {
    private static final String EGYPT_PHONE_REGEX =
            "^(\\+20|0)?(10|11|12|15)[0-9]{8}$";

    public static boolean isEgyptianPhone(String input) {
        return Pattern.matches(EGYPT_PHONE_REGEX, input);
    }
}
