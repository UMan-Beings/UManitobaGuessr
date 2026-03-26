package com.umanbeing.umg;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.umanbeing.umg.models.User;
import com.umanbeing.umg.repos.UserRepo;
import com.umanbeing.umg.services.UserService;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock private UserRepo userRepo;
    @InjectMocks private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("TestUser");
        testUser.setEmail("testuser@example.com");
        testUser.setPasswordHash("hashedpassword");
    }

    @Test
    void getUserById_existingUser_returnsUser() {
        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("TestUser", result.getUsername());
    }
}