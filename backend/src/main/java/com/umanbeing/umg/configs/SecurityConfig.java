package com.umanbeing.umg.configs;

import com.umanbeing.umg.filters.JwtFilter;
import com.umanbeing.umg.services.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration class for Spring Security.
 * <p>
 * This class sets up the security configuration for the application, including
 * authentication, authorization, and JWT token handling.
 * </p>
 * <p>
 * UserDetailsService is the source of user information that Spring Security uses to authenticate users.
 * JwtService is responsible for generating and validating JWT tokens.
 * </p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtService jwtService;

    /**
     * Bean definition for JwtFilter.
     * It is a custom filter that get injected into a filter chain before Spring Security's default filters.
     *
     * @param userDetailsService The source of user information for authentication.
     * @param jwtService The JWT service for token handling.
     * @return JwtFilter instance for JWT token validation.
     */
    @Bean
    public JwtFilter jwtFilter(UserDetailsService userDetailsService, JwtService jwtService) {
        return new JwtFilter(userDetailsService, jwtService);
    }

    /**
     * Bean definition for SecurityFilterChain.
     * Configures the filter chain for handling HTTP requests.
     * Every request passes through the chain and gets captured by the first filter that matches.
     *
     * @param http The HttpSecurity instance for configuring the filter chain.
     * @return SecurityFilterChain instance with configured filters.
     * It does not throw exceptions to Spring MVC, thus only customized filter would return exceptions.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF protection since our API is stateless
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/games/**").permitAll()
                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) ->
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
                ))
                // Stateless session (required for JWT)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Add JWT filter before Spring Security's default filter
                .addFilterBefore(jwtFilter(userDetailsService, jwtService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication manager bean
     * It is responsible for authenticating users based on the provided user details service.
     *
     * @return AuthenticationManager instance with a configured authentication provider.
     */
    @Bean
    public AuthenticationManager authManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        var authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }

}
