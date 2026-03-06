package com.umanbeing.umg.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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