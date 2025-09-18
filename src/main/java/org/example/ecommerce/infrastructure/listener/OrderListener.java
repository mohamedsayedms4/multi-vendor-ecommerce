package org.example.ecommerce.infrastructure.listener;

import lombok.RequiredArgsConstructor;
import org.example.ecommerce.application.service.EmailService;
import org.example.ecommerce.domain.model.admin.Admin;
import org.example.ecommerce.domain.model.admin.AdminRepository;
import org.example.ecommerce.domain.model.notification.Notification;
import org.example.ecommerce.domain.model.notification.repository.NotificationRepository;
import org.example.ecommerce.domain.model.order.Order;
import org.example.ecommerce.domain.model.user.UserRole;
import org.example.ecommerce.infrastructure.event.OrderEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderListener {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final SimpMessagingTemplate messagingTemplate;
    private final AdminRepository adminRepository;

    @EventListener
    public void handleOrderEvent(OrderEvent event) {
        String customerName = event.getCustomer().getFullName();
        StringBuilder orderIds = new StringBuilder();

        for (Order order : event.getOrders()) {
            orderIds.append(order.getOrderId()).append(", ");
        }

        if (orderIds.length() > 2) {
            orderIds.setLength(orderIds.length() - 2);
        }

        String productNames = event.getCart().getCartItems()
                .stream()
                .map(item -> item.getProduct().getTitle())
                .collect(Collectors.joining(", "));

        String fullMessage = "Orders [" + orderIds + "] have been placed by " + customerName
                + ". Products: " + productNames;

        // إنشاء Notification
        Notification notification = Notification.builder()
                .title("New Orders Placed")
                .message(fullMessage)
                .read(false)
                .role(UserRole.ROLE_ADMIN)
                .build();
        notificationRepository.save(notification);

        // إرسال بريد إلكتروني للعميل
        emailService.sendEmail(
                event.getCustomer().getEmail(),
                "New Orders Placed",
                fullMessage
        );

        // إرسال بريد إلكتروني لكل Admins
        sendEmailToAllAdmins("New Orders Placed", fullMessage);

        // إرسال WebSocket للـ Admins (topic عام)
        messagingTemplate.convertAndSend("/topic/notifications", notification);

    }

    private void sendEmailToAllAdmins(String title, String htmlMessage) {
        List<Admin> admins = adminRepository.findAll();
        for (Admin admin : admins) {
            emailService.sendEmail(admin.getEmail(), title, htmlMessage);
        }
    }
}
