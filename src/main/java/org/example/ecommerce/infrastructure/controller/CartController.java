package org.example.ecommerce.infrastructure.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.cart.CartService;
import org.example.ecommerce.domain.model.cart.Cart;
import org.example.ecommerce.infrastructure.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;
    private final JwtUtil jwtUtil;

    @PutMapping
    public ResponseEntity<?> addToCart(
            @RequestParam("product-id") Long productId,
            @RequestParam("quantity") Integer quantity,
            @CookieValue(value = "TEMP_CART_ID", required = false) String tempId,
            @RequestHeader(value = "Authorization", required = false) String jwt,
            HttpServletResponse response) {

        if (tempId == null || tempId.isEmpty()) {
            tempId = UUID.randomUUID().toString();
            Cookie cookie = new Cookie("TEMP_CART_ID", tempId);
            cookie.setPath("/");
            cookie.setHttpOnly(false);
            cookie.setMaxAge(7 * 24 * 3600); // أسبوع
            response.addCookie(cookie);
        }

        Long userId = null;
        if (jwt != null && !jwt.isEmpty()) {
            userId = jwtUtil.extractUserIdFromJwt(jwt);
            cartService.mergeTempCartToUser(tempId, userId);
        }

        cartService.addCartItem(userId, productId, quantity, tempId);

        return ResponseEntity.ok(Map.of("message", "Item added to cart", "tempId", tempId));
    }

    @GetMapping
    public ResponseEntity<?> getCart(
            @CookieValue(value = "TEMP_CART_ID", required = false) String tempId,
            @RequestHeader(value = "Authorization", required = false) String jwt) {

        Long userId = null;
        if (jwt != null && !jwt.isEmpty()) {
            userId = jwtUtil.extractUserIdFromJwt(jwt);
        }

        Cart cart = cartService.getCart(userId ,tempId).orElse(null);
        return ResponseEntity.ok(cart);
    }
}
