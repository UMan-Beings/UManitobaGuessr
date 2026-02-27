package com.umanbeing.umg.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "\"ROUND\"")
@Getter
@Setter
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"roundId\"")
    private Long roundId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "\"gameId\"", nullable = false)
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "\"locationId\"", nullable = false)
    private Location location;

    @Column(name = "\"roundNumber\"", nullable = false)
    private Integer roundNumber;

    @OneToOne(mappedBy = "round", cascade = CascadeType.ALL, orphanRemoval = true)
    private Guess guess;

}
