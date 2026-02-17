package com.umanbeing.umg.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
public class Guess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guessId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roundId", nullable = false, unique = true)
    private Round round;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal guessedLatitude;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal guessedLongitude;

    @Column(nullable = false)
    private Integer guessTimeMs;

    @Column(nullable = false)
    private Integer distanceMeters;

    @Column(nullable = false)
    private Integer score;
}