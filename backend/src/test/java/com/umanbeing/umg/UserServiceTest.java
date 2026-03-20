package com.umanbeing.umg;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import com.umanbeing.umg.models.User;
import com.umanbeing.umg.services.UserService;
import com.umanbeing.umg.repos.UserRepo;

import java.util.Optional;

class UserServiceTest {

    private final UserRepo userRepo = mock(UserRepo.class);
    private final UserService userService = new UserService(userRepo);

    @Test
    void getUserById_existingUser_returnsUser() {
        User user = new User();
        user.setUserId(1L);
        user.setUsername("TestUser");
        user.setEmail("testuser@example.com");
        user.setPasswordHash("hashedpassword");

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("TestUser", result.getUsername());
    }

    @Test
    void getUserById_nonExistingUser_throwsException() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userService.getUserById(1L));

        assertEquals("User not found with id 1", exception.getReason());
        assertEquals(404, exception.getStatusCode().value());
    }
}