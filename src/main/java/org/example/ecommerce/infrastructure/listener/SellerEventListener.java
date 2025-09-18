package org.example.ecommerce.infrastructure.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.EmailService;
import org.example.ecommerce.domain.model.admin.Admin;
import org.example.ecommerce.domain.model.admin.AdminRepository;
import org.example.ecommerce.domain.model.notification.Notification;
import org.example.ecommerce.domain.model.notification.repository.NotificationRepository;
import org.example.ecommerce.domain.model.seller.BusinessDetails;
import org.example.ecommerce.domain.model.seller.Seller;
import org.example.ecommerce.domain.model.seller.repository.SellerRepository;
import org.example.ecommerce.infrastructure.event.NewSellerRegisteredEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SellerEventListener {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final AdminRepository adminRepository;

    @EventListener
    public void handleNewSeller(NewSellerRegisteredEvent event) {
        Seller seller = event.getSeller();
        BusinessDetails details = seller.getBusinessDetails();

        String title = "New Seller Registered";

        String htmlMessage = "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<title>New Seller Notification</title>" +
                "</head>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;'>" +
                "<table width='100%' cellpadding='0' cellspacing='0' style='margin:0; padding:20px;'>" +
                "  <tr>" +
                "    <td>" +
                "      <table width='600' align='center' cellpadding='0' cellspacing='0' style='background-color: #ffffff; border-radius: 10px; padding: 30px; box-shadow: 0 0 10px rgba(0,0,0,0.1);'>" +
                "        <tr>" +
                "          <td style='text-align:center;'>" +
                "            <h2 style='color: #2E86C1;'>🎉 New Seller Registered!</h2>" +
                "          </td>" +
                "        </tr>" +
                "        <tr>" +
                "          <td>" +
                "            <p style='font-size:16px; color: #333;'>A new seller has joined your platform. Details below:</p>" +
                "            <table cellpadding='5' cellspacing='0' style='width:100%; border-collapse: collapse;'>" +
                "              <tr><td><strong>Full Name:</strong></td><td>" + seller.getUser().getFullName() + "</td></tr>" +
                "              <tr><td><strong>Email Verified:</strong></td><td>" + (seller.getIsEmailVerified() ? "Yes" : "No") + "</td></tr>" +
                "              <tr><td><strong>Account Status:</strong></td><td>" + (seller.getAccountStatus() != null ? seller.getAccountStatus() : "N/A") + "</td></tr>" +
                "              <tr><td><strong>Business Name:</strong></td><td>" + details.getBusinessName() + "</td></tr>" +
                "              <tr><td><strong>Business Email:</strong></td><td>" + details.getBusinessEmail() + "</td></tr>" +
                "              <tr><td><strong>Business Mobile:</strong></td><td>" + details.getBusinessMobile() + "</td></tr>" +
                "              <tr><td><strong>Business Address:</strong></td><td>" + details.getBusinessAddress() + "</td></tr>" +
                "            </table>" +
                "          </td>" +
                "        </tr>";

        // إضافة الشعار والبنر لو موجود
        if(details.getLogo() != null && !details.getLogo().isEmpty()) {
            htmlMessage += "<tr><td style='text-align:center; padding-top:15px;'>" +
                    "<img src='" + details.getLogo() + "' alt='Business Logo' style='max-width:150px; height:auto;'/>" +
                    "</td></tr>";
        }

        if(details.getBanner() != null && !details.getBanner().isEmpty()) {
            htmlMessage += "<tr><td style='text-align:center; padding-top:15px;'>" +
                    "<img src='" + details.getBanner() + "' alt='Business Banner' style='max-width:100%; height:auto; border-radius:5px;'/>" +
                    "</td></tr>";
        }

        htmlMessage +=
                "        <tr>" +
                        "          <td style='text-align:center; padding-top:20px;'>" +
                        "            <a href='#' style='display:inline-block; padding:10px 20px; background-color:#2E86C1; color:#ffffff; text-decoration:none; border-radius:5px; font-weight:bold;'>View Seller</a>" +
                        "          </td>" +
                        "        </tr>" +
                        "        <tr>" +
                        "          <td style='padding-top:20px; font-size:12px; color:#888888; text-align:center;'>" +
                        "            This is an automated notification from Your E-commerce System." +
                        "          </td>" +
                        "        </tr>" +
                        "      </table>" +
                        "    </td>" +
                        "  </tr>" +
                        "</table>" +
                        "</body>" +
                        "</html>";

        // حفظ الإشعار في قاعدة البيانات
        Notification notification = Notification.builder()
                .title(title)
                .message("New seller: " + seller.getUser().getFullName())
                .build();
        notificationRepository.save(notification);

        // إرسال الإيميل
        sendEmailToAllAdmins( title,htmlMessage);

        log.info("Admin notified (DB + Email) about new seller: {}", seller.getUser().getFullName());
    }

    public void sendEmailToAllAdmins(String title, String htmlMessage) {
        List<Admin> admins = adminRepository.findAll();

        for (Admin admin : admins) {
            emailService.sendEmail(admin.getEmail(), title, htmlMessage);
        }
    }

    // نفس الطريقة تنطبق على تحديث البيانات أو حذف الايميل
}
