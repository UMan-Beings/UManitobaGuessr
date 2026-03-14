package com.umanbeing.umg;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.umanbeing.umg.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;

    
    private String secret = "test-secret-for-tests-do-not-use-in-production-change-this-value-before-deploying";

    @BeforeEach
    public void setUp() {
        jwtService = new JwtService();
        try {
            var secretField = JwtService.class.getDeclaredField("secret");
            secretField.setAccessible(true);
            secretField.set(jwtService, secret);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set secret field in JwtService", e);
        }
    }

    @Test
    public void testGenerateToken() {
        String username = "testUser";
        String token = jwtService.generateToken(username);
        assertNotNull(token, "Token should not be null");
    }

    @Test
    public void testValidateTokenAndRetrieveSubject() {
        String username = "testUser";
        String token = jwtService.generateToken(username);
        String retrievedUsername = jwtService.validateTokenAndRetrieveSubject(token);
        assertEquals(username, retrievedUsername, "The retrieved username should match the original");
    }

    @Test
    public void testIsTokenExpired() {
        String username = "testUser";
        String token = jwtService.generateToken(username);
        assertFalse(jwtService.isTokenExpired(token), "Token should not be expired immediately after generation");
    }

    @Test
    public void testRefreshToken() {
        String username = "testUser";
        String token = jwtService.generateToken(username);
        try {
            Thread.sleep(1000); // Sleep for a short time to ensure the new token has a different timestamp
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        String refreshedToken = jwtService.refreshToken(token);
        assertNotNull(refreshedToken, "Refreshed token should not be null");
        assertNotEquals(token, refreshedToken, "Refreshed token should be different from the original");
    }

    @Test
    public void testValidateToken_InvalidToken() {
        String invalidToken = "invalid.token.here";
        assertThrows(JWTVerificationException.class, () -> jwtService.validateTokenAndRetrieveSubject(invalidToken), "Invalid token should throw JWTVerificationException");
    }
}
