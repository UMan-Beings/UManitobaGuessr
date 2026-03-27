package com.umanbeing.umg.unit;

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

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("TestUser");
        testUser.setEmail("testuser@example.com");
        testUser.setPasswordHash("hashedpassword");
    }

// ======================= Get User by ID Tests =======================
    @Test
    void getUserById_existingUser_returnsUser() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(testUser));
        
        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("TestUser", result.getUsername());
    }

// ======================= Get User by Email Tests =======================
    @Test
    void getUserByEmail_userNotFound_returnsNull() {
        when(userRepo.findByEmail("inValid@example.com")).thenReturn(Optional.empty());

        User result = userService.getUserByEmail("inValid@example.com");
        assertNull(result);
    }

    @Test
    void getUserByEmail_userExists_returnsUser() {
        when(userRepo.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        User result = userService.getUserByEmail(testUser.getEmail());

        assertNotNull(result, "Expected user when email exists");
        assertEquals(testUser.getEmail(), result.getEmail(),"Returned user should match requested email");
    }
}