package org.example.ecommerce.infrastructure.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpRequest(

        @NotBlank(message = "{user.email.required}")
        @Email(message = "{user.email.invalid}")
        String email,

        @NotBlank(message = "{user.password.required}")
        String password,

        @NotBlank(message = "{user.fullName.required}")
        @Size(min = 3, max = 50, message = "{user.fullName.size}")
        String fullName,

        @NotBlank(message = "{user.phone.required}")
        @Pattern(regexp = "^(\\+20|0)?1[0-9]{9}$", message = "{user.phone.invalid}")
        String phoneNumber,

        String imageUrl
) {}
