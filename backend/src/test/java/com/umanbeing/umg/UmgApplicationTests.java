package com.umanbeing.umg;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.umanbeing.umg.controllers.dto.CreateGameRequest;
import com.umanbeing.umg.controllers.dto.MakeGuessRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.jdbc.core.JdbcTemplate;

@Import(TestSecurityConfig.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(scripts = {"classpath:db/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@TestMethodOrder(org.junit.jupiter.api.MethodOrderer.OrderAnnotation.class)
class UmgApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long gameId = 1L; // Replace with actual user ID from authentication context

    @Test
    void contextLoads() {
    }

    @Test
    @WithMockUser
    @org.junit.jupiter.api.Order(1)
    void testDatabaseInitialization() {
        // Verify that the game created in the first test exists in the database with the correct values
        // Return everytthing in round table
        jdbcTemplate.query("SELECT * FROM ROUND", (rs) -> {
            Long roundId = rs.getLong("roundId");
            Long gameId = rs.getLong("gameId");
            Long locationId = rs.getLong("locationId");
            Integer roundNumber = rs.getInt("roundNumber");
            System.out.println("Round ID: " + roundId + ", Game ID: " + gameId + ", Location ID: " + locationId + ", Round Number: " + roundNumber);
        });

        System.out.println("Verifying game data in the database...");
        jdbcTemplate.query("SELECT * FROM GAME", (rs) -> {
            Long gameId = rs.getLong("gameId");
            System.out.println("Game ID: " + gameId);
        });
    }


    @Test
    @WithMockUser
    @org.junit.jupiter.api.Order(2)
    void testCreateNewGame() throws Exception {
        // Create a request object
        CreateGameRequest request = new CreateGameRequest();
        request.setTotalRounds(5);
        request.setMaxTimerSeconds(60);

        // Convert the request object to JSON
        String requestJson = objectMapper.writeValueAsString(request);

        // Perform the POST request and verify the response
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

        this.gameId = objectMapper.readTree(responseContent).get("gameId").asLong();
        //verify that gameid is in database
        Long count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM GAME WHERE \"gameId\" = ?",
            Long.class,
            this.gameId
        );
        assertEquals(1, count, "The game should exist in the database.");
    }

    @Test
    @WithMockUser
    @org.junit.jupiter.api.Order(3)
    void testGetGameById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/games/{gameId}", gameId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.phase").value("GUESS"));
    }

    @Test
    @WithMockUser
    @org.junit.jupiter.api.Order(4)
    void testMakeGuess() throws Exception {
        // Create a request object
        MakeGuessRequest request = new MakeGuessRequest();
        request.setCorX(BigDecimal.valueOf(100));
        request.setCorY(BigDecimal.valueOf(200));

        // Convert the request object to JSON
        String requestJson = objectMapper.writeValueAsString(request);

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
    @WithMockUser
    @org.junit.jupiter.api.Order(5)
    void testRequestNextRound() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/games/{gameId}/next", gameId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.phase").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.round").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalRounds").exists());
    }

    @Test
    @WithMockUser
    @org.junit.jupiter.api.Order(6)
    void testDatabaseContent() {
        // Verify that the game created in the first test exists in the database with the correct values
        // Return everything in round table
        jdbcTemplate.query("SELECT * FROM ROUND", (rs) -> {
            Long roundId = rs.getLong("roundId");
            Long gameId = rs.getLong("gameId");
            Long locationId = rs.getLong("locationId");
            Integer roundNumber = rs.getInt("roundNumber");
            System.out.println("Round ID: " + roundId + ", Game ID: " + gameId + ", Location ID: " + locationId + ", Round Number: " + roundNumber);
        });

        System.out.println("Verifying game data in the database...");
        jdbcTemplate.query("SELECT * FROM GAME", (rs) -> {
            Long gameId = rs.getLong("gameId");
            System.out.println("Game ID: " + gameId);
        });
    }
}
