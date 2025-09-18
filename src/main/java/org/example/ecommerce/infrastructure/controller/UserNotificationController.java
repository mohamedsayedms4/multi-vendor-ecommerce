package org.example.ecommerce.infrastructure.controller;

import lombok.RequiredArgsConstructor;
import org.example.ecommerce.application.service.user.UserService;
import org.example.ecommerce.domain.model.notification.Notification;
import org.example.ecommerce.domain.model.notification.repository.NotificationRepository;
import org.example.ecommerce.domain.model.user.User;
import org.example.ecommerce.infrastructure.mapper.UserMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/notifications")
@RequiredArgsConstructor
public class UserNotificationController {

    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
//    @PreAuthorize("hasRole('ROLE_USER')")
    public List<Notification> getUnreadNotifications(@RequestHeader("Authorization") String jwt) {
        User user = userService.findByJwt(jwt)
                .map(userMapper::toUser)
                .orElseThrow(() -> new RuntimeException("Invalid JWT token"));
        return notificationRepository.findByUserAndReadFalseOrderByCreatedAtDesc(user);
    }

    @PatchMapping("/{id}/read")
//    @PreAuthorize("hasRole('ROLE_USER')")
    public void markAsRead(@PathVariable Long id,
                           @RequestHeader("Authorization") String jwt) {
        User user = userService.findByJwt(jwt)
                .map(userMapper::toUser)
                .orElseThrow(() -> new RuntimeException("Invalid JWT token"));

        notificationRepository.findByIdAndUser(id, user).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }
}
