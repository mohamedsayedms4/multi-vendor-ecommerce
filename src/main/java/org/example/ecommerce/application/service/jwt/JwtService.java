package org.example.ecommerce.application.service.jwt;

import org.example.ecommerce.domain.model.user.User;

public interface JwtService {
     String generateToken(User user) ;
}
