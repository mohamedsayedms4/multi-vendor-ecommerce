package org.example.ecommerce.application.service.authentication.impl;

import lombok.RequiredArgsConstructor;
import org.example.ecommerce.infrastructure.dto.user.LoginRequestWithEmail;
import org.example.ecommerce.infrastructure.dto.user.LoginRequestWithPhoneNumber;
import org.example.ecommerce.infrastructure.response.ApiResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;
@Service
@RequiredArgsConstructor
public class LoginContext {

    private final ApplicationContext applicationContext;

    private static final String EMAIL_REGEX =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final String EGYPT_PHONE_REGEX =
            "^(\\+20|0)?(10|11|12|15)[0-9]{8}$";

    private boolean isEmail(String input) {
        return Pattern.matches(EMAIL_REGEX, input);
    }

    private boolean isEgyptianPhone(String input) {
        return Pattern.matches(EGYPT_PHONE_REGEX, input);
    }

    // ✅ API موحد
    public ApiResponse login(String emailOrPhone, String password) {
        if (isEmail(emailOrPhone)) {
            LoginStrategy<LoginRequestWithEmail> strategy =
                    applicationContext.getBean(LoginTypes.LOGIN_WITH_EMAIL_VALUE, LoginStrategy.class);
            return strategy.login(new LoginRequestWithEmail(emailOrPhone, password));

        } else if (isEgyptianPhone(emailOrPhone)) {
            LoginStrategy<LoginRequestWithPhoneNumber> strategy =
                    applicationContext.getBean(LoginTypes.LOGIN_WITH_PHONE_NUMBER_VALUE, LoginStrategy.class);
            return strategy.login(new LoginRequestWithPhoneNumber(emailOrPhone, password));
        }

        throw new IllegalArgumentException("Invalid login identifier: " + emailOrPhone);
    }

    // ✅ Overloads للحفاظ على التوافق مع الكود القديم
    public ApiResponse login(LoginRequestWithEmail request) {
        return login(request.email(), request.password());
    }

    public ApiResponse login(LoginRequestWithPhoneNumber request) {
        return login(request.userPhoneNumber(), request.password());
    }
}
