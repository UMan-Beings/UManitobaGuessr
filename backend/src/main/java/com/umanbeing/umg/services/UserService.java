package com.umanbeing.umg.services;

import com.umanbeing.umg.models.User;
import com.umanbeing.umg.repos.UserRepo;
import org.springframework.stereotype.Service;

/**
 * Service class for managing user-related operations.
 * It is not used for authentication purposes. Only for retrieving user information.
 */
@Service
public class UserService {

    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Retrieves a user by their unique identifier.
     * @param userId The unique identifier of the user.
     * @return The user with the specified ID, or null if not found.
     */
    public User getUserById(Long userId) {
        return userRepo.findById(userId).orElse(null);
    }

    /**
     * Retrieves a user by their email address.
     * @param email The email address of the user.
     * @return The user with the specified email, or null if not found.
     */
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);
    }

}
