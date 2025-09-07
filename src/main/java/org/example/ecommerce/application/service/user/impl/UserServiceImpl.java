package org.example.ecommerce.application.service.user.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.FailedLogin.LoginAttemptService;
import org.example.ecommerce.application.service.user.UserService;
import org.example.ecommerce.domain.common.exception.FailedLoginAttempt;
import org.example.ecommerce.domain.model.user.User;
import org.example.ecommerce.domain.model.user.exception.*;
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

import java.util.Optional;

/**
 * Implementation of {@link UserService} that provides business logic
 * for managing users, including profile updates, password changes,
 * login attempts, and deletion.
 *
 * <p>This service interacts with the {@link UserRepository},
 * {@link LoginAttemptService}, and utility classes to perform
 * validation and persistence.</p>
 */
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
    private final LoginAttemptService loginAttemptService;

    /**
     * Find a user profile by email.
     *
     * @param email the user's email
     * @return an optional containing the user profile if found
     */
    @Override
    public Optional<UserProfile> findByEmail(String email) {
        return getUserContext.getUser(email, GetUserByType.EMAIL);
    }

    /**
     * Find a user profile by ID.
     *
     * @param id the user's ID
     * @return an optional containing the user profile if found
     */
    @Override
    public Optional<UserProfile> findById(String id) {
        return getUserContext.getUser(id, GetUserByType.ID);
    }

    /**
     * Find a user profile by phone number.
     *
     * @param phoneNumber the user's phone number
     * @return an optional containing the user profile if found
     */
    @Override
    public Optional<UserProfile> findByPhoneNumber(String phoneNumber) {
        return getUserContext.getUser(phoneNumber, GetUserByType.PHONE);
    }

    /**
     * Find a user profile using a JWT token.
     *
     * @param jwt the JWT token
     * @return an optional containing the user profile if found
     */
    @Override
    public Optional<UserProfile> findByJwt(String jwt) {
        return getUserContext.getUser(jwt, GetUserByType.JWT);
    }

    /**
     * Update a user profile using a JWT token.
     *
     * @param userDTO the update data
     * @param jwt     the JWT token
     * @return an optional containing the updated user profile
     */
    @Override
    public Optional<UserProfile> updateUserByJwt(UserUpdateDto userDTO, String jwt) {
        String email = jwtUtil.extractEmailFromJwt(jwt);
        return updateUser(userDTO, email);
    }

    /**
     * Update a user profile by email.
     *
     * @param userDTO the update data
     * @param email   the user's email
     * @return an optional containing the updated user profile
     * @throws UserNotFoundException if the user is not found
     */
    @Override
    public Optional<UserProfile> updateUserByEmail(UserUpdateDto userDTO, String email) {
        if (userRepository.findByEmail(userDTO.email()).isEmpty()) {
            throw new UserNotFoundException(
                    messageSource.getMessage("user.notfound.email", new Object[]{email}, LocaleContextHolder.getLocale()));
        }
        return updateUser(userDTO, email);
    }

    /**
     * Update a user profile by phone number.
     *
     * @param userDTO     the update data
     * @param phoneNumber the user's phone number
     * @return an optional containing the updated user profile
     * @throws UserNotFoundException if the user is not found
     */
    @Override
    public Optional<UserProfile> updateUserByPhone(UserUpdateDto userDTO, String phoneNumber) {
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);

        if (user.isEmpty()) {
            throw new UserNotFoundException(
                    messageSource.getMessage("user.notfound.phone", new Object[]{phoneNumber}, LocaleContextHolder.getLocale())
            );
        }

        return updateUser(userDTO, user.get().getEmail());
    }

    /**
     * Update a user profile by ID.
     *
     * @param userDTO the update data
     * @param id      the user's ID
     * @return an optional containing the updated user profile
     * @throws UserNotFoundException if the user is not found
     */
    @Override
    public Optional<UserProfile> updateUserById(UserUpdateDto userDTO, Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException(
                    messageSource.getMessage("error.user.notfound.id", new Object[]{id.toString()}, LocaleContextHolder.getLocale())
            );
        }
        return updateUser(userDTO, user.get().getEmail());
    }

    /**
     * Delete a user's profile image.
     *
     * @param email the user's email
     * @return true if the image was deleted, false otherwise
     */
    @Override
    public Boolean deleteImageProfile(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return false;
        }

        User user = optionalUser.get();
        if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
            user.setImageUrl(null);
            userRepository.save(user);
            return true;
        }

        return false;
    }

    /**
     * Update a user's profile image.
     *
     * @param imageUrl the new image URL
     * @param email    the user's email
     * @return an optional containing the updated profile image DTO
     */
    @Override
    @Transactional
    public Optional<UserUpdateImageProfile> updateProfileImage(String imageUrl, String email) {
        log.info("Attempting to update user profile image [{}]", email);
        return userRepository.findByEmail(email)
                .map(user -> {
                    user.setImageUrl(imageUrl);
                    User savedUser = userRepository.save(user);
                    return new UserUpdateImageProfile(savedUser.getImageUrl());
                });
    }

    /**
     * Update a user's password.
     *
     * @param changeUserPWD DTO containing old and new password
     * @return true if password was updated successfully
     * @throws FailedLoginAttempt if the user is blocked due to too many failed attempts
     * @throws InvalidPWD         if the old password is invalid
     */
    @Override
    @Transactional
    public Boolean updatePassword(UserChangeUserPWDDto changeUserPWD) {
        String email = changeUserPWD.email();

        if (loginAttemptService.isBlocked(email)) {
            throw new FailedLoginAttempt(
                    messageSource.getMessage("block.request", new Object[]{email}, LocaleContextHolder.getLocale()));
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(
                        messageSource.getMessage("user.not.found", new Object[]{email}, LocaleContextHolder.getLocale())));

        if (!passwordEncoder.matches(changeUserPWD.password(), user.getPassword())) {
            throw new InvalidPWD(
                    messageSource.getMessage("user.invalid.password", null, LocaleContextHolder.getLocale()));
        }

        loginAttemptService.resetAttempts(email);

        user.setPassword(passwordEncoder.encode(changeUserPWD.newPassword()));
        userRepository.save(user);

        return true;
    }

    /**
     * Delete a user by ID.
     *
     * @param id the user's ID
     * @throws UserNotFoundException if the user does not exist
     */
    @Override
    public void deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException(
                    messageSource.getMessage("user.not.found", new Object[]{id}, LocaleContextHolder.getLocale()));
        }
        userRepository.deleteById(id);
    }

    /**
     * Get the ID of a user by email.
     *
     * @param email the user's email
     * @return the user's ID
     * @throws UserNotFoundException if the user does not exist
     */
    @Override
    public Long userId(String email) {
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new UserNotFoundException("user.not.found"));
    }

    /**
     * Internal helper method to update user data.
     *
     * @param userDTO the update data
     * @param email   the user's email
     * @return an optional containing the updated user profile
     */
    private Optional<UserProfile> updateUser(UserUpdateDto userDTO, String email) {
        if (userDTO == null) {
            return Optional.empty();
        }

        if (userRepository.findByEmail(userDTO.email()).isPresent()) {
            throw new EmailAlreadyExists(
                    messageSource.getMessage("user.exists.email", new Object[]{userDTO.email()}, LocaleContextHolder.getLocale()));
        }

        if (userRepository.existsByPhoneNumber(userDTO.phoneNumber())) {
            throw new PhoneNumberAlreadyExists(
                    messageSource.getMessage("user.exists.phone", new Object[]{userDTO.phoneNumber()}, LocaleContextHolder.getLocale()));
        }

        return userRepository.findByEmail(email)
                .map(user -> {
                    if (userDTO.email() != null) {
                        if (!IsEmail.isEmail(userDTO.email())) {
                            throw new EmailIsNotValid(
                                    messageSource.getMessage("user.email.invalid", new Object[]{userDTO.phoneNumber()}, LocaleContextHolder.getLocale()));
                        }
                        user.setEmail(userDTO.email());
                    }

                    if (userDTO.fullName() != null) {
                        if (!IsFullName.isValid(userDTO.fullName())) {
                            throw new NameIsNotVlild(
                                    messageSource.getMessage("user.fullName.size", new Object[]{userDTO.phoneNumber()}, LocaleContextHolder.getLocale()));
                        }
                        user.setFullName(userDTO.fullName());
                    }

                    if (userDTO.pickupAddress() != null) {
                        user.setPickupAddress(userDTO.pickupAddress());
                    }

                    if (userDTO.phoneNumber() != null) {
                        if (!IsPhoneNumber.isEgyptianPhone(userDTO.phoneNumber())) {
                            throw new PhoneNumberIsNotValid(
                                    messageSource.getMessage("user.phone.invalid", new Object[]{userDTO.phoneNumber()}, LocaleContextHolder.getLocale()));
                        }
                        user.setPhoneNumber(userDTO.phoneNumber());
                    }

                    User savedUser = userRepository.save(user);
                    return userMapper.toUserProfile(savedUser);
                });
    }
}
