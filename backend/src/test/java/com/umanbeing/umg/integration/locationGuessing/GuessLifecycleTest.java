package com.umanbeing.umg.integration.locationGuessing;

import com.fasterxml.jackson.databind.JsonNode;
import com.umanbeing.umg.controllers.dto.CreateGameRequest;
import com.umanbeing.umg.controllers.dto.MakeGuessRequest;
import com.umanbeing.umg.integration.base.PostgresIntegrationTestBase;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class GuessLifecycleTest extends PostgresIntegrationTestBase {

    @Test
    void guessReturns_revealPayload() throws Exception {
        long gameId = createGameForTest(5, 60);

        String requestJson = objectMapper.writeValueAsString(buildValidGuess(30L));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/games/{gameId}/guess", gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.phase").value("REVEAL"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.guessedX").value(100))
                .andExpect(MockMvcResultMatchers.jsonPath("$.guessedY").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.actualX").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.actualY").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.scoreReceived").exists());
    }

    @Test
    void guessPhaseTransition_andPayload() throws Exception {    
        long gameId = createGameForTest(5, 60);

        String guessJson = objectMapper.writeValueAsString(buildValidGuess(20L));
        // Make guess --> transition to reveal phase
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/games/{gameId}/guess", gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(guessJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.phase").value("REVEAL"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.guessedX").value(100))
                .andExpect(MockMvcResultMatchers.jsonPath("$.guessedY").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.actualX").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.actualY").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.scoreReceived").exists());

        // request next round --> guess phase for round 2
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/games/{gameId}/next", gameId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.phase").value("GUESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.round").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalRounds").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timeLimitSeconds").value(60))
                .andExpect(MockMvcResultMatchers.jsonPath("$.score").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.imageUrl").exists())
                // negative assertions (no values that reveal phase would have)
                .andExpect(MockMvcResultMatchers.jsonPath("$.actualX").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.actualY").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.scoreReceived").doesNotExist());
    }

 //--------------
 // HELPERS
 // ------------   
    private long createGameForTest(int totalRounds, int maxTimerSeconds) throws Exception {
        CreateGameRequest request = new CreateGameRequest();
        request.setTotalRounds(totalRounds);
        request.setMaxTimerSeconds(maxTimerSeconds);

        String requestJson = objectMapper.writeValueAsString(request);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.gameId").exists())
                .andReturn();

        JsonNode responseBody = objectMapper.readTree(result.getResponse().getContentAsString());
        return responseBody.get("gameId").asLong();
    }

    private MakeGuessRequest buildValidGuess(long guessTimeSeconds) {
        MakeGuessRequest request = new MakeGuessRequest();
        request.setCorX(BigDecimal.valueOf(100));
        request.setCorY(BigDecimal.valueOf(200));
        request.setGuessTimeSeconds(guessTimeSeconds);
        return request;
    }
}
