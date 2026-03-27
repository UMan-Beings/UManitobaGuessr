package com.umanbeing.umg.integration.authentication;

import com.umanbeing.umg.controllers.dto.CreateAccountRequest;
import com.umanbeing.umg.integration.base.PostgresIntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.umanbeing.umg.models.User;
import com.umanbeing.umg.repos.UserRepo;
import com.umanbeing.umg.services.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

class AuthTest extends PostgresIntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AuthService authService;

    @Test
    void testSuccessfulSignup() throws Exception {
        String requestJson = "{\"username\":\"newuser2\",\"password\":\"newpassword123\",\"email\":\"newuser2@example.com\"}";

        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    @Test
    void testSuccessfulLogin() throws Exception {

        String requestJson = "{\"username\":\"newuser1\",\"password\":\"newpassword123\",\"email\":\"newuser1@example.com\"}";
        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"));

        User user = userRepo.findByUsername("newuser1").orElseThrow(() -> new AssertionError("User not found in database"));
        assertEquals("newuser1@example.com", user.getEmail());

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"password\":\"newpassword123\",\"email\":\"newuser1@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void testFailedLogin() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"newuser@example.com\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registerUser_passwordTooShort_throwsIllegalArgumentException() {
        CreateAccountRequest req = new CreateAccountRequest("valid_user_short_pwd", "valid_user_short_pwd@example.com", "short");
        assertThrows(IllegalArgumentException.class, () -> authService.registerUser(req));
    }

    @Test
    void registerUser_passwordTooLong_throwsIllegalArgumentException() {
        CreateAccountRequest req = new CreateAccountRequest("valid_user_long_pwd", "valid_user_long_pwd@example.com", "ThisPasswordIsWayTooLong123");
        assertThrows(IllegalArgumentException.class, () -> authService.registerUser(req));
    }

    @Test
    void registerUser_usernameTooShort_throwsIllegalArgumentException() {
        CreateAccountRequest req = new CreateAccountRequest("ab", "short_username@example.com", "ValidPass123");
        assertThrows(IllegalArgumentException.class, () -> authService.registerUser(req));
    }

    @Test
    void registerUser_usernameTooLong_throwsIllegalArgumentException() {
        CreateAccountRequest req = new CreateAccountRequest("ThisUsernameIsWayTooLong", "long_username@example.com", "ValidPass123");
        assertThrows(IllegalArgumentException.class, () -> authService.registerUser(req));
    }

}