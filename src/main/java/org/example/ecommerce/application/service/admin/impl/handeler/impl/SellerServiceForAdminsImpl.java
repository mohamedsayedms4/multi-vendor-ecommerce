package org.example.ecommerce.application.service.admin.impl.handeler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.admin.impl.handeler.SellerServiceForAdmins;
import org.example.ecommerce.application.service.seller.impl.GetSellerContext;
import org.example.ecommerce.application.service.user.impl.GetUserByType;
import org.example.ecommerce.domain.model.seller.AccountStatus;
import org.example.ecommerce.domain.model.seller.Seller;
import org.example.ecommerce.domain.model.seller.exception.SellerException;
import org.example.ecommerce.domain.model.seller.repository.SellerRepository;
import org.example.ecommerce.domain.model.user.User;
import org.example.ecommerce.infrastructure.dto.seller.SellerProfile;
import org.example.ecommerce.infrastructure.mapper.SellerMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of {@link SellerServiceForAdmins} that provides
 * administrative operations for managing sellers.
 * <p>
 * This service allows admins to search, verify, delete,
 * and update seller account status with security checks and logging.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SellerServiceForAdminsImpl implements SellerServiceForAdmins {

    private final SellerRepository sellerRepository;
    private final SellerMapper sellerMapper;
    private final GetSellerContext getSellerContext;

    /**
     * Retrieves all sellers with pagination and sorting.
     *
     * @param page      page number (0-based index)
     * @param size      number of sellers per page
     * @param sortBy    the field used to sort
     * @param direction the sort direction ("asc" or "desc")
     * @return a page of {@link SellerProfile} objects
     */
    @Override
    public Page<SellerProfile> findAllSellersSorted(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return sellerRepository.findAll(pageable).map(sellerMapper::toSellerProfile);
    }

    /**
     * Finds a seller by their email.
     *
     * @param email seller's email
     * @return optional containing the seller profile if found
     */
    @Override
    public Optional<SellerProfile> findSellerByEmail(String email) {
        return getSellerContext.getSeller(email, GetUserByType.EMAIL);
    }

    /**
     * Finds a seller by their ID.
     *
     * @param id seller's unique identifier
     * @return optional containing the seller profile if found
     */
    @Override
    public Optional<SellerProfile> findSellerById(String id) {
        return getSellerContext.getSeller(id, GetUserByType.ID);
    }

    /**
     * Finds a seller by their phone number.
     *
     * @param phoneNumber seller's phone number
     * @return optional containing the seller profile if found
     */
    @Override
    public Optional<SellerProfile> findBySellerPhoneNumber(String phoneNumber) {
        return getSellerContext.getSeller(phoneNumber, GetUserByType.PHONE);
    }

    /**
     * Verifies a seller's email status.
     *
     * @param id            seller ID
     * @param emailVerified true if email is verified, false otherwise
     * @return true if the update was successful
     * @throws SellerException if seller not found or invalid input
     */
    @Override
    public Boolean verifySeller(Long id, Boolean emailVerified) {
        if (emailVerified == null) {
            log.error("emailVerified cannot be null");
            throw new SellerException("emailVerified cannot be null");
        }

        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Seller not found with id: {}", id);
                    return new SellerException("Seller not found");
                });

        seller.setIsEmailVerified(emailVerified);
        return sellerRepository.save(seller) != null;
    }

    /**
     * Deletes a seller by ID, ensuring that the relationship
     * between {@link Seller} and {@link User} is properly removed.
     *
     * @param id seller ID
     * @throws RuntimeException if seller not found
     */
    @Override
    public void deleteSellerById(Long id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seller not found with id: " + id));

        // unlink seller from user before deletion
        User user = seller.getUser();
        if (user != null) {
            user.setSeller(null);
        }

        sellerRepository.deleteSellerById(id);
    }

    /**
     * Updates a seller's account status (ACTIVE, BANNED, CLOSED, PENDING_VERIFICATION).
     *
     * @param id     seller ID
     * @param status the new account status
     * @return optional containing the updated seller
     * @throws SellerException if seller not found or invalid status
     */
    @Override
    public Optional<Seller> updateSellerAccountStatus(Long id, AccountStatus status) {
        Optional<Seller> sellerOpt = sellerRepository.findById(id);
        if (sellerOpt.isEmpty()) {
            log.error("Seller not found");
            throw new SellerException("Seller not found");
        }
        if (status != AccountStatus.ACTIVE &&
                status != AccountStatus.BANNED &&
                status != AccountStatus.CLOSED &&
                status != AccountStatus.PENDING_VERIFICATION) {

            log.error("Invalid account status: {}", status);
            throw new SellerException("Invalid account status: " + status);
        }

        Seller seller = sellerOpt.get();
        seller.setAccountStatus(status);
        sellerRepository.save(seller);
        return Optional.of(seller);
    }
}
