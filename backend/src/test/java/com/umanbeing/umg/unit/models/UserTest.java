package com.umanbeing.umg.unit.models;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.umanbeing.umg.domain.Role;
import com.umanbeing.umg.models.Game;
import com.umanbeing.umg.models.User;

class UserTest {

    @Test
    void userConstructorAndGetters_validInput_returnsExpectedValues() {
        User user = new User(
            1L,
            "testUser",
            "testUser@example.com",
            "testUserPasswordHash",
            "https://example.com/profile.jpg",
            new ArrayList<>(),
            Role.USER
        );

        assertEquals(1L, user.getUserId());
        assertEquals("testUser", user.getUsername());
        assertEquals("testUser@example.com", user.getEmail());
        assertEquals("testUserPasswordHash", user.getPasswordHash());
        assertEquals("https://example.com/profile.jpg", user.getProfileImageUrl());
        assertEquals(Role.USER, user.getRole());
        assertNotNull(user.getGames());
    }

    @Test
    void userNoArgConstructor_noInput_returnsInitializedObject() {
        User user = new User();
        assertNotNull(user);
    }

    @Test
    void userSetters_validInput_updatesFieldsCorrectly() {
        User user = new User();

        user.setUserId(2L);
        user.setUsername("testUser");
        user.setEmail("testUser@example.com");
        user.setPasswordHash("testUserPasswordHash");
        user.setProfileImageUrl("https://example.com/jane.jpg");
        user.setRole(Role.ADMIN);

        assertEquals(2L, user.getUserId());
        assertEquals("testUser", user.getUsername());
        assertEquals("testUser@example.com", user.getEmail());
        assertEquals("testUserPasswordHash", user.getPasswordHash());
        assertEquals("https://example.com/jane.jpg", user.getProfileImageUrl());
        assertEquals(Role.ADMIN, user.getRole());
    }

    @Test
    void getGames_newUser_returnsInitializedEmptyList() {
        User user = new User();
        assertNotNull(user.getGames());
        assertTrue(user.getGames().isEmpty());
    }

    @Test
    void userBuilder_validInput_returnsExpectedValues() {
        List<Game> games = new ArrayList<>();
        User user = User.builder()
            .userId(3L)
            .username("testUser")
            .email("testUser@example.com")
            .passwordHash("testUserPasswordHash")
            .profileImageUrl("https://example.com/builder.jpg")
            .role(Role.USER)
            .games(games)
            .build();

        assertEquals(3L, user.getUserId());
        assertEquals("testUser", user.getUsername());
        assertEquals("testUser@example.com", user.getEmail());
        assertEquals("testUserPasswordHash", user.getPasswordHash());
        assertEquals("https://example.com/builder.jpg", user.getProfileImageUrl());
        assertEquals(Role.USER, user.getRole());
        assertEquals(games, user.getGames());
    }

    @Test
    void getPassword_passwordHashSet_returnsPasswordHash() {
        String passwordHash = "testUserPasswordHash";
        User user = new User();
        user.setPasswordHash(passwordHash);

        assertEquals(passwordHash, user.getPassword());
    }

    @Test
    void getAuthorities_roleNull_returnsEmptyAuthorities() {
        User user = new User();
        user.setRole(null);

        assertTrue(user.getAuthorities().isEmpty());
    }

    @Test
    void getAuthorities_roleSet_returnsSingleAuthority() {
        User user = new User();
        user.setRole(Role.ADMIN);

        assertEquals(1, user.getAuthorities().size());
        assertTrue(user.getAuthorities().contains(Role.ADMIN));
    }

    @Test
    void userDetailsStatusMethods_defaultUser_returnsTrueForAllStatusChecks() {
        User user = new User();

        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }
}
