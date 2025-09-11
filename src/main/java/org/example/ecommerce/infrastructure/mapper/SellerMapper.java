package org.example.ecommerce.infrastructure.mapper;

import org.example.ecommerce.domain.model.seller.Seller;
import org.example.ecommerce.infrastructure.dto.seller.SellerProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SellerMapper {



        @Mapping(source = "user.email", target = "email")
        @Mapping(source = "user.fullName", target = "fullName")
        @Mapping(source = "user.phoneNumber", target = "phoneNumber")
        @Mapping(source = "user.authorities", target = "authorities")
        @Mapping(source = "user.pickupAddress", target = "pickupAddress")
        @Mapping(source = "user.imageUrl", target = "imageUrl")
        SellerProfile toSellerProfile(Seller seller);

}
