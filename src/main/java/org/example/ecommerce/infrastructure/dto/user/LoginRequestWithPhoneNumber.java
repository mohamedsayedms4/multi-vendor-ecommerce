package org.example.ecommerce.infrastructure.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequestWithPhoneNumber(
        @NotBlank(message = "{user.phone.required}")
        @Pattern(regexp = "^(\\+20|0)?1[0-9]{9}$", message = "{user.phone.invalid}")
        String userPhoneNumber,
        @NotBlank(message = "{user.password.required}")
        String password) {
}
