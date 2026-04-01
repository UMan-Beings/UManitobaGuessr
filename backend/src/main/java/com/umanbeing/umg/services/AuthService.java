package com.umanbeing.umg.services;

import com.umanbeing.umg.controllers.dto.CreateAccountRequest;
import com.umanbeing.umg.controllers.dto.LoginRequest;
import com.umanbeing.umg.controllers.dto.LoginResponse;
import com.umanbeing.umg.controllers.dto.SignUpResponse;
import com.umanbeing.umg.controllers.mappers.AuthMapper;
import com.umanbeing.umg.models.User;
import com.umanbeing.umg.repos.UserRepo;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
public class AuthService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService tokenService;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    @NonNull
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        Optional<User> userRes = userRepo.findByEmail(email);
        if (userRes.isEmpty())
            throw new UsernameNotFoundException("No user found with this email " + email);
        User user = userRes.get();
        return new
                org.springframework.security.core.userdetails.User(
                email,
                user.getPassword(),
                Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_USER")
                )
        );
    }

    public SignUpResponse registerUser(CreateAccountRequest createAccountRequest) throws IllegalArgumentException {
        User newUser = AuthMapper.fromDtoSignUp(createAccountRequest);
        // Information in DTO was already checked for null/blank values
        // We still need to check for length, complexity, etc. for password
        if (createAccountRequest.password().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        if (createAccountRequest.password().length() > 20) {
            throw new IllegalArgumentException("Password must be less than 20 characters long");
        }
        if (createAccountRequest.username().length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters long");
        }
        if (createAccountRequest.username().length() > 20) {
            throw new IllegalArgumentException("Username must be less than 20 characters long");
        }
        newUser.setPasswordHash(passwordEncoder.encode(createAccountRequest.password()));
        if (userRepo.findByUsername(newUser.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepo.findByEmail(newUser.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        User savedUser = userRepo.save(newUser);
        return new SignUpResponse(
                "User registered successfully",
                savedUser.getUsername()
        );
    }

    public LoginResponse loginUser(LoginRequest loginRequest) throws IllegalArgumentException {
        try {
            // Authenticate the user
            Authentication auth = authenticationManager.authenticate(
                    AuthMapper.fromDtoLogin(loginRequest)
            );
            // Generate JWT token
            String token = tokenService.generateToken(loginRequest.email());
            return AuthMapper.toDto(token, auth.getName());
        } catch (Exception e) {
            throw new BadCredentialsException("Error while trying to login", e);
        }
    }

}
