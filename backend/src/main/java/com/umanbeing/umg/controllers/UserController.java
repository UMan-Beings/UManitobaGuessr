package com.umanbeing.umg.controllers;

import com.umanbeing.umg.controllers.dto.UserStatsResponse;
import com.umanbeing.umg.models.User;
import com.umanbeing.umg.services.GameService;
import com.umanbeing.umg.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller for user-related operations.
 * <p>
 * Provides endpoints for user-related operations such as retrieving user stats.
 */
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

    /**
     * Retrieves the authenticated user from the authentication object.
     *
     * @param authentication The authentication object containing user information.
     * @return The authenticated user.
     * @throws ResponseStatusException if the user is not authenticated or not found.
     */
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