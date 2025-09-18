package org.example.ecommerce.domain.model.order;


import jakarta.persistence.*;
import lombok.*;
import org.example.ecommerce.domain.model.user.User;
import org.example.ecommerce.domain.payment.PaymentMethod;
import org.example.ecommerce.domain.payment.PaymentStatus;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a payment order entity which groups multiple orders under a single payment transaction.
 * Includes the total amount, payment method, status, and the related user.
 *
 * Typically used when a user pays for multiple orders at once using a single payment link.
 *
 * @author Mohamed Sayed
 * @since 2025-07-27
 */
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PaymentOrder {

    /**
     * Unique identifier for the payment order.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Total amount to be paid for the associated orders.
     */
    private Long amount;

    /**
     * Current status of the payment order.
     * Defaults to {@link PaymentOrderStatus#PENDING}.
     */
    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;

    /**
     * Payment method used to complete the transaction.
     * See {@link PaymentMethod} for available options.
     */
    private PaymentMethod paymentMethod;

    /**
     * External payment link ID, e.g., from Razorpay or another payment gateway.
     */
    private String paymentLinkId;

    /**
     * The user who initiated the payment for the associated orders.
     */
    @ManyToOne
    private User user;

    /**
     * Set of orders associated with this payment order.
     * Multiple orders can be paid together in one transaction.
     */
    @OneToMany
    private Set<Order> orders = new HashSet<>();
}
