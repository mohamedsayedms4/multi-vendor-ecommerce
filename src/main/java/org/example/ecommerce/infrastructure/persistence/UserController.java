package org.example.ecommerce.infrastructure.persistence;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.user.UserService;
import org.example.ecommerce.infrastructure.dto.user.UserProfile;
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
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getUserProfileByEmail(@PathVariable
                                                   @NotBlank(message = "{email.invalid.blank.input.from.user}")   // لو الايميل فاضي
                                                   @Email(message = "{email.invalid.format.input.from.user}")     // لو الايميل مش صحيح
                                                   String email)
    {
        Optional<UserProfile> user = userService.findByEmail(email);
        if (user.isPresent()) {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/id/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getUserProfileById(
            @NotBlank(message = "id cannot be blank")
            @Pattern(regexp = "^[0-9]+$",message = "Only numbers")
            @PathVariable String id) {
        Optional<UserProfile> user = userService.findById(id);
        if (user.isPresent()) {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


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
}
