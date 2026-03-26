package com.umanbeing.umg.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.umanbeing.umg.controllers.dto.UserStatsResponse;
import com.umanbeing.umg.models.User;
import com.umanbeing.umg.services.GameService;
import com.umanbeing.umg.services.UserService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {
    private final GameService gameService;
    private final UserService userService;

    @GetMapping("/me/stats")
    public ResponseEntity<UserStatsResponse> getUserStats(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        UserStatsResponse response = gameService.getUserStats(user.getUserId());
        return ResponseEntity.ok(response);
    }

    private User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        User user = userService.getUserByEmail(authentication.getName());
        
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }

        return user;
    }
}