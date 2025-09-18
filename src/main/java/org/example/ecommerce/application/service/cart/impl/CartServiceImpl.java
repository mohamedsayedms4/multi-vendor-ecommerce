package org.example.ecommerce.application.service.cart.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.cart.CartService;
import org.example.ecommerce.domain.model.cart.Cart;
import org.example.ecommerce.domain.model.cart.CartItem;
import org.example.ecommerce.domain.model.cart.repository.CartItemRepository;
import org.example.ecommerce.domain.model.cart.repository.CartRepository;
import org.example.ecommerce.domain.model.product.Product;
import org.example.ecommerce.domain.model.product.repository.ProductRepository;
import org.example.ecommerce.domain.model.user.User;
import org.example.ecommerce.domain.model.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * إضافة منتج إلى السلة سواء للمستخدم المسجل أو الـ guest
     */
    @Override
    @Transactional
    public Optional<CartItem> addCartItem(Long userId, Long productId, Integer quantity, String tempId) {
        log.info("➡️ addCartItem started. userId={}, productId={}, quantity={}, tempId={}", userId, productId, quantity, tempId);

        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (quantity == null || quantity <= 0) {
                throw new RuntimeException("Quantity must be greater than 0");
            }

            Cart cart;
            if (userId != null) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                cart = cartRepository.findByUser(user).orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
            } else {
                cart = cartRepository.findByTempId(tempId).orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setTempId(tempId);
                    return cartRepository.save(newCart);
                });
            }

            CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
                    .orElseGet(() -> {
                        CartItem newItem = new CartItem();
                        newItem.setCart(cart);
                        newItem.setProduct(product);
                        newItem.setUserId(userId);
                        newItem.setQuantity(0);
                        return newItem;
                    });

            int newQuantity = cartItem.getQuantity() + quantity;
            cartItem.setQuantity(newQuantity);
            cartItem.setSellingPrice((long) newQuantity * product.getSellingPrice());
            cartItem.setMaximumRetailPrice((long) newQuantity * product.getMaximumRetailPrice());

            cartItem = cartItemRepository.save(cartItem);

            if (!cart.getCartItems().contains(cartItem)) {
                cart.getCartItems().add(cartItem);
            }

            recalculateCartTotals(cart);
            cartRepository.save(cart);

            log.info("✅ addCartItem finished successfully for userId={}, productId={}", userId, productId);
            return Optional.of(cartItem);

        } catch (Exception e) {
            log.error("❌ Error in addCartItem: {}", e.getMessage(), e);
            throw new RuntimeException("Error adding item to cart: " + e.getMessage());
        }
    }

    /**
     * جلب السلة للمستخدم المسجل أو الـ guest
     */
    @Override
    public Optional<Cart> getCart(Long userId, String tempId) {
        checkIfUserLoginAndHaveCookie( userId,  tempId);

        try {
            Cart cart;
            if (userId != null) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                // إرجاع Optional.empty() بدلاً من رمي استثناء
                Optional<Cart> cartOpt = cartRepository.findByUser(user);
                if (cartOpt.isEmpty()) {
                    log.info("No cart found for user: {}", userId);
                    return Optional.empty();
                }
                cart = cartOpt.get();

            } else {
                // التعامل مع حالة tempId
                if (tempId == null || tempId.isEmpty()) {
                    log.info("No tempId provided and no user authenticated");
                    return Optional.empty();
                }

                Optional<Cart> cartOpt = cartRepository.findByTempId(tempId);
                if (cartOpt.isEmpty()) {
                    log.info("No cart found for tempId: {}", tempId);
                    return Optional.empty();
                }
                cart = cartOpt.get();
            }

            recalculateCartTotals(cart);
            return Optional.of(cart);

        } catch (Exception e) {
            log.error("Error retrieving cart: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving cart: " + e.getMessage());
        }
    }

//    @Transactional
//    public void checkIfUserLoginAndHaveCookie(Long userId, String tempId) {
//        if (userId != null && tempId != null) {
//            Cart cart = cartRepository.findByTempId(tempId)
//                    .orElseThrow(() -> new RuntimeException("Temporary cart with ID " + tempId + " not found"));
//
//            User user = userRepository.findById(userId)
//                    .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));
//
//            cart.setUser(user);
//
//            // يمكن حذف السطر التالي إذا اعتمدت على الـ @Transactional و JPA
//            cartRepository.save(cart);
//        }
//    }

    @Transactional
    public void checkIfUserLoginAndHaveCookie(Long userId, String tempId) {
        if (userId != null && tempId != null) {
            Cart tempCart = cartRepository.findByTempId(tempId).orElse(null);
            if (tempCart == null) return;

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));

            Cart userCart = cartRepository.findByUser(user).orElse(null);

            if (userCart != null) {
                // دمج السلة المؤقتة مع سلة المستخدم
                for (CartItem tempItem : tempCart.getCartItems()) {
                    CartItem existingItem = cartItemRepository.findByCartAndProduct(userCart, tempItem.getProduct())
                            .orElse(null);

                    if (existingItem != null) {
                        existingItem.setQuantity(existingItem.getQuantity() + tempItem.getQuantity());
                        existingItem.setSellingPrice(existingItem.getQuantity() * tempItem.getProduct().getSellingPrice());
                        existingItem.setMaximumRetailPrice(existingItem.getQuantity() * tempItem.getProduct().getMaximumRetailPrice());
                        existingItem.setUserId(user.getId()); // تعيين userId الحقيقي
                        cartItemRepository.save(existingItem);
                    } else {
                        tempItem.setCart(userCart);
                        tempItem.setUserId(user.getId()); // تعيين userId الحقيقي
                        userCart.getCartItems().add(tempItem);
                    }
                }

                recalculateCartTotals(userCart);
                cartRepository.save(userCart);
                cartRepository.delete(tempCart);
            } else {
                // ربط tempCart مباشرة بالمستخدم إذا لم يكن لديه سلة
                tempCart.setUser(user);
                tempCart.setTempId(null); // إزالة tempId لتجنب تكرار الفريد
                for (CartItem item : tempCart.getCartItems()) {
                    item.setUserId(user.getId()); // تأكد من أن كل العناصر لها userId
                }
                cartRepository.save(tempCart);
            }
        }
    }

    @Override
    @Transactional
    public void mergeTempCartToUser(String tempId, Long userId) {
        checkIfUserLoginAndHaveCookie(userId, tempId);
    }

    /**
     * إعادة حساب المجاميع والخصومات للسلة
     */
    private void recalculateCartTotals(Cart cart) {
        long totalPrice = 0L;
        long totalDiscountPrice = 0L;
        int totalItem = 0;

        for (CartItem cartItem : cart.getCartItems()) {
            Long sellingPrice = cartItem.getSellingPrice();
            Long maximumPrice = cartItem.getMaximumRetailPrice();
            Integer itemQuantity = cartItem.getQuantity();

            if (sellingPrice != null && maximumPrice != null && itemQuantity != null) {
                totalDiscountPrice += sellingPrice;
                totalPrice += maximumPrice;
                totalItem += itemQuantity;
            }
        }

        cart.setTotalMaximumRetailPrice(totalPrice);
        cart.setTotalSellingPrice(totalDiscountPrice);
        cart.setDiscount(calculateDiscountPercentage(totalPrice, totalDiscountPrice));
        cart.setQuantity(totalItem);
    }

    private long calculateDiscount(long totalPrice, long totalDiscountPrice) {
        return (totalPrice <= 0 || totalDiscountPrice < 0) ? 0L : totalPrice - totalDiscountPrice;
    }

    private long calculateDiscountPercentage(long totalPrice, long totalDiscountPrice) {
        if (totalPrice <= 0) return 0L;
        long discount = calculateDiscount(totalPrice, totalDiscountPrice);
        return (discount * 100) / totalPrice;
    }
}
