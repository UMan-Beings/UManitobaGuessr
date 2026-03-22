package com.umanbeing.umg.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.umanbeing.umg.services.JwtService;

import jakarta.servlet.http.HttpServletResponse;

import com.umanbeing.umg.filters.JwtFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtService jwtService;
    
    @Bean
    public JwtFilter jwtFilter(UserDetailsService userDetailsService, JwtService jwtService) {
        return new JwtFilter(userDetailsService, jwtService);
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF protection since our API is stateless
            
            .authorizeHttpRequests(auth -> auth
                
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/games/**").permitAll()
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )

            // Stateless session (required for JWT)
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Add JWT filter before Spring Security's default filter
            .addFilterBefore(jwtFilter(userDetailsService, jwtService), UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exh -> exh.authenticationEntryPoint(
            (request, response, ex) -> 
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage())
            
        ));
            return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /* 
     * Authentication manager bean
     * Required for programmatic authentication (e.g., in /generateToken)
     */
    @Bean
    public AuthenticationManager authManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        var authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }
    
}
