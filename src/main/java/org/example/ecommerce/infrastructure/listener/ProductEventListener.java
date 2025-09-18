package org.example.ecommerce.infrastructure.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.EmailService;
import org.example.ecommerce.domain.model.admin.Admin;
import org.example.ecommerce.domain.model.admin.AdminRepository;
import org.example.ecommerce.domain.model.notification.Notification;
import org.example.ecommerce.domain.model.notification.repository.NotificationRepository;
import org.example.ecommerce.domain.model.product.Product;
import org.example.ecommerce.infrastructure.event.ProductEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventListener {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final AdminRepository adminRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleProductEvent(ProductEvent event) {
        Product product = event.getProduct();
        String userName = event.getPerformedBy();
        Long userId = event.getPerformedById();
        String title;
        String action;

        switch (event.getEventType()) {
            case CREATED:
                title = "🛒 منتج جديد: " + product.getTitle();
                action = "تمت الإضافة بواسطة";
                break;
            case UPDATED:
                title = "✏️ تم تعديل المنتج: " + product.getTitle();
                action = "تم التعديل بواسطة";
                break;
            case DELETED:
                title = "🗑️ تم حذف المنتج: " + product.getTitle();
                action = "تم الحذف بواسطة";
                break;
            default:
                title = product.getTitle();
                action = "";
        }

        String htmlMessage = "<div dir='rtl' style='font-family:Arial,sans-serif;color:#333;'>"
                + "<h2 style='color:#2a9d8f;'>" + title + "</h2>"
                + "<p><strong>" + action + ":</strong> " + userName + " (ID: " + userId + ")</p>"
                + "<p><strong>العنوان:</strong> " + product.getTitle() + "</p>"
                + "<p><strong>الوصف:</strong> " + product.getDescription() + "</p>"
                + buildImagesGallery(product)
                + "<p style='margin-top:10px;color:#888;font-size:12px;'>هذه رسالة إشعار تلقائية من متجرنا</p>"
                + "</div>";

        String textMessage = action + ": " + userName + "\nTitle: " + product.getTitle()
                + "\nDescription: " + product.getDescription();

        Notification notification = Notification.builder()
                .title(title)
                .message(textMessage)
                .build();
        notificationRepository.save(notification);
        messagingTemplate.convertAndSend("/topic/notifications", notification);

        sendEmailToAllAdmins(title, htmlMessage);

        log.info("Notification and email sent for {} product: {} by userId: {}", event.getEventType(), product.getTitle(), userId);
    }

    private String buildImagesGallery(Product product) {
        StringBuilder imagesHtml = new StringBuilder();
        imagesHtml.append("<div style='display:flex;gap:10px;overflow-x:auto;padding:10px 0;'>");
        for (String imgUrl : product.getImages()) {
            imagesHtml.append("<div style='flex:0 0 auto;border:1px solid #ddd;border-radius:8px;overflow:hidden;'>")
                    .append("<img src='").append(imgUrl)
                    .append("' style='width:150px;height:150px;object-fit:cover;'/>")
                    .append("</div>");
        }
        imagesHtml.append("</div>");
        return imagesHtml.toString();
    }


    public void sendEmailToAllAdmins(String title, String htmlMessage) {
        List<Admin> admins = adminRepository.findAll();

        for (Admin admin : admins) {
            emailService.sendEmail(admin.getEmail(), title, htmlMessage);
        }
    }
}
