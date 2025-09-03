package org.example.ecommerce.application.service.authentication;


import org.example.ecommerce.infrastructure.api.ApiResponse;
import org.example.ecommerce.infrastructure.dto.user.LoginRequest;
import org.example.ecommerce.infrastructure.dto.user.SignUpRequest;

import java.util.Optional;

public interface Authentication {
    Optional<ApiResponse> createCustomer(SignUpRequest request);
    Optional<ApiResponse> login(LoginRequest request);
    Optional<ApiResponse> loginWithOtp(LoginRequest request);
//    AuthResponse verifyOtp(OtpVerificationRequest request);


}
