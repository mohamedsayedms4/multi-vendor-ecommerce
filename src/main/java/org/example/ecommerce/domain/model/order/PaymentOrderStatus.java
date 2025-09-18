package org.example.ecommerce.domain.model.order;


/**
 * Enum representing the status of a payment order in the system.
 *
 * Used to track the result of a payment attempt, such as whether the payment is still pending,
 * completed successfully, or failed due to some issue (e.g., insufficient funds, network error, etc).
 *
 * <p>Typical usage:
 * <pre>
 * if (paymentOrder.getStatus() == PaymentOrderStatus.SUCCESS) {
 *     // Process order fulfillment
 * }
 * </pre>
 * </p>
 *
 * @author Mohamed Sayed
 * @since 27 July 2025
 */
public enum PaymentOrderStatus {

    /** The payment process is ongoing or has not yet been completed. */
    PENDING,

    /** The payment was completed successfully. */
    SUCCESS,

    /** The payment attempt failed due to an error or rejection. */
    FAILED
}
