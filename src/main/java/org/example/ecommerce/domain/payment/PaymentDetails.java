package org.example.ecommerce.domain.payment;

import lombok.Data;
import org.example.ecommerce.domain.model.order.Order;
import java.time.Instant;

/**
 * Represents the payment-related information associated with an order.
 * Includes identifiers and statuses returned from the Stripe payment gateway.
 *
 * This class is typically used as an embedded object in the {@link Order} entity.
 * Supports multi-vendor payments via Stripe Connected Accounts.
 *
 * Author: Mohamed Sayed
 * Since: 2025-09-18
 */
@Data
public class PaymentDetails {

    /**
     * General payment identifier (internal system ID).
     */
    private String paymentId;

    /**
     * Stripe PaymentIntent ID.
     * Each PaymentIntent corresponds to a single payment attempt.
     */
    private String stripePaymentIntentId;

    /**
     * Stripe Charge ID returned after a successful payment.
     * Optional if the PaymentIntent is not yet completed.
     */
    private String stripeChargeId;

    /**
     * Vendor/Connected Account ID (Stripe Connected Account).
     * Useful for multi-vendor marketplaces to route payments.
     */
    private String vendorAccountId;

    /**
     * Amount paid in cents.
     */
    private Long amount;

    /**
     * Currency of the payment (e.g., USD, EGP).
     */
    private String currency;

    /**
     * Status of the payment (e.g., PENDING, SUCCEEDED, FAILED).
     */
    private PaymentStatus paymentStatus;

    /**
     * Timestamp of the payment creation.
     */
    private Instant createdAt;
}
