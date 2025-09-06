package org.example.ecommerce.application.service.user.impl;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.user.LoginAttemptService;
import org.example.ecommerce.application.service.user.UserService;
import org.example.ecommerce.domain.model.user.FailedLoginAttempt;
import org.example.ecommerce.domain.model.user.User;
import org.example.ecommerce.domain.model.user.exception.*;
import org.example.ecommerce.domain.model.user.repository.FailedLoginAttemptRepository;
import org.example.ecommerce.domain.model.user.repository.UserRepository;
import org.example.ecommerce.infrastructure.dto.UserChangeUserPWDDto;
import org.example.ecommerce.infrastructure.dto.UserUpdateImageProfile;
import org.example.ecommerce.infrastructure.dto.user.UserProfile;
import org.example.ecommerce.infrastructure.dto.user.UserUpdateDto;
import org.example.ecommerce.infrastructure.mapper.UserMapper;
import org.example.ecommerce.infrastructure.utils.*;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final GetUserContext getUserContext;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final MessageSource messageSource;
    private final JwtUtil jwtUtil;
    private final ImageUploadUtil  imageUploadUtil;
    private final LoginAttemptService loginAttemptService ;
    private final FailedLoginAttemptRepository failedLoginAttemptRepository;

    @Override
    public Optional<UserProfile> findByEmail(String email) {
        return getUserContext.getUser(email, GetUserByType.EMAIL);
    }

    @Override
    public Optional<UserProfile> findById(String id) {
        return getUserContext.getUser(id, GetUserByType.ID);
    }

    @Override
    public Optional<UserProfile> findByPhoneNumber(String phoneNumber) {
        return getUserContext.getUser(phoneNumber, GetUserByType.PHONE);
    }

    @Override
    public Optional<UserProfile> findByJwt(String jwt) {
        return getUserContext.getUser(jwt, GetUserByType.JWT);
    }

    @Override
    public Optional<UserProfile> updateUserByJwt(UserUpdateDto userDTO, String jwt) {
        String email = jwtUtil.extractEmailFromJwt(jwt);
        return updateUser(userDTO, email);
    }

    @Override
    public Optional<UserProfile> updateUserByEmail(UserUpdateDto userDTO, String email) {
        if (userRepository.findByEmail(userDTO.email()).isEmpty()) {
            throw new UserNotFoundException(messageSource.getMessage("user.notfound.email", new Object[]{email}, LocaleContextHolder.getLocale()));
        }
        return updateUser(userDTO, email);
    }

    @Override
    public Optional<UserProfile> updateUserByPhone(UserUpdateDto userDTO, String phoneNumber) {

        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);

        if (user.isEmpty()) {
            throw new UserNotFoundException(
                    messageSource.getMessage(
                            "user.notfound.phone",
                            new Object[]{phoneNumber},
                            LocaleContextHolder.getLocale()
                    )
            );
        }

        String email = user.get().getEmail();

        return updateUser(userDTO, email);
    }


    @Override
    public Optional<UserProfile> updateUserById(UserUpdateDto userDTO, Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException(
                    messageSource.getMessage(
                            "error.user.notfound.id",
                            new Object[]{id.toString()},
                            LocaleContextHolder.getLocale()
                    )
            );
        }

        String email = user.get().getEmail();

        return updateUser(userDTO, email);

    }

    @Override
    public Boolean deleteImageProfile(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return false; // المستخدم غير موجود
        }

        User user = optionalUser.get();
        String imageUrl = user.getImageUrl();

        // حذف الصورة من السيرفر/Storage إذا موجودة
        if (imageUrl != null && !imageUrl.isEmpty()) {
//            imageUploadUtil.deleteImage(imageUrl);
            user.setImageUrl(null); // إزالة رابط الصورة من الكائن
            userRepository.save(user); // تحديث المستخدم
            return true;
        }

        return false;
    }

    @Override
    @Transactional
    public Optional<UserUpdateImageProfile> updateProfileImage(String imageUrl, String email) {
        log.info("Attempting to update user profile image [{}]", email);
        return userRepository.findByEmail(email)
                .map(user -> {
                    log.debug("User found in database: {}", user);

                    log.debug("Updating profile image for user [{}]", email);
                    user.setImageUrl(imageUrl);

                    User savedUser = userRepository.save(user);
                    log.info("User [{}] profile image updated successfully", email);

                    // رجّع DTO فيه البيانات الجديدة
                    return new UserUpdateImageProfile(
                            savedUser.getImageUrl()
                    );
                });
    }

    @Override
    @Transactional
    public Boolean updatePassword(UserChangeUserPWDDto changeUserPWD) {
        String email = changeUserPWD.email();

        // تحقق من الحظر قبل أي محاولة
        if (loginAttemptService.isBlocked(email)) {
            log.warn("User [{}] blocked due to multiple failed attempts", email);
            throw new RuntimeException("Account blocked due to multiple failed attempts. Try again later.");
        }

        // جلب المستخدم
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(
                        messageSource.getMessage("user.not.found", new Object[]{email}, LocaleContextHolder.getLocale())
                ));

        // التحقق من كلمة المرور القديمة
        if (!passwordEncoder.matches(changeUserPWD.password(), user.getPassword())) {
            // تسجيل محاولة فاشلة
            FailedLoginAttempt attempt = new FailedLoginAttempt();
        attempt.setEmail(email);
        attempt.setAttempts(1);
        attempt.setLastAttemptTime(LocalDateTime.now());

        failedLoginAttemptRepository.save(attempt);


            log.warn("Invalid password attempt for user [{}]", email);
            throw new UserNotFoundException(
                    messageSource.getMessage("user.password.invalid", null, LocaleContextHolder.getLocale())
            );
        }

        // إعادة تعيين عدد المحاولات بعد نجاح تسجيل الدخول
        loginAttemptService.resetAttempts(email);

        // تحديث كلمة المرور الجديدة
        String encodedPassword = passwordEncoder.encode(changeUserPWD.newPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);

        log.info("Password updated successfully for user: {}", email);
        return true;
    }



    private Optional<UserProfile> updateUser(UserUpdateDto userDTO, String email) {
        if (null == userDTO) {
            log.warn("UserUpdateDto is null, update aborted");
            return Optional.empty();
        }
            if (userRepository.findByEmail(userDTO.email()).isPresent()) {
                throw new EmailAlreadyExists(messageSource.getMessage("user.exists.email", new Object[]{userDTO.email()}, LocaleContextHolder.getLocale()));
            }



        if (userRepository.existsByPhoneNumber(userDTO.phoneNumber())) {
                throw new PhoneNumberAlreadyExists(messageSource.getMessage("user.exists.phone", new Object[]{userDTO.phoneNumber()}, LocaleContextHolder.getLocale()));
            }




            return userRepository.findByEmail(email)
                    .map(user -> {
                        if (userDTO.email() != null) {
                            if (!IsEmail.isEmail(userDTO.email())) {
                                throw new EmailIsNotValid(messageSource.getMessage("user.email.invalid", new Object[]{userDTO.phoneNumber()}, LocaleContextHolder.getLocale()));
                            }
                            user.setEmail(userDTO.email());
                        }

                        if (userDTO.fullName() != null) {
                            if (!IsFullName.isValid(userDTO.fullName())) {
                                throw new NameIsNotVlild(messageSource.getMessage("user.fullName.size", new Object[]{userDTO.phoneNumber()}, LocaleContextHolder.getLocale()));

                            }
                            user.setFullName(userDTO.fullName());
                        }
                        if (userDTO.pickupAddress() != null) user.setPickupAddress(userDTO.pickupAddress());
                        if (userDTO.phoneNumber() != null) {
                            if (!IsPhoneNumber.isEgyptianPhone(userDTO.phoneNumber())) {
                                throw new PhoneNumberIsNotValid(messageSource.getMessage("user.phone.invalid", new Object[]{userDTO.phoneNumber()}, LocaleContextHolder.getLocale()));

                            }
                            user.setPhoneNumber(userDTO.phoneNumber());
                        }

                        User savedUser = userRepository.save(user);
                        return userMapper.toUserProfile(savedUser);
                    });
        }


}
