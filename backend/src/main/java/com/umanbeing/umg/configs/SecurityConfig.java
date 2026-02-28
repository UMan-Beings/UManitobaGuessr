package com.umanbeing.umg.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig{
    
    @Bean
    @Profile("!test") // Apply this security configuration to all profiles except "test"
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF protection since our API is stateless
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()); // Allow all requests without authentication
        return http.build();
    }
}
