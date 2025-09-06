package org.example.ecommerce.infrastructure.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.authentication.Authentication;
import org.example.ecommerce.infrastructure.dto.user.LoginRequestWithEmail;
import org.example.ecommerce.infrastructure.dto.user.LoginRequestWithPhoneNumber;
import org.example.ecommerce.infrastructure.dto.user.SignUpRequestWithoutImageProfille;
import org.example.ecommerce.infrastructure.response.ApiResponse;
import org.example.ecommerce.infrastructure.dto.user.SignUpRequest;
import org.example.ecommerce.infrastructure.utils.ImageUploadUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final Authentication userService;
    private final ImageUploadUtil imageUploadUtil;
    private final Validator validator;


    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse> signup(
            @Valid @RequestPart(value = "data" ,required = true) String request,
            @RequestPart(value = "image", required = false) MultipartFile image) throws JsonProcessingException {

        log.info("user_details : {} + image :{}", request ,image);

        ObjectMapper objectMapper = new ObjectMapper();
        SignUpRequestWithoutImageProfille signUpRequest =
                objectMapper.readValue(request, SignUpRequestWithoutImageProfille.class);

        Set<ConstraintViolation<SignUpRequestWithoutImageProfille>> violations = validator.validate(signUpRequest);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Validation failed", violations);
        }

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = imageUploadUtil.saveImage(image);
            log.info("Uploaded image: {}", imageUrl);
        }


        SignUpRequest updatedRequest = new SignUpRequest(
                signUpRequest.userEmail(),
                signUpRequest.userPassword(),
                signUpRequest.userFullName(),
                signUpRequest.userPhoneNumber(),
                imageUrl
        );
        Optional<ApiResponse> savedUser = userService.createCustomer(updatedRequest);

        if (savedUser.isEmpty()) {
            log.warn("Signup failed for email: {}", updatedRequest.email());
            return ResponseEntity.badRequest().build();
        }

        log.info("Signup successful for email: {}", updatedRequest.email());
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
