package org.example.ecommerce.infrastructure.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestWithEmail(
        @NotBlank(message = "{user.email.required}")
        @Email(message = "{user.email.invalid}")
        String email,
        @NotBlank(message = "{user.password.required}")
        String password) {
}
