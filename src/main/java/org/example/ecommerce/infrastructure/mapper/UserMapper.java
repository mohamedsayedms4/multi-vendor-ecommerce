package org.example.ecommerce.infrastructure.mapper;

import org.example.ecommerce.domain.model.user.User;
import org.example.ecommerce.infrastructure.dto.user.SignUpRequest;
import org.example.ecommerce.infrastructure.dto.user.SignUpRequestWithoutImageProfille;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")

public interface UserMapper {
    // من SignUpRequest -> User
    @Mapping(target = "email", source = "userEmail")
    @Mapping(target = "password", source = "userPassword")
    @Mapping(target = "fullName", source = "userFullName")
    @Mapping(target = "phoneNumber", source = "userPhoneNumber")
    @Mapping(target = "authorities", ignore = true)   // مش جايه من DTO
    @Mapping(target = "pickupAddress", ignore = true) // ممكن نعملها DTO بعدين
    User toUser(SignUpRequest dto);

    // من SignUpRequest -> User
    @Mapping(target = "email", source = "userEmail")
    @Mapping(target = "password", source = "userPassword")
    @Mapping(target = "fullName", source = "userFullName")
    @Mapping(target = "phoneNumber", source = "userPhoneNumber")
    @Mapping(target = "imageUrl" , ignore = true)
    @Mapping(target = "authorities", ignore = true)   // مش جايه من DTO
    @Mapping(target = "pickupAddress", ignore = true) // ممكن نعملها DTO بعدين
    User toUser(SignUpRequestWithoutImageProfille dto);
}
