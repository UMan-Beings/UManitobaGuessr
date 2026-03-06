package com.umanbeing.umg.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "\"User\"")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"userId\"")
    private Long userId;

    @Column(name = "\"username\"", nullable = false, unique = true)
    private String username;

    @Column(name = "\"email\"", nullable = false, unique = true)
    private String email;

    @Column(name = "\"passwordHash\"", nullable = false)
    private String passwordHash;

    @Column(name = "\"profileImageUrl\"")
    private String profileImageUrl;

    @Column(name = "\"totalScore\"", nullable = false)
    @ColumnDefault("0")
    private Long totalScore;

    @Column(name = "\"gamesPlayed\"", nullable = false)
    @ColumnDefault("0")
    private Long gamesPlayed;

    @Column(name = "\"totalGuesses\"", nullable = false)
    @ColumnDefault("0")
    private Long totalGuesses;

    @Column(name = "\"totalTimeSpentGuessing\"", nullable = false)
    @ColumnDefault("0")
    private Long totalTimeSpentGuessing;

    @Column(name = "\"totalCorrectGuesses\"", nullable = false)
    @ColumnDefault("0")
    private Long totalCorrectGuesses;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Game> games = new ArrayList<>();
}