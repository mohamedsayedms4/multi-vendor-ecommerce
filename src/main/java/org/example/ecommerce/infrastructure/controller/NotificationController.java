package org.example.ecommerce.infrastructure.controller;

import lombok.RequiredArgsConstructor;
import org.example.ecommerce.domain.model.notification.Notification;
import org.example.ecommerce.domain.model.notification.repository.NotificationRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<Notification> getUnreadNotifications() {
        return notificationRepository.findByReadFalseOrderByCreatedAtDesc();
    }
    @PostMapping("/{id}/read")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void markAsRead(@PathVariable Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

}