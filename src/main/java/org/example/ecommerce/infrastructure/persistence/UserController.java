package org.example.ecommerce.infrastructure.persistence;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ecommerce.application.service.UserService;
import org.example.ecommerce.application.service.authentication.Authentication;
import org.example.ecommerce.domain.model.user.User;
import org.example.ecommerce.infrastructure.api.ApiResponse;
import org.example.ecommerce.infrastructure.dto.user.SignUpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserController {
    private final Authentication userService;

    @PostMapping("signup")
    public ResponseEntity<ApiResponse> signup(@Valid @RequestBody SignUpRequest request) {
        Optional<ApiResponse> savedUser = userService.createCustomer(request);
        if (savedUser.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
         return ResponseEntity.status(HttpStatus.CREATED).body(savedUser.get());
    }
}
