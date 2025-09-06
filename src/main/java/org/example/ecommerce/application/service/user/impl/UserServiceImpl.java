package org.example.ecommerce.application.service.user.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.user.UserService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final GetUserContext getUserContext ;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final MessageSource messageSource;
    @Override
    public Optional<UserProfile> findByEmail(String email) {
        return getUserContext.getUser(email,GetUserByType.EMAIL);
    }

    @Override
    public Optional<UserProfile> findById(String id) {
        return getUserContext.getUser(id,GetUserByType.ID);
    }

    @Override
    public Optional<UserProfile> findByPhoneNumber(String phoneNumber) {
        return getUserContext.getUser(phoneNumber,GetUserByType.PHONE);
    }

    @Override
    public Optional<UserProfile> findByJwt(String jwt) {
        return getUserContext.getUser(jwt,GetUserByType.JWT);
    }

    @Override
    public Optional<UserProfile> updateUser(UserUpdateDto userDTO, String email) {

        if (null == userDTO) {
            log.warn("UserUpdateDto is null, update aborted");
            return Optional.empty();
        }


        if (userRepository.findByEmail(userDTO.email()).isPresent()) {
            throw new EmailAlreadyExists(messageSource.getMessage("user.exists.email",new Object[]{userDTO.email()}, LocaleContextHolder.getLocale()));
        }

        if (userRepository.existsByPhoneNumber(userDTO.phoneNumber())) {
            throw new PhoneNumberAlreadyExists(messageSource.getMessage("user.exists.phone",new Object[]{userDTO.phoneNumber()}, LocaleContextHolder.getLocale()));
        }


        return userRepository.findByEmail(email)
                .map(user -> {
                    if (userDTO.email() != null){
                        if (!IsEmail.isEmail(userDTO.email())) {
                            throw new EmailIsNotValid(messageSource.getMessage("user.email.invalid",new Object[]{userDTO.phoneNumber()}, LocaleContextHolder.getLocale()));
                        }
                        user.setEmail(userDTO.email());
                    }

                    if (userDTO.fullName() != null){
                        if (!IsFullName.isValid(userDTO.fullName())) {
                            throw new NameIsNotVlild(messageSource.getMessage("user.fullName.size",new Object[]{userDTO.phoneNumber()}, LocaleContextHolder.getLocale()));

                        }
                        user.setFullName(userDTO.fullName());
                    }
                    if (userDTO.pickupAddress() != null) user.setPickupAddress(userDTO.pickupAddress());
                    if (userDTO.phoneNumber() != null) {
                        if (!IsPhoneNumber.isEgyptianPhone(userDTO.phoneNumber())) {
                            throw new PhoneNumberIsNotValid(messageSource.getMessage("user.phone.invalid",new Object[]{userDTO.phoneNumber()}, LocaleContextHolder.getLocale()));

                        }
                        user.setPhoneNumber(userDTO.phoneNumber());
                    }

                    User savedUser = userRepository.save(user);
                    return userMapper.toUserProfile(savedUser);
                });


    }
}
