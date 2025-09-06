package org.example.ecommerce.application.service.user;



import org.example.ecommerce.infrastructure.dto.UserChangeUserPWDDto;
import org.example.ecommerce.infrastructure.dto.UserUpdateImageProfile;
import org.example.ecommerce.infrastructure.dto.user.UserUpdateDto;
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
    Optional<UserProfile> updateUserByJwt(UserUpdateDto userDTO, String jwt);

//    for admin only

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    Optional<UserProfile> updateUserByEmail(UserUpdateDto userDTO, String email);
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    Optional<UserProfile> updateUserByPhone(UserUpdateDto userDTO, String phoneNumber);
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    Optional<UserProfile> updateUserById(UserUpdateDto userDTO, Long id);

    Boolean deleteImageProfile(String email);
     Optional<UserUpdateImageProfile> updateProfileImage(String imageUrl, String email);
    // void deleteUser(Long id);
     Boolean updatePassword(UserChangeUserPWDDto changeUserPWD);
}
