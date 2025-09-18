package org.example.ecommerce.application.service.payment;

import org.example.ecommerce.domain.model.order.Order;
import org.example.ecommerce.domain.model.order.PaymentOrder;
import org.example.ecommerce.domain.model.user.User;

import java.util.Set;

public interface PaymentService {

    PaymentOrder createOrder(User user, Set<Order>orders);
    PaymentOrder getPaymentOrderById(Long orderId);

    PaymentOrder getPaymentOrderByPaymentId(String orderId);

    Boolean ProceedPaymentOrder(PaymentOrder paymentOrder,
                                String paymentId,
                                String paymentLinkId);

    String createStripePaymentLink(User user,
                                   Long amount ,
                                   Long orderId);
}
