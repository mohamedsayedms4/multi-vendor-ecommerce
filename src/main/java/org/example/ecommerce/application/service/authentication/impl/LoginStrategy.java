package org.example.ecommerce.application.service.authentication.impl;

import org.example.ecommerce.infrastructure.response.ApiResponse;

public interface LoginStrategy<T> {
    ApiResponse login(T request);
}
