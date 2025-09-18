package org.example.ecommerce.infrastructure.repository;

import org.example.ecommerce.domain.model.notification.Notification;
import org.example.ecommerce.domain.model.notification.repository.NotificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaNotificationRepository extends JpaRepository<Notification, Long>, NotificationRepository {

    // Spring Data JPA هتطبقها تلقائي
    List<Notification> findByReadFalseOrderByCreatedAtDesc();

    @Override
    Optional<Notification> findById(Long id);

    @Override
    Notification save(Notification notification);
}
