package org.example.ecommerce.infrastructure.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.EmailService;
import org.example.ecommerce.domain.model.admin.Admin;
import org.example.ecommerce.domain.model.admin.AdminRepository;
import org.example.ecommerce.domain.model.category.Category;
import org.example.ecommerce.domain.model.notification.Notification;
import org.example.ecommerce.domain.model.notification.repository.NotificationRepository;
import org.example.ecommerce.infrastructure.event.CategoryEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CategoryEventListener {

    private final AdminRepository adminRepository;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleCategoryEvent(CategoryEvent event) {
        Category category = event.getCategory();
        String userName = event.getPerformedBy();

        String title = switch (event.getEventType()) {
            case CREATED -> "New Category Created";
            case UPDATED -> "Category Updated";
            case DELETED -> "Category Deleted";
        };

        String htmlMessage = generateHtmlMessage(title, category, userName);

        Notification notification = Notification.builder()
                .title(title)
                .message(category.getNameEn() + " by " + userName)
                .build();

        notificationRepository.save(notification);

        // 🔥 إرسال عبر WebSocket
        messagingTemplate.convertAndSend("/topic/notifications", notification);
        sendEmailToAllAdmins(title, htmlMessage);
        // الإيميل زي ما هو
    }

    public void sendEmailToAllAdmins(String title, String htmlMessage) {
        List<Admin> admins = adminRepository.findAll();
        for (Admin admin : admins) {
            emailService.sendEmail(admin.getEmail(), title, htmlMessage);
        }
    }

    private String generateHtmlMessage(String header, Category category, String performedBy) {
        String htmlTemplate = """
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    background-color: #f4f4f4;
                    margin: 0;
                    padding: 0;
                }
                .container {
                    max-width: 600px;
                    margin: 20px auto;
                    background-color: #ffffff;
                    border-radius: 10px;
                    box-shadow: 0 2px 5px rgba(0,0,0,0.1);
                    padding: 20px;
                    box-sizing: border-box;
                }
                .header {
                    background-color: #007bff;
                    color: white;
                    padding: 15px;
                    text-align: center;
                    border-radius: 10px 10px 0 0;
                    font-size: 20px;
                    font-weight: bold;
                }
                .content {
                    margin-top: 15px;
                    line-height: 1.6;
                    color: #333;
                    font-size: 16px;
                }
                .label {
                    font-weight: bold;
                }
                .footer {
                    margin-top: 20px;
                    font-size: 12px;
                    color: #999;
                    text-align: center;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">%s</div>
                <div class="content">
                    <p><span class="label">Performed by:</span> %s</p>
                    <p><span class="label">Category Name (EN):</span> %s</p>
                    <p><span class="label">Category Name (AR):</span> %s</p>
                    <p><span class="label">Category ID:</span> %s</p>
                    <p><span class="label">Level:</span> %d</p>
                </div>
                <div class="footer">
                    &copy; 2025 Ecommerce System. All rights reserved.
                </div>
            </div>
        </body>
        </html>
        """;

        return String.format(htmlTemplate,
                header,
                performedBy,
                category.getNameEn(),
                category.getNameAr(),
                category.getCategoryId(),
                category.getLevel()
        );
    }



}
