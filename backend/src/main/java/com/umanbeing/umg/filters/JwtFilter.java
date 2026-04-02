package com.umanbeing.umg.filters;

import com.umanbeing.umg.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * Filter for JWT authentication.
 * <p>
 * This filter intercepts incoming requests and validates JWT tokens to authenticate users.
 * It is inserted into the filter chain before Spring Security's default filters.
 * <p>
 * The filter extracts the JWT token from the request header, validates it, and sets the authenticated user in the security context.
 * If the validation fails, the filter propagates the exception to the global exception handler.
 * If the request is not authenticated, the filter allows the request to proceed to the next filter in the chain.
 */
public class JwtFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    /**
     * Spring dependency injection for exception resolver.
     * Used to propagate exceptions to the global exception handler.
     */
    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    public JwtFilter(UserDetailsService userDetailsService, JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token;
        String username;
        token = extractTokenFromHeader(request);
        // Validate and set the authentication context
        if (token != null) {
            try {
                username = jwtService.validateTokenAndRetrieveSubject(token);
                // Load user details from the database
                UserDetails userDetails = userDetailsService
                        .loadUserByUsername(username);
                // Create an authentication token with the user's authorities
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                // Set authentication in the security context
                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);

            } catch (Exception e) {
                // Propagate other exceptions
                resolver.resolveException(request, response, null, e);
                return;
            }

        }
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the request header.
     * <p>
     *     The token is expected to be in the format "Bearer &lt;token&gt;"
     * </p>
     * @param request the incoming HTTP request
     * @return the JWT token or null if not found
     */
    private String extractTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

}
