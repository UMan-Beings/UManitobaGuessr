package com.umanbeing.umg.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "\"LOCATION\"")
@Getter
@Setter
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"locationId\"")
    private Long locationId;

    @Column(name = "\"name\"", nullable = false)
    private String name;

    @Column(name = "\"imageUrl\"", nullable = false)
    private String imageUrl;

    @Column(name = "\"corX\"", nullable = false, precision = 9, scale = 6)
    private BigDecimal corX;

    @Column(name = "\"corY\"", nullable = false, precision = 9, scale = 6)
    private BigDecimal corY;
}