package com.umanbeing.umg.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roundId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gameId", nullable = false)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locationId", nullable = false)
    private Location location;

    @Column(nullable = false)
    private Integer roundNumber;

    @OneToOne(mappedBy = "round", cascade = CascadeType.ALL, orphanRemoval = true)
    private Guess guess;

}
