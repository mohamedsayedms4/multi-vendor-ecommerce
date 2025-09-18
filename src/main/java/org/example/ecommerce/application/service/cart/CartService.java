package org.example.ecommerce.application.service.cart;

import org.example.ecommerce.domain.model.cart.Cart;
import org.example.ecommerce.domain.model.cart.CartItem;

import java.util.Optional;

public interface CartService {

    /**
     * إضافة عنصر للسلة سواء لمستخدم مسجل أو مؤقت.
     *
     * @param userId إذا كان المستخدم مسجل، otherwise null
     * @param productId معرف المنتج
     * @param quantity الكمية المراد إضافتها
     * @param tempId معرف مؤقت للسلة للمستخدم الغير مسجل (يمكن استخدام UUID)
     * @return العنصر المضاف
     */
    Optional<CartItem> addCartItem(Long userId,
                                   Long productId,
                                   Integer quantity,
                                   String tempId);

    /**
     * جلب السلة الحالية سواء للمستخدم المسجل أو المؤقت
     *
     * @param userId معرف المستخدم المسجل، إذا كان null سيتم استخدام tempId
     * @param tempId معرف مؤقت للسلة للمستخدم الغير مسجل
     * @return السلة الحالية
     */
    Optional<Cart> getCart(Long userId, String tempId);

    /**
     * دمج سلة مؤقتة مع المستخدم المسجل بعد تسجيل الدخول
     *
     * @param tempId معرف السلة المؤقتة
     * @param userId معرف المستخدم المسجل
     */
    void mergeTempCartToUser(String tempId, Long userId);
}
