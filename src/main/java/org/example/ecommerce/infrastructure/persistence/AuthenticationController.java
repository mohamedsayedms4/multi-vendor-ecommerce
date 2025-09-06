package org.example.ecommerce.infrastructure.persistence;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.authentication.Authentication;
import org.example.ecommerce.infrastructure.dto.user.LoginRequestWithEmail;
import org.example.ecommerce.infrastructure.dto.user.LoginRequestWithPhoneNumber;
import org.example.ecommerce.infrastructure.response.ApiResponse;
import org.example.ecommerce.infrastructure.dto.user.SignUpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final Authentication userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@Valid @RequestBody SignUpRequest request) {
        log.info("Signup request received for email: {}", request.userEmail());

        Optional<ApiResponse> savedUser = userService.createCustomer(request);

        if (savedUser.isEmpty()) {
            log.warn("Signup failed for email: {}", request.userEmail());
            return ResponseEntity.badRequest().build();
        }

        log.info("Signup successful for email: {}", request.userEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser.get());
    }

    @PostMapping("/login-email")
    public ResponseEntity<ApiResponse> loginWithEmail(@Valid @RequestBody LoginRequestWithEmail request) {
        log.info("Login with email request received: {}", request.email());

        Optional<ApiResponse> loginUser = userService.loginWithEmail(request);

        if (loginUser.isEmpty()) {
            log.warn("Login with email failed for: {}", request.email());
            return ResponseEntity.badRequest().build();
        }

        log.info("Login with email successful for: {}", request.email());
        return ResponseEntity.status(HttpStatus.OK).body(loginUser.get());
    }

    @PostMapping("/login-phone")
    public ResponseEntity<ApiResponse> loginWithPhone(@Valid @RequestBody LoginRequestWithPhoneNumber request) {
        log.info("Login with phone request received: {}", request.userPhoneNumber());

        Optional<ApiResponse> loginUser = userService.loginWithPhoneNumber(request);

        if (loginUser.isEmpty()) {
            log.warn("Login with phone failed for: {}", request.userPhoneNumber());
            return ResponseEntity.badRequest().build();
        }

        log.info("Login with phone successful for: {}", request.userPhoneNumber());
        return ResponseEntity.status(HttpStatus.OK).body(loginUser.get());
    }
}
