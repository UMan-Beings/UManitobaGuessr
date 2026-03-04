package com.umanbeing.umg.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

import com.domain.GameState;

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
}