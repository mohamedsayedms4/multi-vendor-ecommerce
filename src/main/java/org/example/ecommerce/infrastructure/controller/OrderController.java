package org.example.ecommerce.infrastructure.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.cart.CartService;
import org.example.ecommerce.application.service.order.OrderService;
import org.example.ecommerce.application.service.user.UserService;
import org.example.ecommerce.domain.common.Address;
import org.example.ecommerce.domain.model.cart.Cart;
import org.example.ecommerce.domain.model.order.Order;
import org.example.ecommerce.domain.model.order.OrderItem;
import org.example.ecommerce.domain.model.order.OrderStatus;
import org.example.ecommerce.domain.model.user.User;
import org.example.ecommerce.domain.model.user.exception.UserNotFoundException;
import org.example.ecommerce.infrastructure.mapper.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/orders")
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final CartService cartService;
    /**
     * Create orders from cart for a specific user and shipping address
     */
    @PostMapping("/create")
    public ResponseEntity<Set<Order>> createOrder(
            @RequestHeader(value = "Authorization", required = false) String jwt,
            @CookieValue(value = "TEMP_CART_ID", required = false) String tempId,
            @RequestBody Address address
    ) {
        log.info("----------JWT IS______________ : {}",jwt);
        if (jwt == null || jwt.trim().isEmpty()) {
            log.error("JWT is null or empty");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.emptySet());

        }
        User user = userService.findByJwt(jwt)
                .map(userMapper::toUser)
                .orElseThrow(()-> new UserNotFoundException("user not found"));

        Cart cart = cartService.getCart(user.getId(),tempId).orElseThrow(()->new UserNotFoundException("cart not found"));


//        User user = request.getUser();
//        Address address = request.getShippingAddress();
//        Cart cart = request.getCart();

        Set<Order> orders = orderService.createOrder(user, address, cart);
//        return ResponseEntity.ok(orders);
        return ResponseEntity.ok(orders);
    }


}
