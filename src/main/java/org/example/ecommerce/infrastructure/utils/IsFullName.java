package org.example.ecommerce.infrastructure.utils;

import java.util.regex.Pattern;

public class IsFullName {

    private static final String FULL_NAME_REGEX = "^[\\p{L} ]{3,50}$";

    private static final Pattern pattern = Pattern.compile(FULL_NAME_REGEX);


    public static boolean isValid(String fullName) {
        if (fullName == null) return false;
        return pattern.matcher(fullName).matches();
    }
}
