package org.example.ecommerce.application.service.user;



import org.example.ecommerce.infrastructure.dto.user.UserProfile;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

public interface UserService {

    // for admin
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    Optional<UserProfile> findByEmail(String email);
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    Optional<UserProfile> findById(String id);
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    Optional<UserProfile> findByPhoneNumber(String phoneNumber);

    // for user
    Optional<UserProfile> findByJwt(String jwt);

    // Optional<UserDTO> updateUser(UserDTO userDTO, String email);
    // Optional<UserDTO> updateProfileImage(String imageUrl, String email);
    // void deleteUser(Long id);
    // Boolean updatePassword(ChangeUserPWD changeUserPWD);
}
