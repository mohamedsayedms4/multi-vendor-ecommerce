package org.example.ecommerce.application.service.seller.impl.handeler.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.seller.impl.handeler.UpdateSellerProfile;
import org.example.ecommerce.domain.model.seller.Seller;
import org.example.ecommerce.domain.model.seller.exception.SellerException;
import org.example.ecommerce.domain.model.seller.repository.SellerRepository;
import org.example.ecommerce.domain.model.user.User;
import org.example.ecommerce.domain.model.user.exception.*;
import org.example.ecommerce.domain.model.user.repository.UserRepository;
import org.example.ecommerce.infrastructure.dto.seller.UpdateSellerDto;
import org.example.ecommerce.infrastructure.utils.IsEmail;
import org.example.ecommerce.infrastructure.utils.IsFullName;
import org.example.ecommerce.infrastructure.utils.IsPhoneNumber;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementation of UpdateSellerProfile handler.
 * Responsible for updating seller profile information with validation.
 */
@Component
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UpdateSellerProfileImpl implements UpdateSellerProfile {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    /**
     * Updates the seller profile with validation checks.
     *
     * @param id the seller ID
     * @param updateSellerDto the DTO containing updated info
     * @return true if update succeeded
     * @throws SellerException if seller not found or validation fails
     */
    @Override
    public Boolean updateSellerProfile(Long id, UpdateSellerDto updateSellerDto) {
        log.info("updateSellerProfile called for sellerId: {}", id);

        if (updateSellerDto == null) {
            log.error("updateSellerDto is null");
            throw new SellerException("updateSellerDto is null");
        }

        // Check email uniqueness
        Optional<Seller> sellerWithEmail = sellerRepository.findByBusinessDetails_BusinessEmail(updateSellerDto.businessEmail());
        Optional<User> userWithEmail = userRepository.findByEmail(updateSellerDto.businessEmail());

        if (sellerWithEmail.isPresent() && !sellerWithEmail.get().getId().equals(id)) {
            log.warn("Email {} already exists for another seller", updateSellerDto.businessEmail());
            throw new EmailAlreadyExists(
                    messageSource.getMessage("user.exists.email",
                            new Object[]{updateSellerDto.businessEmail()},
                            LocaleContextHolder.getLocale()));
        }

        if (userWithEmail.isPresent() && !userWithEmail.get().getId().equals(id)) {
            log.warn("Email {} already exists for another user", updateSellerDto.businessEmail());
            throw new EmailAlreadyExists(
                    messageSource.getMessage("user.exists.email",
                            new Object[]{updateSellerDto.businessEmail()},
                            LocaleContextHolder.getLocale()));
        }

        // Check phone number uniqueness
        Optional<Seller> sellerWithMobile = sellerRepository.findByBusinessDetails_BusinessMobile(updateSellerDto.businessMobile());
        Optional<User> userWithMobile = userRepository.findByPhoneNumber(updateSellerDto.businessMobile());

        if (sellerWithMobile.isPresent() && !sellerWithMobile.get().getId().equals(id)) {
            log.warn("Phone number {} already exists for another seller", updateSellerDto.businessMobile());
            throw new PhoneNumberAlreadyExists(
                    messageSource.getMessage("user.exists.phone",
                            new Object[]{updateSellerDto.businessMobile()},
                            LocaleContextHolder.getLocale()));
        }

        if (userWithMobile.isPresent() && !userWithMobile.get().getId().equals(id)) {
            log.warn("Phone number {} already exists for another user", updateSellerDto.businessMobile());
            throw new PhoneNumberAlreadyExists(
                    messageSource.getMessage("user.exists.phone",
                            new Object[]{updateSellerDto.businessMobile()},
                            LocaleContextHolder.getLocale()));
        }

        // Update seller profile
        return sellerRepository.findById(id).map(seller -> {
            log.info("Updating seller profile for id: {}", id);

            if (updateSellerDto.businessEmail() != null) {
                if (!IsEmail.isEmail(updateSellerDto.businessEmail())) {
                    log.error("Invalid email provided: {}", updateSellerDto.businessEmail());
                    throw new EmailIsNotValid(
                            messageSource.getMessage("user.email.invalid",
                                    new Object[]{updateSellerDto.businessEmail()},
                                    LocaleContextHolder.getLocale()));
                }
                seller.getBusinessDetails().setBusinessEmail(updateSellerDto.businessEmail());
                log.info("Updated email to {}", updateSellerDto.businessEmail());
            }

            if (updateSellerDto.businessMobile() != null) {
                if (!IsPhoneNumber.isEgyptianPhone(updateSellerDto.businessMobile())) {
                    log.error("Invalid phone number provided: {}", updateSellerDto.businessMobile());
                    throw new EmailIsNotValid(
                            messageSource.getMessage("user.phone.invalid",
                                    new Object[]{updateSellerDto.businessMobile()},
                                    LocaleContextHolder.getLocale()));
                }
                seller.getBusinessDetails().setBusinessMobile(updateSellerDto.businessMobile());
                log.info("Updated mobile to {}", updateSellerDto.businessMobile());
            }

            if (updateSellerDto.businessName() != null) {
                if (!IsFullName.isValid(updateSellerDto.businessName())) {
                    log.error("Invalid business name provided: {}", updateSellerDto.businessName());
                    throw new NameIsNotVlild(
                            messageSource.getMessage("user.fullName.size",
                                    new Object[]{updateSellerDto.businessName()},
                                    LocaleContextHolder.getLocale()));
                }
                seller.getBusinessDetails().setBusinessName(updateSellerDto.businessName());
                log.info("Updated business name to {}", updateSellerDto.businessName());
            }

            if (updateSellerDto.businessAddress() != null) {
                seller.getBusinessDetails().setBusinessAddress(updateSellerDto.businessAddress());
                log.info("Updated address to {}", updateSellerDto.businessAddress());
            }

            if (updateSellerDto.logo() != null) {
                seller.getBusinessDetails().setLogo(updateSellerDto.logo());
                log.info("Updated logo");
            }

            if (updateSellerDto.banner() != null) {
                seller.getBusinessDetails().setBanner(updateSellerDto.banner());
                log.info("Updated banner");
            }

            log.info("Updating address, logo, and banner for sellerId: {}", id);
            sellerRepository.save(seller);
            log.info("Seller profile updated successfully for id: {}", id);

            return true;
        }).orElseThrow(() -> {
            log.error("Seller not found with id: {}", id);
            return new SellerException("Seller not found");
        });
    }
}
