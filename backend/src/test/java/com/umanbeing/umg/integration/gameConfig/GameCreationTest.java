package com.umanbeing.umg.integration.gameConfig;

import com.umanbeing.umg.integration.base.PostgresIntegrationTestBase;
import com.umanbeing.umg.controllers.dto.CreateGameRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

class GameCreationTest extends PostgresIntegrationTestBase {

    @Test
    void seededLocationsExist() {
        Long locationCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM \"LOCATION\"",
            Long.class
        );
        assertNotNull(locationCount, "Location count should not be null.");
        assertEquals(true, locationCount > 0, "Seeded locations should exist.");
    }

    @Test
    void createGame_returns_gameId() throws Exception {
        CreateGameRequest request = new CreateGameRequest();
        request.setTotalRounds(5);
        request.setMaxTimerSeconds(60);

        String requestJson = objectMapper.writeValueAsString(request);

        String responseContent = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.gameId").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.phase").value("GUESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalRounds").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timeLimitSeconds").value(60))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long gameId = objectMapper.readTree(responseContent).get("gameId").asLong();

        Long gameCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM \"GAME\" WHERE \"gameId\" = ?",
            Long.class,
            gameId
        );
        assertEquals(1L, gameCount, "The game should exist in the database.");
    }

    @Test
    void getGameById_returnsGuessPhase() throws Exception {
        CreateGameRequest request = new CreateGameRequest();
        request.setTotalRounds(5);
        request.setMaxTimerSeconds(60);
        String requestJson = objectMapper.writeValueAsString(request);

        String responseContent = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.gameId").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();
        long gameId = objectMapper.readTree(responseContent).get("gameId").asLong();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/games/{gameId}", gameId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.phase").value("GUESS"));
    }
}