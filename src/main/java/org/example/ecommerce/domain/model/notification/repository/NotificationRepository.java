package org.example.ecommerce.domain.model.notification.repository;

import org.example.ecommerce.domain.model.notification.Notification;
import org.example.ecommerce.domain.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // للإشعارات غير المقروءة للـ Admin
    List<Notification> findByReadFalseOrderByCreatedAtDesc();

    // للإشعارات غير المقروءة لمستخدم محدد
    List<Notification> findByUserAndReadFalseOrderByCreatedAtDesc(User user);

    // جلب إشعار بالـ id
    Optional<Notification> findById(Long id);

    // جلب إشعار بالـ id للمستخدم الحالي
    Optional<Notification> findByIdAndUser(Long id, User user);

    // حفظ أو تحديث الإشعار (موجودة أصلاً في JpaRepository)
    @Override
    <S extends Notification> S save(S notification);
}
