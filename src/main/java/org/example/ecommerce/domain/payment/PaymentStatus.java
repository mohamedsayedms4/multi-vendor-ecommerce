package org.example.ecommerce.domain.payment;


/**
 * Enum representing the lifecycle status of a payment transaction.
 *
 * <p>This status is used to track the progress of a payment operation
 * from initiation to finalization.</p>
 *
 * <ul>
 *   <li>{@link #PENDING} - Payment has been initiated but not yet processed.</li>
 *   <li>{@link #PROCESSING} - Payment is currently being handled by the system or a payment gateway.</li>
 *   <li>{@link #COMPLETED} - Payment was successfully completed.</li>
 *   <li>{@link #FAILED} - Payment failed due to an error or rejection.</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>
 * if (payment.getStatus() == PaymentStatus.COMPLETED) {
 *     // Grant user access to purchased items
 * }
 * </pre>
 *
 * @author Mohamed Sayed
 * @since 27 July 2025
 */
public enum PaymentStatus {

    /** Payment has been initiated but not yet processed. */
    PENDING,

    /** Payment is currently being processed by the system. */
    PROCESSING,

    /** Payment completed successfully. */
    COMPLETED,

    /** Payment failed due to an error or rejection. */
    FAILED
}
