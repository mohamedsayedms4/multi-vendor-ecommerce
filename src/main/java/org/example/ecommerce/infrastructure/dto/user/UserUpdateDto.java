package org.example.ecommerce.infrastructure.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.example.ecommerce.domain.common.Address;

public record UserUpdateDto(

        String email,

        String password,


        String fullName,

        Address pickupAddress ,

        String phoneNumber
){
}
