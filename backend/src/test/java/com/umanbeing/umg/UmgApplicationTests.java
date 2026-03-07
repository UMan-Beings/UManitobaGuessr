package com.umanbeing.umg;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.umanbeing.umg.controllers.dto.CreateGameRequest;
import com.umanbeing.umg.controllers.dto.MakeGuessRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;

@Import(TestSecurityConfig.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(scripts = {"classpath:db/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@TestMethodOrder(org.junit.jupiter.api.MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UmgApplicationTests {

    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine")
            .withDatabaseName("umg_test")
            .withUsername("test_user")
            .withPassword("mypassword");

    static {
        postgres.start();
    }

    @DynamicPropertySource
    static void registerDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long gameId;

    @Test
    void contextLoads() {
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    void testDatabaseInitialization() {
        Long locationCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM \"LOCATION\"",
            Long.class
        );
        assertNotNull(locationCount, "Location count should not be null.");
        assertEquals(true, locationCount > 0, "Seeded locations should exist.");
    }


    @Test
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
        assertNotNull(this.gameId, "A created game should return a non-null gameId.");

        // Verify that gameId is in database
        Long count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM \"GAME\" WHERE \"gameId\" = ?",
            Long.class,
            this.gameId
        );
        assertEquals(1, count, "The game should exist in the database.");
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    void testGetGameById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/games/{gameId}", gameId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.phase").value("GUESS"));
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    void testMakeGuess() throws Exception {
        // Create a request object
        MakeGuessRequest request = new MakeGuessRequest();
        request.setCorX(BigDecimal.valueOf(100));
        request.setCorY(BigDecimal.valueOf(200));
        request.setGuessTimeSeconds(30L);

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
    @org.junit.jupiter.api.Order(5)
    void testMakeGuessTwiceReturnsConflict() throws Exception {
        MakeGuessRequest request = new MakeGuessRequest();
        request.setCorX(BigDecimal.valueOf(100));
        request.setCorY(BigDecimal.valueOf(200));
        request.setGuessTimeSeconds(25L);

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/games/{gameId}/guess", gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    @org.junit.jupiter.api.Order(6)
    void testRequestNextRound() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/games/{gameId}/next", gameId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.phase").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.round").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalRounds").exists());
    }

    @Test
    @org.junit.jupiter.api.Order(7)
    void testRequestNextRoundWithoutRevealReturnsConflict() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/games/{gameId}/next", gameId))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    @org.junit.jupiter.api.Order(8)
    void testGetNonexistentGameReturnsNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/games/{gameId}", Long.MAX_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @org.junit.jupiter.api.Order(9)
    void testMakeGuessWithMissingCoordinatesReturnsBadRequest() throws Exception {
        MakeGuessRequest request = new MakeGuessRequest();
        request.setCorX(null);
        request.setCorY(BigDecimal.valueOf(200));
        request.setGuessTimeSeconds(10L);

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/games/{gameId}/guess", gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @org.junit.jupiter.api.Order(10)
    void testDatabaseContent() {
        Long gameCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM \"GAME\" WHERE \"gameId\" = ?",
            Long.class,
            gameId
        );
        assertEquals(1, gameCount, "Created game should still exist in the database.");

        Long roundCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM \"ROUND\" WHERE \"gameId\" = ?",
            Long.class,
            gameId
        );
        assertNotNull(roundCount, "Round count should not be null.");
        assertEquals(true, roundCount > 0, "Created game should have at least one round.");
    }
}
