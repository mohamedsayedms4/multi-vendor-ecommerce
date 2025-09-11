package org.example.ecommerce.infrastructure.controller.seller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.seller.SellerService;
import org.example.ecommerce.application.service.user.UserService;
import org.example.ecommerce.domain.common.exception.INTERNAL_SERVER_ERROR;
import org.example.ecommerce.domain.common.exception.LogoIsRequired;
import org.example.ecommerce.domain.common.exception.UnauthorizedException;
import org.example.ecommerce.domain.model.user.exception.UserNotFoundException;
import org.example.ecommerce.infrastructure.dto.seller.*;
import org.example.ecommerce.infrastructure.dto.user.UserProfile;
import org.example.ecommerce.infrastructure.utils.ImageUploadUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/sellers")
@Slf4j
@RequiredArgsConstructor
public class SellerController {
    private final SellerService sellerService;
    private final UserService userService;
    private final ImageUploadUtil  imageUploadUtil;


    @PostMapping("")
    public ResponseEntity<?> createSeller(
            @RequestPart("seller_details") String dto,
            @RequestHeader(value = "Authorization", required = false) String jwt,
            @RequestPart(value = "logo" ) MultipartFile logo,
            @RequestPart(value = "banner", required = false) MultipartFile banner

    ) {

        log.info("----------JWT IS______________ : {}",jwt);
        Optional<UserProfile> sellerOptional =userService.findByJwt(jwt);
        if (sellerOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not found"));
        }
        ObjectMapper objectMapper = new ObjectMapper();
        BecomeASellerWithoutLogoAndBannerDto becomeASellerDto;
        try {
            becomeASellerDto = objectMapper.readValue(dto, BecomeASellerWithoutLogoAndBannerDto.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "صيغة البيانات غير صحيحة: " + e.getMessage()));
        }

// دلوقتي المتغير متاح هنا



        String logoUrl = null;
        if (logo.isEmpty() || logo == null) {
            throw new  LogoIsRequired("logo is required");
        }else {
            logoUrl = imageUploadUtil.saveImage(logo);

        }

        String bannerUrl = null;
        if (banner != null && !banner.isEmpty()) {
            bannerUrl = imageUploadUtil.saveImage(banner);
        }

        BecomeASellerDto updatedDto = new BecomeASellerDto(
                becomeASellerDto.businessName(),
                becomeASellerDto.businessEmail(),
                becomeASellerDto.businessMobile(),
                becomeASellerDto.businessAddress(),
                logoUrl,
                bannerUrl
        );

        Long userId = sellerOptional.get().id();

        Optional<SellerProfile> sellerOpt = sellerService.becomeASeller(userId, updatedDto);
        if (sellerOpt.isPresent()) {
            return ResponseEntity.ok(Map.of(
                    "message", "User promoted to Seller successfully",
                    "sellerId", sellerOpt.get().id(),
                    "accountStatus", sellerOpt.get().accountStatus().name()
            ));
        }else {
            throw new INTERNAL_SERVER_ERROR("Could not create seller");
        }


    }

    @PostMapping("/profile")
    public ResponseEntity<?> getMyProfile(
            @RequestHeader(value = "Authorization", required = false) String jwt
            )
    {
        if (jwt == null || jwt.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authorization header is missing"));
        }
        try {
            Optional<SellerProfile> seller = sellerService.getSellerProfile(jwt);

            if (seller.isPresent()) {
                System.out.println("User found: " + seller.get().email());
                return ResponseEntity.ok(seller.get());
            } else {
                System.out.println("User not found or invalid JWT");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "error", "User not found",
                                "message", "Either the JWT is invalid or the user doesn't exist in database"
                        ));
            }
        } catch (Exception e) {
            System.out.println("Exception in getUserProfile: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "JWT processing failed", "details", e.getMessage()));
        }
    }

    @PatchMapping("/update")
    public ResponseEntity<?> updateSeller(
            @RequestPart("seller_details") String dto,
            @RequestHeader(value = "Authorization", required = false) String jwt,
            @RequestPart(value = "logo", required = false) MultipartFile logo,
            @RequestPart(value = "banner", required = false) MultipartFile banner) throws Exception {

        if (jwt == null || jwt.trim().isEmpty()) {
            throw new UnauthorizedException("Authorization header is missing");
        }
        log.info("----------JWT IS______________ : {}", jwt);

        Optional<SellerProfile> sellerOptional = sellerService.getSellerProfile(jwt);
        if (sellerOptional.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        UpdateSellerWithoutLogoAndBanerDto updatedDto;
        updatedDto = objectMapper.readValue(dto, UpdateSellerWithoutLogoAndBanerDto.class);

        String logoUrl = null;
        if (logo != null && !logo.isEmpty()) {
            logoUrl = imageUploadUtil.saveImage(logo);
        }

        String bannerUrl = null;
        if (banner != null && !banner.isEmpty()) {
            bannerUrl = imageUploadUtil.saveImage(banner);
        }

        UpdateSellerDto updateSellerDto = new UpdateSellerDto(
                updatedDto.businessName(),
                updatedDto.businessEmail(),
                updatedDto.businessMobile(),
                updatedDto.businessAddress(),
                logoUrl,
                bannerUrl
        );

        Long id = sellerOptional.get().id();
        sellerService.updateSellerProfile(id, updateSellerDto);

        return ResponseEntity.ok("Seller profile updated successfully");
    }



}
