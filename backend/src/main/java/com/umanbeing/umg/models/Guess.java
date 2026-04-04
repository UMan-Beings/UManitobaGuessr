package com.umanbeing.umg.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a guess made by a user in a round. This class is mapped to the "GUESS" table in the
 * database.
 *
 * <p>It has the following attributes:
 * <li>{@link Long} guessId
 * <li>{@link Round} round
 * <li>{@link BigDecimal} guessedX
 * <li>{@link BigDecimal} guessedY
 * <li>{@link Long} guessTimeSeconds
 * <li>{@link Integer} distanceMeters
 * <li>{@link Integer} score
 */
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

  @Column(name = "\"guessedX\"", nullable = true, precision = 17, scale = 13)
  private BigDecimal guessedX;

  @Column(name = "\"guessedY\"", nullable = true, precision = 17, scale = 13)
  private BigDecimal guessedY;

  @Column(name = "\"guessTimeSeconds\"", nullable = false)
  private Long guessTimeSeconds;

  @Column(name = "\"distanceMeters\"", nullable = true)
  private Integer distanceMeters;

  @Column(name = "\"score\"", nullable = false)
  private Integer score;
}
