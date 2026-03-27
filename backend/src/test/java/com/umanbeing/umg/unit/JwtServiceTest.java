package com.umanbeing.umg.unit;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.umanbeing.umg.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private String secret = "test-secret-for-tests-do-not-use-in-production-change-this-value-before-deploying";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        try {
            var secretField = JwtService.class.getDeclaredField("secret");
            secretField.setAccessible(true);
            secretField.set(jwtService, secret);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set secret field in JwtService", e);
        }
    }

// ======================= Token Generation Tests =======================
    @Test
    void generateToken_validUsername_returnsNonNullToken() {
        String username = "testUser";
        String token = jwtService.generateToken(username);
        assertNotNull(token, "Token should not be null");
    }
    
    // ======================= Token Validation Tests =======================
    @Test
    void validateTokenAndRetrieveSubject_validToken_returnsCorrectUsername() {
        String username = "testUser";
        String token = jwtService.generateToken(username);
        String retrievedUsername = jwtService.validateTokenAndRetrieveSubject(token);
        assertEquals(username, retrievedUsername, "The retrieved username should match the original");
    }
    
    @Test
    void validateTokenAndRetrieveSubject_invalidToken_throwsJWTVerificationException() {
        String invalidToken = "invalidToken";
        assertThrows(JWTVerificationException.class, 
            () -> jwtService.validateTokenAndRetrieveSubject(invalidToken), "Invalid token should throw JWTVerificationException");
    }

    @Test
    void validateToken_validToken_returnsTrue() {
        String username = "testUser";
        String token = jwtService.generateToken(username);
        assertTrue(jwtService.validateToken(token), "validateToken should return true for a valid token");
    }

    @Test
    void validateToken_invalidToken_returnsFalse() {
        String invalidToken = "invalidToken";
        assertFalse(jwtService.validateToken(invalidToken), "validateToken should return false for an invalid token");
    }

// ======================= Token Expiration Tests =======================
    @Test
    void isTokenExpired_generatedToken_returnsFalse() {
        String username = "testUser";
        String token = jwtService.generateToken(username);
        assertFalse(jwtService.isTokenExpired(token), "Token should not be expired immediately after generation");
    }

    @Test
    void isTokenExpired_futureToken_returnsFalse() {
        JwtService testService = new JwtService() {
            @Override
            protected Date extractExpiration(String token) {
                return new Date(System.currentTimeMillis() + 10000); // 10 seconds in the future
            }
        };

        try {
            var secretField = JwtService.class.getDeclaredField("secret");
            secretField.setAccessible(true);
            secretField.set(testService, secret);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set secret field in JwtService", e);
        }
        assertFalse(testService.isTokenExpired("validToken"),"Token should be valid because expiration is in the future");
    }

    @Test
    void isTokenExpired_pastToken_returnsTrue() {
        JwtService testService = new JwtService() {
            @Override
            protected Date extractExpiration(String token) {
                return new Date(System.currentTimeMillis() - 10000); // 10 seconds in the past
            }
        };

        try {
            var secretField = JwtService.class.getDeclaredField("secret");
            secretField.setAccessible(true);
            secretField.set(testService, secret);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set secret field in JwtService", e);
        }
        assertTrue(testService.isTokenExpired("expiredToken"), "Token should be expired because expiration is in the past");
    }

// ======================= Token Refresh Tests =======================
    @Test
    void refreshToken_existingToken_returnsDifferentToken() {
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
    void refreshToken_existingToken_returnsValidToken() {
        String username = "testUser";
        String token = jwtService.generateToken(username);

        String refreshedToken = jwtService.refreshToken(token);

        assertNotNull(refreshedToken, "Refreshed token should not be null");
        assertEquals(username, jwtService.validateTokenAndRetrieveSubject(refreshedToken),
        "Refreshed token should contain the same username");
    }

}
