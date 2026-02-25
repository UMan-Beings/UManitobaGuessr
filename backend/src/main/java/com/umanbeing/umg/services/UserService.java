package com.umanbeing.umg.services;

import org.springframework.stereotype.Service;
import com.umanbeing.umg.models.User;
import com.umanbeing.umg.repos.UserRepo;

@Service
public class UserService {
    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public User getUserById(Long userId) {
        return userRepo.findById(userId).orElse(null);
    }
}
