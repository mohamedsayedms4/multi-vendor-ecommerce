package org.example.ecommerce.infrastructure.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommerce.application.service.admin.AdminService;
import org.example.ecommerce.domain.model.seller.AccountStatus;
import org.example.ecommerce.domain.model.seller.Seller;
import org.example.ecommerce.domain.model.seller.exception.SellerException;
import org.example.ecommerce.infrastructure.dto.seller.SellerProfile;
import org.example.ecommerce.infrastructure.dto.user.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * REST controller that exposes administrative endpoints for managing
 * {@link UserProfile} and {@link SellerProfile}.
 * <p>
 * This controller allows admins to:
 * <ul>
 *     <li>Search for users or sellers by email, phone, or ID</li>
 *     <li>Retrieve paginated lists of users or sellers</li>
 *     <li>Verify sellers' email status</li>
 *     <li>Update sellers' account status</li>
 *     <li>Delete sellers</li>
 * </ul>
 * </p>
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminService adminService;

    /**
     * Get a user profile by email.
     *
     * @param email the email address of the user
     * @return 200 with the user profile if found, otherwise 404
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getUserProfileByEmail(
            @PathVariable
            @NotBlank(message = "{user.email.required}")
            @Email(message = "{user.email.invalid}") String email) {
        Optional<UserProfile> user = adminService.findUserByEmail(email);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Get a user profile by phone number.
     *
     * @param phone the phone number of the user
     * @return 200 with the user profile if found, otherwise 404
     */
    @GetMapping("/phone/{phone}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getUserProfileByPhone(
            @PathVariable
            @NotBlank(message = "{user.phone.required}")
            @Pattern(regexp = "^(\\+20|0)?1[0-9]{9}$", message = "{user.phone.invalid}") String phone) {
        log.info("getUserProfileByPhone({})", phone);
        Optional<UserProfile> user = adminService.findUserByPhoneNumber(phone);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Get a user profile by ID.
     *
     * @param id the numeric ID of the user
     * @return 200 with the user profile if found, otherwise 404
     */
    @GetMapping("/id/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getUserProfileById(
            @NotBlank(message = "{user.id.required}")
            @Pattern(regexp = "^[0-9]+$", message = "{user.id.invalid}")
            @PathVariable String id) {
        Optional<UserProfile> user = adminService.findUserById(id);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Get a paginated list of all users with sorting options.
     *
     * @param page      page number (zero-based)
     * @param size      number of records per page
     * @param sortBy    field name to sort by
     * @param direction sort direction ("asc" or "desc")
     * @return paginated list of users
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<UserProfile>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fullName") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        Page<UserProfile> usersPage = adminService.findAllUsersSorted(page, size, sortBy, direction);
        return ResponseEntity.ok(usersPage);
    }

    /**
     * Get a paginated list of all sellers with sorting options.
     *
     * @param page      page number (zero-based)
     * @param size      number of records per page
     * @param sortBy    field name to sort by
     * @param direction sort direction ("asc" or "desc")
     * @return paginated list of sellers
     */
    @GetMapping("/sellers")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<SellerProfile>> getAllSellers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fullName") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        Page<SellerProfile> sellersPage = adminService.findAllSellersSorted(page, size, sortBy, direction);
        return ResponseEntity.ok(sellersPage);
    }

    /**
     * Search for a seller by email, phone number, or ID.
     *
     * @param email       seller email (optional)
     * @param phoneNumber seller phone number (optional)
     * @param id          seller ID (optional)
     * @return 200 with seller profile if found, otherwise 404
     */
    @GetMapping("/sellers/search")
    public ResponseEntity<SellerProfile> getSellerProfile(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String id) {
        if (email != null) {
            Optional<SellerProfile> seller = adminService.findSellerByEmail(email);
            if (seller.isPresent()) {
                return new ResponseEntity<>(seller.get(), HttpStatus.OK);
            }
        }
        if (phoneNumber != null) {
            Optional<SellerProfile> seller = adminService.findBySellerPhoneNumber(phoneNumber);
            if (seller.isPresent()) {
                return new ResponseEntity<>(seller.get(), HttpStatus.OK);
            }
        }
        if (id != null) {
            Optional<SellerProfile> seller = adminService.findSellerById(id);
            if (seller.isPresent()) {
                return new ResponseEntity<>(seller.get(), HttpStatus.OK);
            }
        }
        throw new SellerException("seller not found");
    }

    /**
     * Verify a seller's email status.
     *
     * @param id            seller ID
     * @param emailVerified true if email should be marked verified
     * @return success or error message
     * @throws SellerException if seller is not found
     */
    @PutMapping("/verified")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> verifySeller(@RequestParam Long id,
                                          @RequestParam Boolean emailVerified) throws SellerException {
        String idLong = String.valueOf(id);
        Optional<SellerProfile> sellerOpt = adminService.findSellerById(idLong);
        if (sellerOpt.isPresent()) {
            Boolean updatedSellerOpt = adminService.verifySeller(id, emailVerified);
            if (updatedSellerOpt) {
                return ResponseEntity.ok("Email is verified");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to update seller verification status");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Seller not found with id: " + id);
        }
    }

    /**
     * Delete a seller by ID.
     *
     * @param id seller ID
     * @return confirmation message if deleted
     * @throws SellerException if seller not found
     */
    @DeleteMapping("/del")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<?> deleteSeller(@RequestParam Long id) throws SellerException {
        Optional<SellerProfile> sellerOpt = adminService.findSellerById(String.valueOf(id));
        if (sellerOpt.isPresent()) {
            adminService.deleteSellerById(id);
            return ResponseEntity.ok(Map.of("message", "Seller deleted successfully"));
        } else {
            throw new SellerException("Seller not found with id: " + id);
        }
    }

    /**
     * Update seller account status (e.g., ACTIVE, SUSPENDED).
     *
     * @param id     seller ID
     * @param status new account status
     * @return the updated seller if successful
     * @throws SellerException if seller not found
     */
    @PutMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateSellerAccountStatus(@RequestParam Long id,
                                                       @RequestParam AccountStatus status) throws SellerException {
        String idLong = String.valueOf(id);
        Optional<SellerProfile> sellerOpt = adminService.findSellerById(idLong);
        if (sellerOpt.isPresent()) {
            Seller updatedSeller = adminService.updateSellerAccountStatus(id, status).orElse(null);
            if (updatedSeller != null) {
                return ResponseEntity.ok(updatedSeller);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to update seller status");
            }
        }
        throw new SellerException("Seller not found with id: " + id);
    }
}
