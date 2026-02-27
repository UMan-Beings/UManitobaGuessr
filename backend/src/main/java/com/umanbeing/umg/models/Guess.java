package com.umanbeing.umg.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "\"GUESS\"")
@Getter
@Setter
public class Guess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guessId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"roundId\"", nullable = false, unique = true)
    private Round round;

    @Column(name = "\"guessedX\"", nullable = false, precision = 9, scale = 6)
    private BigDecimal guessedX;

    @Column(name = "\"guessedY\"", nullable = false, precision = 9, scale = 6)
    private BigDecimal guessedY;

    @Column(name = "\"guessTimeSeconds\"", nullable = false)
    private Long guessTimeSeconds;

    @Column(name = "\"distanceMeters\"", nullable = false)
    private Integer distanceMeters;

    @Column(name = "\"score\"", nullable = false)
    private Integer score;
}