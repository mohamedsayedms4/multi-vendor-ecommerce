package org.example.ecommerce.infrastructure.listener;

import lombok.RequiredArgsConstructor;
import org.example.ecommerce.application.service.EmailService;
import org.example.ecommerce.domain.model.notification.Notification;
import org.example.ecommerce.domain.model.notification.repository.NotificationRepository;
import org.example.ecommerce.domain.model.order.Order;
import org.example.ecommerce.domain.model.user.UserRole;
import org.example.ecommerce.infrastructure.event.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderCustomerEventListener {

    private static final Logger logger = LoggerFactory.getLogger(OrderCustomerEventListener.class);

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleOrderEvent(OrderEvent event) {
        try {
            logger.info("Processing order event for user: {}", event.getCustomer().getId());

            // بناء معرفات الطلبات
            StringBuilder orderIds = new StringBuilder();
            for (Order order : event.getOrders()) {
                if (orderIds.length() > 0) {
                    orderIds.append(", ");
                }
                orderIds.append(order.getOrderId());
            }

            // بناء أسماء المنتجات
            String productNames = event.getCart().getCartItems()
                    .stream()
                    .map(item -> item.getProduct().getTitle())
                    .collect(Collectors.joining(", "));

            // رسالة العميل
            String customerMessage = String.format(
                    "Your order [%s] has been successfully placed. Products: %s",
                    orderIds.toString(),
                    productNames
            );

            // إنشاء الإشعار
            Notification customerNotification = Notification.builder()
                    .title("Order Placed Successfully")
                    .message(customerMessage)
                    .read(false)
                    .role(UserRole.ROLE_CUSTOMER)
                    .user(event.getCustomer())
                    .build();

            // حفظ الإشعار في قاعدة البيانات
            Notification savedNotification = notificationRepository.save(customerNotification);
            logger.info("Notification saved with ID: {}", savedNotification.getId());

            // إرسال بريد إلكتروني للعميل
            try {
                emailService.sendEmail(
                        event.getCustomer().getEmail(),
                        "Order Placed Successfully",
                        customerMessage
                );
                logger.info("Email sent to: {}", event.getCustomer().getEmail());
            } catch (Exception e) {
                logger.error("Failed to send email to: {}", event.getCustomer().getEmail(), e);
                // لا نوقف العملية إذا فشل البريد الإلكتروني
            }

            // إرسال WebSocket للعميل
            String topicDestination = "/topic/notifications/user" + event.getCustomer().getId();
            logger.info("Sending WebSocket notification to: {}", topicDestination);

            try {
                messagingTemplate.convertAndSend(topicDestination, savedNotification);
                logger.info("WebSocket notification sent successfully to topic: {}", topicDestination);
            } catch (Exception e) {
                logger.error("Failed to send WebSocket notification to topic: {}", topicDestination, e);
            }

        } catch (Exception e) {
            logger.error("Error processing order event", e);
            // يمكن إضافة آلية إعادة المحاولة هنا إذا لزم الأمر
        }
    }
}