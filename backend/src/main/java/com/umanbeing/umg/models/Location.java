package com.umanbeing.umg.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a location entity in the game. This class is mapped to the "LOCATION" table in the
 * database.
 *
 * <p>It has the following attributes:
 * <li>{@link Long} locationId
 * <li>{@link String} name
 * <li>{@link String} imageUrl
 * <li>{@link BigDecimal} corX
 * <li>{@link BigDecimal} corY
 */
@Entity
@Table(name = "\"LOCATION\"")
@Getter
@Setter
@NoArgsConstructor
public class Location {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "\"locationId\"")
  private Long locationId;

  @Column(name = "\"name\"", nullable = false)
  private String name;

  @Column(name = "\"imageUrl\"", nullable = false)
  private String imageUrl;

  @Column(name = "\"corX\"", nullable = false, precision = 17, scale = 13)
  private BigDecimal corX;

  @Column(name = "\"corY\"", nullable = false, precision = 17, scale = 13)
  private BigDecimal corY;

  public Location(String name, String imageUrl, BigDecimal corX, BigDecimal corY) {
    this.name = name;
    this.imageUrl = imageUrl;
    this.corX = corX;
    this.corY = corY;
  }
}
