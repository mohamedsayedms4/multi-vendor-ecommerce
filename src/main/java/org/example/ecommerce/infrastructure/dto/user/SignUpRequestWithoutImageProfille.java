package org.example.ecommerce.infrastructure.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpRequestWithoutImageProfille(

        @NotBlank(message = "{user.email.required}")
        @Email(message = "{user.email.invalid}")
        String userEmail,

        @NotBlank(message = "{user.password.required}")
        String userPassword,

        @NotBlank(message = "{user.fullName.required}")
        @Size(min = 3, max = 50, message = "{user.fullName.size}")
        String userFullName,

        @NotBlank(message = "{user.phone.required}")
        @Pattern(regexp = "^(\\+20|0)?1[0-9]{9}$", message = "{user.phone.invalid}")
        String userPhoneNumber
) {}
