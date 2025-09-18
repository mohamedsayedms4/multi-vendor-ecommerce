package org.example.ecommerce.application.service.cart.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.cart.CartItemService;
import org.example.ecommerce.domain.model.cart.CartItem;
import org.example.ecommerce.domain.model.cart.repository.CartItemRepository;
import org.example.ecommerce.domain.model.user.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    @Override
    @Transactional
    public CartItem updateCartItem(Long userId, Long id, CartItem cartItem) throws Exception {
        CartItem item = cartItemRepository.findCartItemById(id)
                .orElseThrow(()->new  Exception("CartItem not found"));
        User cartItemUser = item.getCart().getUser();

        if (cartItemUser.getId().equals(userId)) {
            item.setQuantity(cartItem.getQuantity() + cartItem.getQuantity());
            item.setSellingPrice(item.getQuantity() * cartItem.getSellingPrice());
            item.setSellingPrice(item.getQuantity() * cartItem.getSellingPrice());
        }
        throw new Exception("you cannot update cart");
    }

    @Override
    @Transactional
    public void deleteCartItem(Long userId, Long cartItemId) throws Exception {
        CartItem cartItem = cartItemRepository.findCartItemById(cartItemId)
                .orElseThrow(()->new  Exception("CartItem not found"));
        User cartItemUser = cartItem.getCart().getUser();
        if (cartItemUser.getId().equals(userId)) {
            cartItemRepository.deleteCartItemById(cartItemId);
        }
         throw new Exception("cannot delete cartItem");
    }

    @Override
    public CartItem getCartItem(Long cartItemId) throws Exception {

        return cartItemRepository.findCartItemById(cartItemId)
                .orElseThrow(()->new  Exception("CartItem not found"));
    }
}
