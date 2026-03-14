package com.umanbeing.umg.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.umanbeing.umg.models.User;
import com.umanbeing.umg.repos.UserRepo;
import com.umanbeing.umg.domain.Role;
import java.util.Collections;
import java.util.Optional;

@Component
public class AuthService implements UserDetailsService{


    @Autowired private UserRepo userRepo;

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

    public User registerUser(String username, String email, String passwordHash) throws IllegalArgumentException {
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
        return userRepo.save(newUser);
    }

    public String loginUser(String username, String passwordHash, JwtService jwtService) throws IllegalArgumentException {
        Optional<User> userRes = userRepo.findByUsername(username);
        if (userRes.isEmpty() || !userRes.get().getPasswordHash().equals(passwordHash)) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        return jwtService.generateToken(username);
    }

    


}
