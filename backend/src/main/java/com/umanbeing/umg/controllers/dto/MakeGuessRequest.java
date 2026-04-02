package com.umanbeing.umg.controllers.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Data transfer object for making a guess.
 * It is used for transferring guess data between the controller and service layers.
 * <p>It must contain:<li>a valid coordinate for the x-axis</li><li>a valid coordinate for the y-axis</li>
 * <li>a valid time taken to make the guess in seconds</li></p>
 */
@Setter
@Getter
public class MakeGuessRequest {

    @NotBlank
    private BigDecimal corX;

    @NotBlank
    private BigDecimal corY;

    @NotBlank
    private Long guessTimeSeconds;

}