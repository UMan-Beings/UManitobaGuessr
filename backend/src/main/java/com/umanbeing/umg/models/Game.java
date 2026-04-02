package com.umanbeing.umg.models;

import com.umanbeing.umg.domain.GameState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a game entity with user association, game state tracking, and round management.
 * This class is mapped to the "GAME" table in the database.
 * <p>It has the following attributes:
 * <li>{@link Long} gameId</li>
 * <li>{@link User} user</li>
 * <li>{@link Integer} maxTimerSeconds</li>
 * <li>{@link Integer} totalRounds</li>
 * <li>boolean isCompleted</li>
 * <li>{@link GameState} gameState</li>
 * <li>{@link Integer} currentRoundNumber</li>
 * <li>{@link Integer} score</li>
 * </p>
 */
@Entity
@Table(name = "\"GAME\"")
@Getter
@Setter
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"gameId\"")
    private Long gameId;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "\"userId\"", nullable = true)
    private User user;

    @Column(name = "\"maxTimerSeconds\"", nullable = false)
    private Integer maxTimerSeconds;

    @Column(name = "\"totalRounds\"", nullable = false)
    private Integer totalRounds;

    @Column(name = "\"isCompleted\"", nullable = false)
    private boolean isCompleted;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"gameState\"", nullable = false)
    private GameState gameState;

    @Column(name = "\"currentRoundNumber\"", nullable = false)
    private Integer currentRoundNumber;

    @Column(name = "\"score\"", nullable = false)
    private Integer score; // cant we just sum the guess scores?

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("roundNumber ASC")
    private List<Round> rounds = new ArrayList<>();

    public Round getCurrentRound() {
        return rounds.get(currentRoundNumber - 1);
    }

    public void incrementCurrentRoundNumber() {
        currentRoundNumber++;
    }

    public void addScore(int additionalScore) {
        score += additionalScore;
    }

}