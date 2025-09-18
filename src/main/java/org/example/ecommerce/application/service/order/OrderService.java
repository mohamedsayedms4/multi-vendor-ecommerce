package org.example.ecommerce.application.service.order;

import org.example.ecommerce.domain.common.Address;
import org.example.ecommerce.domain.model.cart.Cart;
import org.example.ecommerce.domain.model.order.Order;
import org.example.ecommerce.domain.model.order.OrderItem;
import org.example.ecommerce.domain.model.order.OrderStatus;
import org.example.ecommerce.domain.model.user.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OrderService {

    Set<Order> createOrder(User user , Address shippingAddress , Cart cart);
    Order findOrderById(Long id);
    List<Order> userOrdersHistory(Long userId);

    List<Order> sellerOrdersHistory(Long SellerId);

    Order updateOrderStatus(Long orderId, OrderStatus orderStatus);
    Order cancelOrder(Long orderId , User user);

    OrderItem findOrderItemById(Long orderItemId);
}
