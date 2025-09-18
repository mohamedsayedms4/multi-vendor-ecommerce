package org.example.ecommerce.application.service.admin.impl.handeler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.admin.impl.handeler.UserServiceForAdmins;
import org.example.ecommerce.application.service.user.impl.GetUserByType;
import org.example.ecommerce.application.service.user.impl.GetUserContext;
import org.example.ecommerce.domain.model.user.User;
import org.example.ecommerce.domain.model.user.exception.*;
import org.example.ecommerce.domain.model.user.repository.UserRepository;
import org.example.ecommerce.infrastructure.dto.user.UserProfile;
import org.example.ecommerce.infrastructure.dto.user.UserUpdateDto;
import org.example.ecommerce.infrastructure.mapper.UserMapper;
import org.example.ecommerce.infrastructure.utils.IsEmail;
import org.example.ecommerce.infrastructure.utils.IsFullName;
import org.example.ecommerce.infrastructure.utils.IsPhoneNumber;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of {@link UserServiceForAdmins} that provides
 * administrative operations for managing users.
 * <p>
 * This service allows admins to:
 * - Find users by email, ID, or phone number
 * - Update user details with validation
 * - Retrieve all users with pagination and sorting
 * </p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceForAdminsImpl implements UserServiceForAdmins {

    private final GetUserContext getUserContext;
    private final UserRepository userRepository;
    private final MessageSource messageSource;
    private final UserMapper userMapper;

    /**
     * Find a user by email.
     *
     * @param email user email
     * @return optional containing {@link UserProfile} if found
     */
    @Override
    public Optional<UserProfile> findByEmail(String email) {
        return getUserContext.getUser(email, GetUserByType.EMAIL);
    }

    /**
     * Find a user by ID.
     *
     * @param id user unique ID
     * @return optional containing {@link UserProfile} if found
     */
    @Override
    public Optional<UserProfile> findById(String id) {
        return getUserContext.getUser(id, GetUserByType.ID);
    }

    /**
     * Find a user by phone number.
     *
     * @param phoneNumber user's phone number
     * @return optional containing {@link UserProfile} if found
     */
    @Override
    public Optional<UserProfile> findByPhoneNumber(String phoneNumber) {
        return getUserContext.getUser(phoneNumber, GetUserByType.PHONE);
    }

    /**
     * Update user details using email as the identifier.
     *
     * @param userDTO new user data
     * @param email   existing user email
     * @return optional containing updated {@link UserProfile}
     * @throws UserNotFoundException if no user is found with the given email
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
     * Update user details using phone number as the identifier.
     *
     * @param userDTO     new user data
     * @param phoneNumber existing user phone number
     * @return optional containing updated {@link UserProfile}
     * @throws UserNotFoundException if no user is found with the given phone number
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
     * Update user details using user ID as the identifier.
     *
     * @param userDTO new user data
     * @param id      existing user ID
     * @return optional containing updated {@link UserProfile}
     * @throws UserNotFoundException if no user is found with the given ID
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
     * Internal helper method to update user data with validation.
     *
     * @param userDTO the update data
     * @param email   the user's current email
     * @return an optional containing the updated {@link UserProfile}
     */
    private Optional<UserProfile> updateUser(UserUpdateDto userDTO, String email) {
        if (userDTO == null) {
            return Optional.empty();
        }

        if (userRepository.findByEmail(userDTO.email()).isPresent()) {
            throw new EmailAlreadyExists(
                    messageSource.getMessage("user.exists.email",
                            new Object[]{userDTO.email()},
                            LocaleContextHolder.getLocale()));
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
                                    messageSource.getMessage("user.email.invalid", new Object[]{userDTO.email()}, LocaleContextHolder.getLocale()));
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
                        user.setPickupAddress(userDTO.pickupAddress().getCustomer().getPickupAddress());
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

    /**
     * Retrieves all users with pagination and sorting.
     *
     * @param page      page number (0-based index)
     * @param size      number of users per page
     * @param sortBy    field to sort by
     * @param direction sort direction ("asc" or "desc")
     * @return a page of {@link UserProfile} objects
     */
    public Page<UserProfile> findAllUsersSorted(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findAll(pageable).map(userMapper::toUserProfile);
    }
}
