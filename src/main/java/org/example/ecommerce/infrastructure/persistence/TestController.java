//package org.example.ecommerce.infrastructure.persistence;
//
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.example.ecommerce.domain.model.user.FailedLoginAttempt;
//import org.example.ecommerce.domain.model.user.repository.FailedLoginAttemptRepository;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.time.LocalDateTime;
//
//@RestController
//@RequestMapping("/test")
//@RequiredArgsConstructor
//public class TestController {
//
//    private final FailedLoginAttemptRepository repository;
//
//    @GetMapping("/save")
//    @Transactional
//    public String testSave() {
//        FailedLoginAttempt attempt = new FailedLoginAttempt();
//        attempt.setEmail("test@example.com");
//        attempt.setAttempts(1);
//        attempt.setLastAttemptTime(LocalDateTime.now());
//
//        FailedLoginAttempt saved = repository.save(attempt);
//        return "Saved with ID: " + saved.getId();
//    }
//}