package org.example.ecommerce.application.service.user;

public interface LoginAttemptService {
    void loginFailed(String email);           // تسجل محاولة فاشلة
    boolean isBlocked(String email);          // تتحقق إذا المستخدم محظور
    void resetAttempts(String email);


}
