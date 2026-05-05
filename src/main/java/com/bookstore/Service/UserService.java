package com.bookstore.Service;

import com.bookstore.Entity.UserEntity;
import com.bookstore.Enum.PermissionType;
import com.bookstore.POJOs.SafeUserPOJO;
import com.bookstore.POJOs.UserCartPOJO;
import com.bookstore.POJOs.UserPOJO;
import com.bookstore.Repository.CartRepository;
import com.bookstore.Repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;

    public UserService(
            UserRepository userRepository,
            CartRepository cartRepository,
            CartService cartService
    ) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.cartService = cartService;
    }

    public ResponseEntity<String> registerUser(UserPOJO user) {
        try {
            ResponseEntity<String> validationResponse = validateUserForRegister(user);

            if (validationResponse != null) {
                return validationResponse;
            }

            if (userRepository.existsByUsernameIgnoreCase(user.getUsername())) {
                return new ResponseEntity<>(
                        "Username already exists: " + user.getUsername(),
                        HttpStatus.CONFLICT
                );
            }

            if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
                return new ResponseEntity<>(
                        "Email already exists: " + user.getEmail(),
                        HttpStatus.CONFLICT
                );
            }

            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(user.getUsername().trim());
            userEntity.setEmail(user.getEmail().trim());
            userEntity.setPass(user.getPass());
            userEntity.setFullname(user.getFullname());
            userEntity.setPermissions(PermissionType.USER);
            userEntity.setRegular(user.getRegular() != null ? user.getRegular() : false);

            userRepository.save(userEntity);

            return new ResponseEntity<>("Registering user was successful", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(
                    "Registering user failed: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @Transactional
    public ResponseEntity<String> updateUser(Integer id, UserPOJO updatedUser) {
        try {
            UserEntity user = userRepository.findById(id).orElse(null);

            if (user == null) {
                return new ResponseEntity<>(
                        "User not found with ID: " + id,
                        HttpStatus.NOT_FOUND
                );
            }

            ResponseEntity<String> validationResponse = validateUserForUpdate(updatedUser);

            if (validationResponse != null) {
                return validationResponse;
            }

            Optional<UserEntity> userWithSameUsername =
                    userRepository.findByUsernameIgnoreCase(updatedUser.getUsername());

            if (userWithSameUsername.isPresent()
                    && !userWithSameUsername.get().getId().equals(id)) {
                return new ResponseEntity<>(
                        "Another user already has this username: " + updatedUser.getUsername(),
                        HttpStatus.CONFLICT
                );
            }

            Optional<UserEntity> userWithSameEmail =
                    userRepository.findByEmailIgnoreCase(updatedUser.getEmail());

            if (userWithSameEmail.isPresent()
                    && !userWithSameEmail.get().getId().equals(id)) {
                return new ResponseEntity<>(
                        "Another user already has this email: " + updatedUser.getEmail(),
                        HttpStatus.CONFLICT
                );
            }

            user.setUsername(updatedUser.getUsername().trim());
            user.setEmail(updatedUser.getEmail().trim());
            user.setFullname(updatedUser.getFullname());

            if (updatedUser.getPass() != null && !updatedUser.getPass().isBlank()) {
                user.setPass(updatedUser.getPass());
            }

            if (updatedUser.getRegular() != null) {
                user.setRegular(updatedUser.getRegular());
            }

            userRepository.save(user);

            return new ResponseEntity<>("Updating user was successful", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(
                    "Updating user failed: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @Transactional
    public ResponseEntity<String> deleteUser(Integer id) {
        try {
            UserEntity user = userRepository.findById(id).orElse(null);

            if (user == null) {
                return new ResponseEntity<>(
                        "User not found with ID: " + id,
                        HttpStatus.NOT_FOUND
                );
            }

            cartRepository.deleteByIdCartId(id);
            userRepository.delete(user);

            return new ResponseEntity<>("Deleting user was successful", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(
                    "Deleting user failed: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public ResponseEntity<?> getUserById(Integer id) {
        try {
            UserEntity user = userRepository.findById(id).orElse(null);

            if (user == null) {
                return new ResponseEntity<>(
                        "User not found with ID: " + id,
                        HttpStatus.NOT_FOUND
                );
            }

            UserCartPOJO cart = cartService.buildUserCartPOJO(id);

            SafeUserPOJO safeUser = new SafeUserPOJO(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFullname(),
                    user.getPermissions(),
                    user.getRegular(),
                    cart
            );

            return new ResponseEntity<>(safeUser, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(
                    "Getting user failed: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public Integer getUserIdByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found with username: " + username
                ))
                .getId();
    }

    @Transactional
    public ResponseEntity<String> changeUserPermission(
            Integer id,
            PermissionType permission
    ) {
        try {
            if (permission == null) {
                return new ResponseEntity<>(
                        "Permission cannot be empty",
                        HttpStatus.BAD_REQUEST
                );
            }

            UserEntity user = userRepository.findById(id).orElse(null);

            if (user == null) {
                return new ResponseEntity<>(
                        "User not found with ID: " + id,
                        HttpStatus.NOT_FOUND
                );
            }

            user.setPermissions(permission);
            userRepository.save(user);

            return new ResponseEntity<>(
                    "Changing user permission was successful",
                    HttpStatus.OK
            );

        } catch (Exception e) {
            return new ResponseEntity<>(
                    "Changing user permission failed: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private ResponseEntity<String> validateUserForRegister(UserPOJO user) {
        if (user == null) {
            return new ResponseEntity<>("User cannot be null", HttpStatus.BAD_REQUEST);
        }

        if (user.getUsername() == null || user.getUsername().isBlank()) {
            return new ResponseEntity<>("Username cannot be empty", HttpStatus.BAD_REQUEST);
        }

        if (user.getUsername().length() > 10) {
            return new ResponseEntity<>("Username cannot be longer than 10 characters", HttpStatus.BAD_REQUEST);
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            return new ResponseEntity<>("Email cannot be empty", HttpStatus.BAD_REQUEST);
        }

        if (user.getEmail().length() > 250) {
            return new ResponseEntity<>("Email cannot be longer than 250 characters", HttpStatus.BAD_REQUEST);
        }

        if (user.getPass() == null || user.getPass().isBlank()) {
            return new ResponseEntity<>("Password cannot be empty", HttpStatus.BAD_REQUEST);
        }

        if (user.getPass().length() > 20) {
            return new ResponseEntity<>("Password cannot be longer than 20 characters", HttpStatus.BAD_REQUEST);
        }

        return null;
    }

    private ResponseEntity<String> validateUserForUpdate(UserPOJO user) {
        if (user == null) {
            return new ResponseEntity<>("User cannot be null", HttpStatus.BAD_REQUEST);
        }

        if (user.getUsername() == null || user.getUsername().isBlank()) {
            return new ResponseEntity<>("Username cannot be empty", HttpStatus.BAD_REQUEST);
        }

        if (user.getUsername().length() > 10) {
            return new ResponseEntity<>("Username cannot be longer than 10 characters", HttpStatus.BAD_REQUEST);
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            return new ResponseEntity<>("Email cannot be empty", HttpStatus.BAD_REQUEST);
        }

        if (user.getEmail().length() > 250) {
            return new ResponseEntity<>("Email cannot be longer than 250 characters", HttpStatus.BAD_REQUEST);
        }

        if (user.getPass() != null && !user.getPass().isBlank() && user.getPass().length() > 20) {
            return new ResponseEntity<>("Password cannot be longer than 20 characters", HttpStatus.BAD_REQUEST);
        }

        return null;
    }
}
