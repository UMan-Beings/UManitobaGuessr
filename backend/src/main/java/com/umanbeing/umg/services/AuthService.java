package com.umanbeing.umg.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;

import com.umanbeing.umg.models.User;
import com.umanbeing.umg.repos.UserRepo;
import com.umanbeing.umg.controllers.dto.CreateAccountRequest;
import com.umanbeing.umg.controllers.dto.LoginRequest;
import com.umanbeing.umg.controllers.dto.LoginResponse;
import com.umanbeing.umg.controllers.dto.SignUpResponse;
import com.umanbeing.umg.controllers.mappers.AuthMapper;
import com.umanbeing.umg.domain.Role;
import java.util.Collections;
import java.util.Optional;

@Component
public class AuthService implements UserDetailsService{


    @Autowired private UserRepo userRepo;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtService tokenService;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userRes = userRepo.findByUsername(username);

        if(userRes.isEmpty())
            throw new UsernameNotFoundException("No user found with this username "+username);
        User user = userRes.get();
        return new
                org.springframework.security.core.userdetails.User(
                        username,
                        user.getPassword(),
                        Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_USER")
                        )
        );
    }

    public SignUpResponse registerUser(CreateAccountRequest createAccountRequest) throws IllegalArgumentException {
        String username = createAccountRequest.username();
        String email = createAccountRequest.email();
        String passwordHash = passwordEncoder.encode(createAccountRequest.password());

        if (userRepo.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        User newUser = User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordHash)
                .role(Role.USER)
                .build();
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
                AuthMapper.fromDto(loginRequest)
            );

            // Generate JWT token
            String token = tokenService.generateToken(loginRequest.email());
            return AuthMapper.toDto(token, auth.getName());
        } catch (UsernameNotFoundException e) {
            throw new IllegalArgumentException("Invalid username or password", e);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while trying to login", e);
        }
    }

}
