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

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    private final AdminService adminService;
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getUserProfileByEmail(@PathVariable
                                                   @NotBlank(message = "{user.email.required}")
                                                   @Email(message = "{user.email.invalid}")
                                                   String email)
    {
        Optional<UserProfile> user = adminService.findUserByEmail(email);
        if (user.isPresent()) {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/phone/{phone}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getUserProfileByPhone(@PathVariable
                                                   @NotBlank(message = "{user.phone.required}")
                                                   @Pattern(regexp = "^(\\+20|0)?1[0-9]{9}$", message = "{user.phone.invalid}")
                                                   String phone)
    {
        log.info("getUserProfileByPhone({})", phone);
        Optional<UserProfile> user = adminService.findUserByPhoneNumber(phone);
        if (user.isPresent()) {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @GetMapping("/id/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getUserProfileById(
            @NotBlank(message = "{user.id.required}")
            @Pattern(regexp = "^[0-9]+$",message = "{user.id.invalid}")
            @PathVariable String id) {
        Optional<UserProfile> user = adminService.findUserById(id);
        if (user.isPresent()) {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<UserProfile>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fullName") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Page<UserProfile> usersPage = adminService.findAllUsersSorted(page, size, sortBy, direction);

        return ResponseEntity.ok(usersPage);
    }

    @GetMapping("/sellers")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<SellerProfile>> getAllSellers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fullName") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Page<SellerProfile> sellersPage = adminService.findAllSellersSorted(page, size, sortBy, direction);

        return ResponseEntity.ok(sellersPage);
    }

    @GetMapping("/sellers/search")
    public ResponseEntity<SellerProfile> getSellerProfile(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String id
    ){
        if (null != email){
            Optional<SellerProfile> seller = adminService.findSellerByEmail(email);
            if (seller.isPresent()) {
                return new ResponseEntity<>(seller.get(), HttpStatus.OK);
            }
        }
        if (null != phoneNumber){
            Optional<SellerProfile> seller = adminService.findBySellerPhoneNumber(phoneNumber);
            if (seller.isPresent()) {
                return new ResponseEntity<>(seller.get(), HttpStatus.OK);
            }
        }
        if (null != id){
            Optional<SellerProfile> seller = adminService.findSellerById(id);
            if (seller.isPresent()) {
                return new ResponseEntity<>(seller.get(), HttpStatus.OK);
            }
        }
       throw new SellerException("seller not found");
    }

    @PutMapping("/verified")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> verifySeller (
            @RequestParam Long id,
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

    @PutMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateSellerAccountStatus(@RequestParam Long id,
                                                       @RequestParam AccountStatus status)
            throws SellerException {
        String idLong = String.valueOf(id);
        Optional<SellerProfile> sellerOpt = adminService.findSellerById(idLong);
        if (sellerOpt.isPresent()) {
            Seller updatedSeller = adminService.updateSellerAccountStatus(id, status).orElse(null);
            if (updatedSeller != null) {
                return ResponseEntity.ok(updatedSeller);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update seller status");
            }
        }
       throw new SellerException("Seller not found with id: " + id);
    }
}
