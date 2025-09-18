package org.example.ecommerce.domain.model.order;

/**
 * Represents the different states an order can go through in the order lifecycle.
 * Each status reflects the current progress or outcome of an order.
 *
 * Used in both user-facing and admin-facing order tracking systems.
 *
 * <p>Example usage:
 * <pre>
 * if (order.getStatus() == OrderStatus.SHIPPED) {
 *     notifyCustomer("Your order is on the way!");
 * }
 * </pre>
 * </p>
 *
 * @author Mohamed Sayed
 * @since 27 July 2025
 */
public enum OrderStatus {

    /** Order has been created but not yet confirmed or processed. */
    PENDING,

    /** Order has been successfully placed by the customer. */
    PLACED,

    /** Order has been confirmed by the seller or system and is being prepared. */
    CONFIRMED,

    /** Order has been handed to the shipping provider. */
    SHIPPED,

    /** Order has been delivered to the customer. */
    DELIVERED,

    /** Order was cancelled before being shipped. */
    CANCELLED
}
