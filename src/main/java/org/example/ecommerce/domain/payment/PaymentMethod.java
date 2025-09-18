package org.example.ecommerce.domain.payment;


/**
 * Enum representing the available payment methods supported by the system.
 *
 * This is used to determine which payment provider is used when a user completes a transaction.
 * Can be extended in the future to support more providers like PayPal, Apple Pay, etc.
 *
 * <p>Example usage:
 * <pre>
 * if (payment.getMethod() == PaymentMethod.RAZORPAY) {
 *     razorpayService.processPayment(payment);
 * }
 * </pre>
 * </p>
 *
 * @author Mohamed Sayed
 * @since 27 July 2025
 */
public enum PaymentMethod {

    /** Payment processed using Razorpay. Ideal for India-based transactions. */
//    RAZORPAY,

    /** Payment processed using Stripe. Widely used for international transactions. */
    STRIPE
}
