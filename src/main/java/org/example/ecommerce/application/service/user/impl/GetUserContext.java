package org.example.ecommerce.application.service.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.infrastructure.dto.user.UserProfile;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
public class GetUserContext {
    private final ApplicationContext applicationContext;

    public Optional<UserProfile> getUser(String input, GetUserByType type) {
        // تحويل نوع الـ enum إلى اسم الـ Bean المسجل
        String beanName = switch (type) {
            case ID -> GetUserByType.ID_VALUE;
            case EMAIL -> GetUserByType.EMAIL_VALUE;
            case PHONE -> GetUserByType.PHONE_VALUE;
            case JWT -> GetUserByType.JWT_VALUE;
        };

        // جلب الاستراتيجية المناسبة
        GetUserStrategy strategy = applicationContext.getBean(beanName, GetUserStrategy.class);

                    return strategy.getUser(input);

//        try {
//            return strategy.getUser(input);
//        } catch (Exception e) {
//            log.error("Error fetching user with type {}: {}", type, e.getMessage());
//            return Optional.empty();
//        }
    }
}
