package org.example.ecommerce.application.service.payment;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.domain.model.order.Order;
import org.example.ecommerce.domain.model.order.PaymentOrder;
import org.example.ecommerce.domain.model.order.repository.OrderRepository;
import org.example.ecommerce.domain.model.user.User;
import org.example.ecommerce.domain.payment.PaymentOrderNotFoundException;
import org.example.ecommerce.domain.payment.PaymentStatus;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Service implementation for handling payment orders.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentOrderRepository paymentOrderRepository;
    private final OrderRepository orderRepository;

    /**
     * Creates a new payment order for the given user and set of orders.
     */
    @Override
    @Transactional
    public PaymentOrder createOrder(User user, Set<Order> orders) {
        Long amount = orders.stream()
                .mapToLong(Order::getTotalSellingPrice)
                .sum();

        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setUser(user);
        paymentOrder.setAmount(amount);
        paymentOrder.setOrders(orders);
        paymentOrder.setStatus(PaymentStatus.PENDING);

        log.info("Creating payment order for user {} with amount {}", user.getId(), amount);
        return paymentOrderRepository.save(paymentOrder);
    }

    /**
     * Retrieves a payment order by its ID.
     */
    @Override
    public PaymentOrder getPaymentOrderById(Long orderId) {
        log.info("Fetching payment order by id {}", orderId);
        return paymentOrderRepository.findById(orderId)
                .orElseThrow(() -> new PaymentOrderNotFoundException("PaymentOrder not found with id: " + orderId));
    }

    /**
     * Retrieves a payment order by its payment link ID.
     */
    @Override
    public PaymentOrder getPaymentOrderByPaymentId(String paymentLinkId) {
        log.info("Fetching payment order by paymentLinkId {}", paymentLinkId);
        return paymentOrderRepository.findByPaymentLinkId(paymentLinkId)
                .orElseThrow(() -> new PaymentOrderNotFoundException("PaymentOrder not found with paymentLinkId: " + paymentLinkId));
    }



    /**
     * Proceeds with payment for the given payment order.
     */
    @Override
    @Transactional
    public Boolean ProceedPaymentOrder(PaymentOrder paymentOrder,
                                       String paymentId, String paymentLinkId) {
        if (!paymentOrder.getStatus().equals(PaymentStatus.PENDING)) {
            log.warn("Payment order {} already processed with status {}",
                    paymentOrder.getId(), paymentOrder.getStatus());
            return false;
        }

        paymentOrder.setStatus(PaymentStatus.COMPLETED);
        paymentOrder.setPaymentLinkId(paymentId);          // Payment transaction ID
        paymentOrder.setPaymentLinkId(paymentLinkId);  // Payment link ID

        paymentOrderRepository.save(paymentOrder);
        log.info("Payment order {} marked as COMPLETED with paymentId {} and paymentLinkId {}",
                paymentOrder.getId(), paymentId, paymentLinkId);
        return true;
    }

    /**
     * Creates a Stripe payment link for the user.
     * TODO: دمج مع Stripe API الفعلي هنا.
     */
    @Override
    public String createStripePaymentLink(User user, Long amount, Long orderId) {
        log.info("Creating Stripe payment link for user {} and order {}", user.getId(), orderId);

        // Example placeholder link; replace with actual Stripe API call
        String demoLink = "https://stripe.com/payment-link-demo";

        log.info("Stripe payment link created: {}", demoLink);
        return demoLink;
    }
}
