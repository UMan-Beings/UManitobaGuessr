package com.umanbeing.umg.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a round in a game. This class is mapped to the "ROUND" table in the database.
 *
 * <p>It has the following attributes:
 * <li>{@link Long} roundId
 * <li>{@link Game} game
 * <li>{@link Location} location
 * <li>{@link Integer} roundNumber
 * <li>{@link Guess} guess
 */
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
