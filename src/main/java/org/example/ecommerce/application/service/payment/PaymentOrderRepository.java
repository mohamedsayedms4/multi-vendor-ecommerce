package org.example.ecommerce.application.service.payment;

import org.example.ecommerce.domain.model.order.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long> {
    Optional<PaymentOrder> findByPaymentLinkId(String paymentLinkId);

}
