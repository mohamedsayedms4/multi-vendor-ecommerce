package org.example.ecommerce.application.service.order.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.order.OrderService;
import org.example.ecommerce.domain.common.Address;
import org.example.ecommerce.domain.common.repository.AddressRepository;
import org.example.ecommerce.domain.model.cart.Cart;
import org.example.ecommerce.domain.model.cart.CartItem;
import org.example.ecommerce.domain.model.order.Order;
import org.example.ecommerce.domain.model.order.OrderItem;
import org.example.ecommerce.domain.model.order.OrderStatus;
import org.example.ecommerce.domain.model.order.repository.OrderItemRepository;
import org.example.ecommerce.domain.model.order.repository.OrderRepository;
import org.example.ecommerce.domain.model.user.User;
import org.example.ecommerce.domain.payment.PaymentStatus;
import org.example.ecommerce.infrastructure.event.OrderEvent;
import org.example.ecommerce.infrastructure.event.ProductEvent;
import org.example.ecommerce.infrastructure.event.Type;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ApplicationEventPublisher eventPublisher;

    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final OrderItemRepository orderItemRepository;
//    @Override
//    @Transactional
//    public Set<Order> createOrder(User user, Address shippingAddress, Cart cart) {
//        if (!user.getPickupAddress().contains(shippingAddress)) {
//            user.getPickupAddress().add(shippingAddress);
//        }
//        Address address = addressRepository.save(shippingAddress);
//
////        brand- 1 ----------> 4 t shirt
////        brand- 2 ------------> 3 mobile
////        brand- 3 --------------> 1 watch
//
//        Map<Long, List<CartItem>> itemsBySeller =
//                cart.getCartItems().stream()
//                        .collect(Collectors.groupingBy(item -> item.getProduct()
//                                .getSeller().getId()));
//        Set<Order> orders = new HashSet<>();
//        Order createdOrder = null;
//        for (Map.Entry<Long, List<CartItem>> entry : itemsBySeller.entrySet()) {
//            Long sellerId = entry.getKey();
//            List<CartItem> items = entry.getValue();
//
//            int totalOrderPrice = Math.toIntExact(items.stream().mapToLong(
//                    CartItem::getSellingPrice
//            ).sum());
//
//            int totalItem = items.stream().mapToInt(CartItem::getQuantity).sum();
//
//            createdOrder = new Order();
//            createdOrder.setUser(user);
//            createdOrder.setSellerId(sellerId);
//            createdOrder.setTotalMrpPrice(totalOrderPrice);
//            createdOrder.setTotalSellingPrice(totalOrderPrice);
//            createdOrder.setTotalItems(totalItem);
//            createdOrder.setShippingAddress(address);
//            createdOrder.setOrderStatus(OrderStatus.PENDING);
//            createdOrder.getPaymentDetails().setPaymentStatus(PaymentStatus.PENDING);
//            Order savedOrder = orderRepository.save(createdOrder);
//            orders.add(savedOrder);
//
//            List<OrderItem> orderItems = new ArrayList<>();
//
//            for (CartItem item : items) {
//                OrderItem orderItem = new OrderItem();
//                orderItem.setOrder(savedOrder);
//                orderItem.setMrpPrice(Double.valueOf(item.getMaximumRetailPrice()));
//                orderItem.setProduct(item.getProduct());
//                orderItem.setQuantity(item.getQuantity());
//                orderItem.setUserId(item.getUserId());
//                orderItem.setSellingPrice(Double.valueOf(item.getSellingPrice()));
//                savedOrder.getOrderItems().add(orderItem);
//                OrderItem savedOrderItem = orderItemRepository.save(orderItem);
//
//                orderItems.add(savedOrderItem);
//            }
//        }
//        return orders;
//    }

@Override
@Transactional
public Set<Order> createOrder(User user, Address shippingAddress, Cart cart) {
    if (!user.getPickupAddress().contains(shippingAddress)) {
        user.getPickupAddress().add(shippingAddress);
    }
    Address address = addressRepository.save(shippingAddress);

    Map<Long, List<CartItem>> itemsBySeller =
            cart.getCartItems().stream()
                    .collect(Collectors.groupingBy(item -> item.getProduct()
                            .getSeller().getId()));
    Set<Order> orders = new HashSet<>();
    Order createdOrder = null;
    for (Map.Entry<Long, List<CartItem>> entry : itemsBySeller.entrySet()) {
        Long sellerId = entry.getKey();
        List<CartItem> items = entry.getValue();

        Long totalOrderPrice = items.stream().mapToLong(
                CartItem::getSellingPrice
        ).sum();

        int totalItem = items.stream().mapToInt(CartItem::getQuantity).sum();

        createdOrder = new Order();

        // Generate unique order ID - you can customize this format
        String orderIdGenerated = "ORD-" + System.currentTimeMillis() + "-" + sellerId;
        createdOrder.setOrderId(orderIdGenerated); // THIS IS THE FIX

        createdOrder.setUser(user);
        createdOrder.setSellerId(sellerId);
        createdOrder.setTotalMrpPrice(totalOrderPrice);
        createdOrder.setTotalSellingPrice(totalOrderPrice);
        createdOrder.setTotalItems(totalItem);
        createdOrder.setShippingAddress(address);
        createdOrder.setOrderStatus(OrderStatus.PENDING);
        createdOrder.getPaymentDetails().setPaymentStatus(PaymentStatus.PENDING);
        Order savedOrder = orderRepository.save(createdOrder);
        orders.add(savedOrder);

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem item : items) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setMrpPrice(Double.valueOf(item.getMaximumRetailPrice()));
            orderItem.setProduct(item.getProduct());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setUserId(item.getUserId());
            orderItem.setSellingPrice(Double.valueOf(item.getSellingPrice()));
            savedOrder.getOrderItems().add(orderItem);
            OrderItem savedOrderItem = orderItemRepository.save(orderItem);

            orderItems.add(savedOrderItem);
        }
    }
    eventPublisher.publishEvent(new OrderEvent(orders, user, Type.CREATED,cart));
    log.info("Product saved in database: {}", orders);
    return orders;
}
    @Override
    public Order findOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public List<Order> userOrdersHistory(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public List<Order> sellerOrdersHistory(Long SellerId) {
        return orderRepository.findBySellerId(SellerId);
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus orderStatus) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setOrderStatus(orderStatus);
        orderRepository.save(order);
        return order;
    }

    @Override
    @Transactional
    public Order cancelOrder(Long orderId, User user) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        if (!user.getId().equals(order.getUser().getId())) {
            throw new RuntimeException("User not authorized to order");
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
//        return null;
    }

    @Override
    public OrderItem findOrderItemById(Long orderItemId) {
        return orderItemRepository.findById(orderItemId).orElseThrow(() -> new RuntimeException("OrderItem not found"));
    }
}
