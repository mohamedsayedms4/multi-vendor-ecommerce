package org.example.ecommerce.infrastructure.persistence;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.user.UserService;
import org.example.ecommerce.infrastructure.dto.user.UserProfile;
import org.example.ecommerce.infrastructure.dto.user.UserUpdateDto;
import org.example.ecommerce.infrastructure.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

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


}
