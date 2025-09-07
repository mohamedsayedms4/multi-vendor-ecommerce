package org.example.ecommerce.infrastructure.persistence;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.FailedLogin.LoginAttemptService;
import org.example.ecommerce.application.service.user.UserService;
import org.example.ecommerce.domain.model.user.exception.EmailIsNotValid;
import org.example.ecommerce.infrastructure.dto.UserChangeUserPWDDto;
import org.example.ecommerce.infrastructure.dto.UserUpdateImageProfile;
import org.example.ecommerce.infrastructure.dto.user.UserProfile;
import org.example.ecommerce.infrastructure.dto.user.UserUpdateDto;
import org.example.ecommerce.infrastructure.utils.ImageUploadUtil;
import org.example.ecommerce.infrastructure.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;



@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final ImageUploadUtil imageUploadUtil;
    private final LoginAttemptService loginAttemptService;
    @PostMapping("/jwt")
    public ResponseEntity<?> getUserProfileByJwt(
            @RequestHeader(value = "Authorization", required = false) String jwt) {

        log.info("----------JWT IS______________ : {}",jwt);
        if (jwt == null || jwt.trim().isEmpty()) {
            log.error("JWT is null or empty");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authorization header is missing"));
        }

        try {
            Optional<UserProfile> user = userService.findByJwt(jwt);

            if (user.isPresent()) {
                log.info("UserFullInformationDto : {}", user.get().email());
                System.out.println("User found: " + user.get().email());
                return ResponseEntity.ok(user.get());
            } else {
                log.error("User not found");
                System.out.println("User not found or invalid JWT");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "error", "User not found",
                                "message", "Either the JWT is invalid or the user doesn't exist in database"
                        ));
            }
        } catch (Exception e) {
            System.out.println("Exception in getUserProfile: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "JWT processing failed", "details", e.getMessage()));
        }
    }


    @PatchMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateDto userFullInfo,
                                        @RequestParam(required = false) String email,
                                        @RequestParam(required = false) String phoneNumber,
                                        @RequestParam(required = false) Long id,
                                        @RequestHeader(value = "Authorization", required = false) String jwt) {

        if (userFullInfo == null) {
            log.warn("Update request body is null");
            return ResponseEntity.badRequest().body("User data must be provided");
        }

        Optional<UserProfile> updatedUser = Optional.empty();

        if (id != null) {
            log.info("Updating user by ID: {}", id);
            updatedUser = userService.updateUserById(userFullInfo, id);
        } else if (email != null) {
            log.info("Updating user by email: {}", email);
            updatedUser = userService.updateUserByEmail(userFullInfo, email);
        } else if (phoneNumber != null) {
            log.info("Updating user by phone number: {}", phoneNumber);
            updatedUser = userService.updateUserByPhone(userFullInfo, phoneNumber);
        } else if (jwt != null) {
            log.info("Updating user by jwt: {}", jwt);
            updatedUser = userService.updateUserByJwt(userFullInfo, jwt); // تأكد من وجود هذه الدالة
        } else {
            log.warn("No identifier provided for update");
            return ResponseEntity.badRequest().body("No identifier provided. Please provide email, phoneNumber, id, or jwt");
        }

        return updatedUser
                .<ResponseEntity<?>>map(user -> {
                    log.info("User updated successfully: {}", user.email());
                    return ResponseEntity.ok(user);
                })
                .orElseGet(() -> {
                    log.error("Failed to update user with provided identifier");
                    return ResponseEntity.badRequest().body("User update failed");
                });

    }

    @PutMapping("/update/image")
    public ResponseEntity<?> updateImageUrl(
            @RequestPart(value = "imageProfile", required = true) MultipartFile imageProfile,
            @RequestHeader(value = "Authorization", required = false) String jwt
    ){
        log.info("Received request to update user: {}", imageProfile);

        if (jwt == null || jwt.trim().isEmpty()) {
            log.warn("Authorization header is missing");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authorization header is missing"));
        }

        String email = jwtUtil.extractEmailFromJwt(jwt);
        log.debug("Extracted email from JWT: {}", email);

        // رفع الصور
        String imageUrl = null;
        if (imageProfile != null && !imageProfile.isEmpty()) {
            imageUrl = imageUploadUtil.saveImage(imageProfile);
        }
        Optional<UserUpdateImageProfile> updatedUser = userService.updateProfileImage(imageUrl, email);
        if (updatedUser.isPresent()) {
            log.info("User with email [{}] updated successfully", email);
            return ResponseEntity.ok(updatedUser.get());
        } else {
            log.error("Failed to update user with email [{}]", email);
            return ResponseEntity.badRequest().body("User update failed");
        }
    }

    @DeleteMapping("/del/image-profile")
    public ResponseEntity<?> deleteImageProfile(
            @RequestParam(required = false) String email,
            @RequestHeader(value = "Authorization", required = false) String jwt){

        log.info("Received request to delete profile image with email [{}] or JWT", email);
        if ((email == null || email.isBlank()) && (jwt == null || jwt.isBlank())) {
            log.warn("Neither email nor JWT provided");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "You must provide either email or JWT"));
        }
        if ((email == null || email.isBlank()) && jwt != null && !jwt.isBlank()) {
            email = jwtUtil.extractEmailFromJwt(jwt);
            log.debug("Extracted email from JWT: {}", email);
        }

        Boolean deleted = userService.deleteImageProfile(email);
        if (deleted) {
            log.info("Profile image for [{}] deleted successfully", email);
            return ResponseEntity.ok(Map.of("message", "Profile image deleted successfully"));
        } else {
            log.warn("No profile image found for [{}] or deletion failed", email);
            return ResponseEntity.badRequest().body(Map.of("error", "Profile image deletion failed or not found"));
        }
    }
    @PutMapping("/changePwd")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody UserChangeUserPWDDto changeUserPWD,
            @RequestHeader(value = "Authorization", required = false) String jwt
    ){
        log.info("Received request to delete user with jwt: {}", jwt);

        if (jwt == null || jwt.trim().isEmpty()) {
            log.warn("Authorization header is missing");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authorization header is missing"));
        }

        String email = jwtUtil.extractEmailFromJwt(jwt);
        log.debug("Extracted email from JWT: {}", email);
        loginAttemptService.loginFailed(email);

        if(!email.equals(changeUserPWD.email())){
            throw new EmailIsNotValid("Invalid email address");
        }
        userService.updatePassword(changeUserPWD);
        log.info("User with email [{}] updated successfully", changeUserPWD.email());
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }


    @DeleteMapping("/del")
    public ResponseEntity<?> deleteUser(
            @RequestHeader(value = "Authorization", required = false) String jwt
    ) {
        log.info("Received request to delete user with jwt: {}", jwt);

        if (jwt == null || jwt.trim().isEmpty()) {
            log.warn("Authorization header is missing");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authorization header is missing"));
        }

        String email = jwtUtil.extractEmailFromJwt(jwt);
        log.debug("Extracted email from JWT: {}", email);

        Long id = userService.userId(email);


        if (id == null) {
            log.error("Invalid email: {}", email);
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid email address"));
        }

        userService.deleteUser(id);
        log.info("User with id [{}] deleted successfully", id);

        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }
}

