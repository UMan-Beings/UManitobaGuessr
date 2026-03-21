package com.umanbeing.umg.integration.exception;

import com.umanbeing.umg.controllers.dto.CreateGameRequest;
import com.umanbeing.umg.integration.base.PostgresIntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.security.access.AccessDeniedException;

public class ExceptionHandlerTest extends PostgresIntegrationTestBase{

    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testMethodNotAllowed() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auth/signup"))
                .andExpect(MockMvcResultMatchers.status().isMethodNotAllowed())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("Method Not Allowed"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(""));
    }

    @Test
    void testNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/nonexistent"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("Not Found"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(""));
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid email or password"));

    }

    @Test
    void testInternalServerErrorSignup() throws Exception {
        // Trigger an internal server error by sending a malformed JSON
        String malformedJson = "{\"email\":\"invalid_email\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("Internal Server Error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("An unexpected error occurred"));

    }

    @Test
    void testInternalServerErrorGameCreation() throws Exception {
        // Trigger an internal server error by sending a malformed JSON
        String malformedJson = "{\"totalRounds\":\"100000\",\"maxTimerSeconds\":3.14}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("Internal Server Error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("An unexpected error occurred"));

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").doesNotExist());

    }
}
