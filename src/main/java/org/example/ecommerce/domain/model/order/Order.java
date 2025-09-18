package org.example.ecommerce.domain.model.order;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.example.ecommerce.domain.common.Address;
import org.example.ecommerce.domain.common.BaseEntity;
import org.example.ecommerce.domain.model.user.User;
import org.example.ecommerce.domain.payment.PaymentDetails;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer order in the system.
 * Each order is linked to a user and contains multiple order items,
 * shipping details, payment information, and delivery metadata.
 *
 * This entity is mapped to the "orders" table in the database.
 *
 * @author Mohamed Sayed
 * @since 2025-07-27
 */
@Entity

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)

@Table(name = "orders")
public class Order extends BaseEntity {



    /**
     * Business-generated order identifier (can be UUID or custom format).
     */

    @Column(nullable = false, unique = true)
    private String orderId;

    /**
     * The user who placed the order.
     */
    @ManyToOne
    private User user;

    /**
     * ID of the seller who is responsible for fulfilling the order.
     */
    private Long sellerId;

    /**
     * List of items associated with this order.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    /**
     * Shipping address where the order will be delivered.
     */
    @ManyToOne
    private Address shippingAddress;

    /**
     * Embedded payment details including method and status.
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "createdAt", column = @Column(name = "payment_created_at"))
    })    private PaymentDetails paymentDetails = new PaymentDetails();

    /**
     * Total MRP (original) price before discounts.
     */
    private Long totalMrpPrice;

    /**
     * Total price after applying discounts.
     */
    private Long totalSellingPrice;

    /**
     * Discount percentage or value applied to the order.
     */
    private Integer discount;

    /**
     * Current status of the order (e.g., PENDING, SHIPPED, DELIVERED).
     */
    private OrderStatus orderStatus;

    /**
     * Total number of items in the order.
     */
    private Integer totalItems;

    /**
     * Date and time when the order was placed.
     */
//    private LocalDateTime orderDate = LocalDateTime.now();

    /**
     * Estimated delivery date for the order (default is 7 days from order).
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deliverDate;

    @Override

    protected void onPrePersist() {
        super.onPrePersist();
        this.setOrderDate();
    }

    public void setOrderDate() {
        deliverDate = createdAt.plusDays(7);
    }
}
