package com.umanbeing.umg.Models;

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
    private Long guess_id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id", nullable = false, unique = true)
    private Round round;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal guessed_latitude;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal guessed_longitude;

    @Column(nullable = false)
    private Integer guess_time_ms;

    @Column(nullable = false)
    private Integer distance_meters;

    @Column(nullable = false)
    private Integer score;
}