package org.example.ecommerce.application.service.user;



import org.example.ecommerce.infrastructure.dto.user.UserProfile;

import java.util.Optional;

public interface UserService {

    // for admin
    Optional<UserProfile> findByEmail(String email);

    Optional<UserProfile> findById(String id);

    Optional<UserProfile> findByPhoneNumber(String phoneNumber);

    // for user
    Optional<UserProfile> findByJwt(String jwt);

    // Optional<UserDTO> updateUser(UserDTO userDTO, String email);
    // Optional<UserDTO> updateProfileImage(String imageUrl, String email);
    // void deleteUser(Long id);
    // Boolean updatePassword(ChangeUserPWD changeUserPWD);
}
