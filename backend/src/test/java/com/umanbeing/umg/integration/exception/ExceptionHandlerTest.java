package com.umanbeing.umg.integration.exception;

import com.umanbeing.umg.controllers.dto.CreateGameRequest;
import com.umanbeing.umg.integration.base.PostgresIntegrationTestBase;
import com.umanbeing.umg.repos.UserRepo;
import com.umanbeing.umg.models.User;

import org.junit.jupiter.api.Test;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.beans.factory.annotation.Value;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.Date;

public class ExceptionHandlerTest extends PostgresIntegrationTestBase{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepo;

    @Value("${JWT_SECRET}")
    private String secret;
    
    @Test
    void testMethodNotAllowed() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auth/signup"))
                .andExpect(MockMvcResultMatchers.status().isMethodNotAllowed())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("Method Not Allowed"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(""));
    }


    // Spring Security will unathorize access to protected endpoints without a valid token, so we expect a 401 Unauthorized status instead of 403 Forbidden
    @Test
    void testNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/nonexistent"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void testNotFoundAfterLogin() throws Exception {
        // First, perform a successful login to get a valid token
        String requestJson = "{\"username\":\"newuser1\",\"password\":\"newpassword123\",\"email\":\"newuser1@example.com\"}";
        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"));

        User user = userRepo.findByUsername("newuser1").orElseThrow(() -> new AssertionError("User not found in database"));
        assertEquals("newuser1@example.com", user.getEmail());

        // Store only the token field from the login response
        String token = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"password\":\"newpassword123\",\"email\":\"newuser1@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract the token field from the JSON response
        token = objectMapper.readTree(token).get("token").asText();

        // Access a non-existing API endpoint with the token
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/nonexistent")
                .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("Not Found"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
    }


    @Test
    void testInternalServerErrorLogin() throws Exception {
        // Trigger an internal server error by sending a malformed JSON
        String malformedJson = "{\"email\":\"invalid_email\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("Unauthorized"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Error while trying to login"));

    }

    @Test
    void testBadRequestSignupBadEmail() throws Exception {
        // Trigger a bad request error by sending an incorrect email format
        String malformedJson = "{\"email\":\"invalid_email\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Argument validation failed"));

    }

    @Test
    void testBadRequestSignupMissingFields() throws Exception {
        // Trigger a bad request error by sending a request with missing fields
        String malformedJson = "{\"email\":\"agoodemail@example.com\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Argument validation failed"));

    }

    @Test
    void testInternalServerErrorGameCreation() throws Exception {
        // Trigger an internal server error by sending a JSON with wrong data types
        String malformedJson = "{\"totalRounds\":\"10\",\"maxTimerSeconds\":\"abcd\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Malformed JSON request"));

    }

    @Test
    void testBadRequestGameCreation() throws Exception {
        // Trigger a bad request error by sending an invalid game setting
        CreateGameRequest request = new CreateGameRequest();
        request.setTotalRounds(0); // Invalid total rounds
        request.setMaxTimerSeconds(60);

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Total rounds must be greater than 0"));

    }

    @Test
    void testBadRequestGetGameById() throws Exception {
        // Trigger a bad request error by sending a non-numeric game ID
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/games/invalid_id"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Wrong data type provided in request"));

    }

    @Test
    void testNotReadable() throws Exception {
        // Trigger a not readable error by sending malformed JSON
        String malformedJson = "{\"totalRounds\":5, \"maxTimerSeconds\":30"; // Missing closing brace
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Malformed JSON request"));

    }

    @Test
    void testUsernameAlreadyExists() throws Exception {
        // First, create a user
        String requestJson = "{\"username\":\"existinguser\",\"password\":\"password123\",\"email\":\"existinguser@example.com\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Try to create the same user again
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Username already exists"));
    }

    @Test
    void testUnauthorizedAccess() throws Exception {
        // Try to access a protected endpoint without authentication
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/me/stats"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void testExpiredToken() throws Exception {
        // Build a token that is already expired
        String expiredToken = JWT.create()
                .withSubject("User Details")
                .withClaim("username", "testuser")
                .withIssuedAt(new Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000)) // Issued 2 hours ago
                .withExpiresAt(new Date(System.currentTimeMillis() - 60 * 60 * 1000)) // Expired 1 hour ago
                .sign(Algorithm.HMAC256(secret));
        
        String requestJson = "{\"username\":\"testuser\",\"password\":\"newpassword123\",\"email\":\"newuser1@example.com\"}";
        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"));

        User user = userRepo.findByUsername("testuser").orElseThrow(() -> new AssertionError("User not found in database"));
        assertEquals("newuser1@example.com", user.getEmail());

        String realToken = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"password\":\"newpassword123\",\"email\":\"newuser1@example.com\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        realToken = objectMapper.readTree(realToken).get("token").asText();

        
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/me/stats")
                .header("Authorization", "Bearer " + realToken))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Access a protected endpoint with the expired token
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/me/stats")
                .header("Authorization", "Bearer " + expiredToken))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("JWT token has expired"));
    }
}
