package org.example.ecommerce.application.service.authentication;


import org.example.ecommerce.infrastructure.dto.user.LoginRequestWithPhoneNumber;
import org.example.ecommerce.infrastructure.response.ApiResponse;
import org.example.ecommerce.infrastructure.dto.user.LoginRequestWithEmail;
import org.example.ecommerce.infrastructure.dto.user.SignUpRequest;

import java.util.Optional;

public interface Authentication {
    Optional<ApiResponse> createCustomer(SignUpRequest request );
    Optional<ApiResponse> loginWithEmail(LoginRequestWithEmail request);
    Optional<ApiResponse> loginWithPhoneNumber(LoginRequestWithPhoneNumber request);

    Optional<ApiResponse> loginWithOtp(LoginRequestWithEmail request);
//    AuthResponse verifyOtp(OtpVerificationRequest request);

    String getFcmTokenByPhone(String phoneNumber);

}
