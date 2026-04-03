package com.umanbeing.umg.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;

import com.umanbeing.umg.models.User;
import com.umanbeing.umg.repos.UserRepo;
import com.umanbeing.umg.services.JwtService;
import com.umanbeing.umg.services.AuthService;
import com.umanbeing.umg.controllers.dto.CreateAccountRequest;
import com.umanbeing.umg.controllers.dto.LoginRequest;
import com.umanbeing.umg.controllers.dto.LoginResponse;
import com.umanbeing.umg.controllers.dto.SignUpResponse;
import com.umanbeing.umg.controllers.mappers.AuthMapper;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock UserRepo userRepo;
    @Mock AuthenticationManager authenticationManager;
    @Mock JwtService jwtService;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks AuthService authService;

// ====================== Login User Tests ======================
    @Test
    void loginUser_validCredentials_returnsTokenAndUsername() {
        String email = "test@example.com";
        String password = "password";
        String token = "token";
        String username = "testuser";

        LoginRequest testLoginRequest = new LoginRequest(email, password);

        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(email);
        when(authenticationManager.authenticate(AuthMapper.fromDtoLogin(testLoginRequest))).thenReturn(authentication);
        when(jwtService.generateToken(user.getEmail())).thenReturn(token);
        

        LoginResponse result = authService.loginUser(testLoginRequest);

        assertEquals(token, result.token());
        assertEquals(email, result.name());
    }

    @Test
    void loginUser_invalidPassword_throwsBadCredentialsException() {
        String email = "test@example.com";
        String password = "password";
        String username = "testuser";

        LoginRequest testLoginRequest = new LoginRequest(email, password);

        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode("wrongpassword"));

        when(authenticationManager.authenticate(AuthMapper.fromDtoLogin(testLoginRequest))).thenThrow(BadCredentialsException.class);
        
        
        //Later change this to expect a custom exception that we will create for this case instead of BadCredentialsException
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> authService.loginUser(testLoginRequest));

        assertEquals("Error while trying to login", exception.getMessage());
    }

    @Test
    void loginUser_invalidUser_throwsBadCredentialsException() {
        String email = "test@example.com";
        String password = "password";
        LoginRequest testLoginRequest = new LoginRequest(email, password);

        when(authenticationManager.authenticate(AuthMapper.fromDtoLogin(testLoginRequest))).thenThrow(BadCredentialsException.class);
        
        
        //Later change this to expect a custom exception that we will create for this case instead of BadCredentialsException
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> authService.loginUser(testLoginRequest));

        assertEquals("Error while trying to login", exception.getMessage());
    }

// ====================== Regiester User Tests ======================
    @Test
    void registerUser_validCredentials_returnsSuccessMessage() {
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
    void registerUser_emailAlreadyExists_throwsIllegalArgumentException() {
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
    void registerUser_usernameAlreadyExists_throwsIllegalArgumentException() {
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
    void registerUser_usernameNull_throwsNullPointerException() {
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
    void registerUser_emailNull_throwsNullPointerException() {
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
    void registerUser_passwordNull_throwsNullPointerException() {
        String email = "test@example.com";
        String password = null;
        String username = "testuser";

        CreateAccountRequest testCreateAccountRequest = new CreateAccountRequest(username, email, password);
        // Maybe change this later to expect a custom exception that we will create for this case instead of NullPointerException
        assertThrows(NullPointerException.class, () -> {
            authService.registerUser(testCreateAccountRequest);
        });
    }

    @Test
    void registerUser_passwordBelowMinimumLength_throwsIllegalArgumentException() {
        String email = "test@example.com";
        String password = "1234567";
        String username = "testuser";

        CreateAccountRequest testCreateAccountRequest = new CreateAccountRequest(username, email, password);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.registerUser(testCreateAccountRequest);
        });

        assertEquals("Password must be at least 8 characters long", exception.getMessage());
    }

    @Test
    void registerUser_passwordAtMinimumLength_returnsSuccessMessage() {
        String email = "test@example.com";
        String password = "12345678";
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
    void registerUser_passwordAboveMaximumLength_throwsIllegalArgumentException() {
        String email = "test@example.com";
        String password = "123456789012345678901"; // 21 characters
        String username = "testuser";

        CreateAccountRequest testCreateAccountRequest = new CreateAccountRequest(username, email, password);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.registerUser(testCreateAccountRequest);
        });

        assertEquals("Password must be less than 20 characters long", exception.getMessage());
    }

    @Test
    void registerUser_passwordAtMaximumLength_returnsSuccessMessage() {
        String email = "test@example.com";
        String password = "12345678901234567890"; // 20 characters
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
    void registerUser_usernameBelowMinimumLength_throwsIllegalArgumentException() {
        String email = "test@example.com";
        String password = "password";
        String username = "ab";

        CreateAccountRequest testCreateAccountRequest = new CreateAccountRequest(username, email, password);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.registerUser(testCreateAccountRequest);
        });

        assertEquals("Username must be at least 3 characters long", exception.getMessage());
    }

    @Test
    void registerUser_usernameAtMinimumLength_returnsSuccessMessage() {
        String email = "test@example.com";
        String password = "password";
        String username = "abc";

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
    void registerUser_usernameAboveMaximumLength_throwsIllegalArgumentException() {
        String email = "test@example.com";
        String password = "password";
        String username = "123456789012345678901"; // 21 characters

        CreateAccountRequest testCreateAccountRequest = new CreateAccountRequest(username, email, password);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.registerUser(testCreateAccountRequest);
        });

        assertEquals("Username must be less than 20 characters long", exception.getMessage());
    }

    @Test
    void registerUser_usernameAtMaximumLength_returnsSuccessMessage() {
        String email = "test@example.com";
        String password = "password";
        String username = "12345678901234567890"; // 20 characters

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

// ====================== Load User By Username Tests ======================
    @Test
    void loadUserByUsername_userNotFound_throwsUsernameNotFoundException() {
        String email = "test@example.com";
        when(userRepo.findByEmail(email)).thenReturn(java.util.Optional.empty());

        UsernameNotFoundException exception = assertThrows(
            UsernameNotFoundException.class,
            () -> authService.loadUserByUsername(email)
        );

        assertEquals("No user found with this email " + email, exception.getMessage());
    }

    @Test
    void loadUserByUsername_userFound_returnsUserDetails() {
        String email = "test@example.com";
        String password = "password";
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(password);

        when(userRepo.findByEmail(email)).thenReturn(java.util.Optional.of(user));

        var result = authService.loadUserByUsername(email);

        assertEquals(email, result.getUsername());
        assertEquals(password, result.getPassword());
        assertEquals(1, result.getAuthorities().size());
        assertEquals("ROLE_USER", result.getAuthorities().iterator().next().getAuthority());
    }
}