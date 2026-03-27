package com.umanbeing.umg.integration.playerStatistics;

import com.umanbeing.umg.integration.base.PostgresIntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PlayerStatisticsTest extends PostgresIntegrationTestBase {

    @Test
        void getUserStats_noCompletedGames_returnsZeroedStats() throws Exception {
        String token = registerAndLogin("testUser", "testUser@example.com");

        mockMvc.perform(get("/api/v1/users/me/stats")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalScore").value(0))
                .andExpect(jsonPath("$.totalRounds").value(0))
                .andExpect(jsonPath("$.totalGames").value(0))
                .andExpect(jsonPath("$.averageScore").value(0.0))
                .andExpect(jsonPath("$.totalGuessTimeSeconds").value(0))
                .andExpect(jsonPath("$.averageGuessTimeSeconds").value(0.0));
    }

    @Test
        void getUserStats_completedGamesExist_returnsAllStats() throws Exception {
        String email = "testUser@example.com";
        String token = registerAndLogin("testUser", email);

        Long userId = jdbcTemplate.queryForObject(
                "SELECT \"userId\" FROM \"User\" WHERE email = ?",
                Long.class,
                email
        );
        assertNotNull(userId, "Expected userId to exist for newly registered user");

        Long completedGameOneId = jdbcTemplate.queryForObject(
                "INSERT INTO \"GAME\" (\"userId\", \"maxTimerSeconds\", \"totalRounds\", \"isCompleted\", \"gameState\", \"currentRoundNumber\", \"score\") " +
                        "VALUES (?, 60, 2, TRUE, 'FINISHED', 2, 1000) RETURNING \"gameId\"",
                Long.class,
                userId
        );
        Long completedGameTwoId = jdbcTemplate.queryForObject(
                "INSERT INTO \"GAME\" (\"userId\", \"maxTimerSeconds\", \"totalRounds\", \"isCompleted\", \"gameState\", \"currentRoundNumber\", \"score\") " +
                        "VALUES (?, 45, 1, TRUE, 'FINISHED', 1, 500) RETURNING \"gameId\"",
                Long.class,
                userId
        );

        // This unfinished game should be excluded from stats.
        Long inProgressGameId = jdbcTemplate.queryForObject(
                "INSERT INTO \"GAME\" (\"userId\", \"maxTimerSeconds\", \"totalRounds\", \"isCompleted\", \"gameState\", \"currentRoundNumber\", \"score\") " +
                        "VALUES (?, 45, 1, FALSE, 'GUESS', 1, 9999) RETURNING \"gameId\"",
                Long.class,
                userId
        );

        assertNotNull(completedGameOneId);
        assertNotNull(completedGameTwoId);
        assertNotNull(inProgressGameId);

        List<Long> locationIds = jdbcTemplate.queryForList(
                "SELECT \"locationId\" FROM \"LOCATION\" ORDER BY \"locationId\" LIMIT 4",
                Long.class
        );
        assertNotNull(locationIds);

        insertRoundAndGuess(completedGameOneId, 1, locationIds.get(0), 30L, 650);
        insertRoundAndGuess(completedGameOneId, 2, locationIds.get(1), 90L, 350);
        insertRoundAndGuess(completedGameTwoId, 1, locationIds.get(2), 60L, 500);

        // This guess belongs to unfinished game and should not be counted.
        insertRoundAndGuess(inProgressGameId, 1, locationIds.get(3), 1000L, 9999);

        mockMvc.perform(get("/api/v1/users/me/stats")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalScore").value(1500))
                .andExpect(jsonPath("$.totalRounds").value(3))
                .andExpect(jsonPath("$.totalGames").value(2))
                .andExpect(jsonPath("$.averageScore").value(750.0))
                .andExpect(jsonPath("$.totalGuessTimeSeconds").value(180))
                .andExpect(jsonPath("$.averageGuessTimeSeconds").value(60.0));
    }

    @Test
        void getUserStats_withoutAuthentication_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/users/me/stats"))
                .andExpect(status().isUnauthorized());
    }

// =========================== Helper Methods ===========================
    private void insertRoundAndGuess(Long gameId, int roundNumber, Long locationId, Long guessTimeSeconds, int score) {
        Long roundId = jdbcTemplate.queryForObject(
                "INSERT INTO \"ROUND\" (\"gameId\", \"locationId\", \"roundNumber\") VALUES (?, ?, ?) RETURNING \"roundId\"",
                Long.class,
                gameId,
                locationId,
                roundNumber
        );

        assertNotNull(roundId, "Expected roundId from inserted round");

        jdbcTemplate.update(
                "INSERT INTO \"GUESS\" (\"roundId\", \"guessedX\", \"guessedY\", \"guessTimeSeconds\", \"distanceMeters\", \"score\") VALUES (?, 0, 0, ?, 0, ?)",
                roundId,
                guessTimeSeconds,
                score
        );
    }

    private String registerAndLogin(String username, String email) throws Exception {
        String signUpBody = "{\"username\":\"" + username + "\",\"password\":\"password123\",\"email\":\"" + email + "\"}";
        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signUpBody))
                .andExpect(status().isOk());

        String loginBody = "{\"email\":\"" + email + "\",\"password\":\"password123\"}";
        String loginResponse = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(loginResponse).get("token").asText();
    }
}
