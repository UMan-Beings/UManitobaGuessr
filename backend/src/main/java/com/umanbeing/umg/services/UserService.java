package com.umanbeing.umg.services;

import com.umanbeing.umg.models.User;
import com.umanbeing.umg.repos.UserRepo;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public User getUserById(Long userId) {
        return userRepo.findById(userId).orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);
    }

}
