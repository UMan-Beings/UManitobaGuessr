package com.umanbeing.umg;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.umanbeing.umg.controllers.dto.CreateGameRequest;
import com.umanbeing.umg.controllers.dto.MakeGuessRequest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

@SpringBootTest
@AutoConfigureMockMvc
class UmgApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void contextLoads() {
    }

    private long testGameId;

    @Disabled("Database is not filled yet")
    @Test
    void testCreateNewGame() throws Exception {
        // Create a request object
        CreateGameRequest request = new CreateGameRequest();
        request.setTotalRounds(5);
        request.setMaxTimerSeconds(60);

        // Convert the request object to JSON
        String requestJson = objectMapper.writeValueAsString(request);

        // Perform the POST request and verify the response
        mockMvc.perform(post("/api/v1/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").exists())
                .andExpect(jsonPath("$.phase").value("GUESS"))
                .andExpect(jsonPath("$.totalRounds").value(5))
                .andExpect(jsonPath("$.timeLimitSeconds").value(60));

        // Extract the gameId from the response for use in other tests
        String responseJson = mockMvc.perform(post("/api/v1/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andReturn()
                .getResponse()
                .getContentAsString();
        testGameId = objectMapper.readTree(responseJson).get("gameId").asLong();
    }

    @Disabled("Database is not filled yet")
    @Test
    void testGetGameById() throws Exception {
        // Assuming a game with ID 1 exists in the database
        long gameId = 1;

        mockMvc.perform(get("/api/v1/games/{gameId}", gameId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phase").exists());
    }

    @Disabled("Database is not filled yet")
    @Test
    void testMakeGuess() throws Exception {
        // Check if testGameId is set, if not, throw exception
        if (testGameId == 0) {
            throw new IllegalStateException("testGameId is not set. Ensure that testCreateNewGame runs before this test.");
        }

        // Create a request object
        MakeGuessRequest request = new MakeGuessRequest();
        request.setCorX(BigDecimal.valueOf(100));
        request.setCorY(BigDecimal.valueOf(200));

        // Convert the request object to JSON
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/games/{gameId}/guess", testGameId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phase").value("REVEAL"))
                .andExpect(jsonPath("$.guessedX").value(100))
                .andExpect(jsonPath("$.guessedY").value(200))
                .andExpect(jsonPath("$.actualX").exists())
                .andExpect(jsonPath("$.actualY").exists())
                .andExpect(jsonPath("$.scoreReceived").exists());
    }

    @Disabled("Database is not filled yet")
    @Test
    void testRequestNextRound() throws Exception {
        // Check if testGameId is set, if not, throw exception
        if (testGameId == 0) {
            throw new IllegalStateException("testGameId is not set. Ensure that testCreateNewGame runs before this test.");
        }

        mockMvc.perform(post("/api/v1/games/{gameId}/next", testGameId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phase").exists())
                .andExpect(jsonPath("$.round").exists())
                .andExpect(jsonPath("$.totalRounds").exists());
    }
}
