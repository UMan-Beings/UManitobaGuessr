package com.umanbeing.umg.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@Getter
@Setter
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private Integer maxTimerSeconds;

    @Column(nullable = false)
    private Integer totalRounds;

    @Column(nullable = false)
    private boolean isCompleted;

    @Column(nullable = false)
    private String gameState;

    @Column(nullable = false)
    private Integer currentRoundNumber;

    @Column(nullable = false)
    private Integer score;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("roundNumber ASC")
    private List<Round> rounds = new ArrayList<>();
}