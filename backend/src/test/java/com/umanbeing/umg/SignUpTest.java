package com.umanbeing.umg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.authentication.AuthenticationManager;

import com.umanbeing.umg.models.User;
import com.umanbeing.umg.repos.UserRepo;
import com.umanbeing.umg.services.JwtService;
import com.umanbeing.umg.services.AuthService;
import com.umanbeing.umg.controllers.dto.CreateAccountRequest;
import com.umanbeing.umg.controllers.dto.SignUpResponse;
//import com.umanbeing.umg.controllers.mappers.AuthMapper;

@ExtendWith(MockitoExtension.class)
public class SignUpTest {
    @Mock UserRepo userRepo;
    @Mock AuthenticationManager authenticationManager;
    @Mock JwtService jwtService;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks AuthService authService;

    @Test
    public void testSignUpSuccess() {
        String email = "test@example.com";
        String password = "password";
        String username = "testuser";

        CreateAccountRequest testCreateAccountRequest = new CreateAccountRequest(username, email, password);

        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPasswordHash("encodedPassword");

        when(userRepo.findByEmail(email)).thenReturn(java.util.Optional.empty());
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        when(userRepo.save(any(User.class))).thenReturn(user);

        SignUpResponse result = authService.registerUser(testCreateAccountRequest);

        assertEquals("User registered successfully", result.message());
        assertEquals(username, result.name());
    }

    @Test
    public void testSignUpFailure_EmailAlreadyExists() {
        String email = "test@example.com";
        String password = "password";
        String username = "testuser";

        CreateAccountRequest testCreateAccountRequest = new CreateAccountRequest(username, email, password);
        User existingUser = new User();
        existingUser.setEmail(email);

        when(userRepo.findByEmail(email)).thenReturn(java.util.Optional.of(existingUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.registerUser(testCreateAccountRequest);
        });
        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    public void testSignUpFailure_UsernameAlreadyExists() {
        String email = "test@example.com";
        String password = "password";
        String username = "testuser";

        CreateAccountRequest testCreateAccountRequest = new CreateAccountRequest(username, email, password);
        User existingUser = new User();
        existingUser.setUsername(username);

        when(userRepo.findByUsername(username)).thenReturn(java.util.Optional.of(existingUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.registerUser(testCreateAccountRequest);
        });
        assertEquals("Username already exists", exception.getMessage());
    }

    @Test
    public void testSignUpFailure_UsernameNull() {
        String email = "test@example.com";
        String password = "password";
        String username = null;

        CreateAccountRequest testCreateAccountRequest = new CreateAccountRequest(username, email, password);
        // Maybe change this later to expect a custom exception that we will create for this case instead of NullPointerException
        assertThrows(NullPointerException.class, () -> {
            authService.registerUser(testCreateAccountRequest);
        });
    }

    @Test
    public void testSignUpFailure_EmailNull() {
        String email = null;
        String password = "password";
        String username = "testuser";

        CreateAccountRequest testCreateAccountRequest = new CreateAccountRequest(username, email, password);
        // Maybe change this later to expect a custom exception that we will create for this case instead of NullPointerException
        assertThrows(NullPointerException.class, () -> {
            authService.registerUser(testCreateAccountRequest);
        });
    }

    @Test
    public void testSignUpFailure_PasswordNull() {
        String email = "test@example.com";
        String password = null;
        String username = "testuser";

        CreateAccountRequest testCreateAccountRequest = new CreateAccountRequest(username, email, password);
        // Maybe change this later to expect a custom exception that we will create for this case instead of NullPointerException
        assertThrows(NullPointerException.class, () -> {
            authService.registerUser(testCreateAccountRequest);
        });
    }



}
