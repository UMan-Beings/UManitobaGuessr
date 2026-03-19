package com.umanbeing.umg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import javax.naming.AuthenticationException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;

import com.umanbeing.umg.models.User;
import com.umanbeing.umg.repos.UserRepo;
import com.umanbeing.umg.services.JwtService;
import com.umanbeing.umg.services.AuthService;
import com.umanbeing.umg.controllers.dto.LoginRequest;
import com.umanbeing.umg.controllers.dto.LoginResponse;
import com.umanbeing.umg.controllers.mappers.AuthMapper;

@ExtendWith(MockitoExtension.class)
public class LoginTest {
    @Mock UserRepo userRepo;
    @Mock AuthenticationManager authenticationManager;
    @Mock JwtService jwtService;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks AuthService authService;


    @Test
    public void testLoginSuccess() {
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
    public void testLoginFailure_InvalidPassword() {
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
    public void testLoginFailure_InvalidUser() {
        String email = "test@example.com";
        String password = "password";
        LoginRequest testLoginRequest = new LoginRequest(email, password);

        when(authenticationManager.authenticate(AuthMapper.fromDtoLogin(testLoginRequest))).thenThrow(BadCredentialsException.class);
        
        
        //Later change this to expect a custom exception that we will create for this case instead of BadCredentialsException
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> authService.loginUser(testLoginRequest));

        assertEquals("Error while trying to login", exception.getMessage());
    }



}
