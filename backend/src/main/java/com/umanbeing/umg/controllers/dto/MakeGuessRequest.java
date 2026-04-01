package com.umanbeing.umg.controllers.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

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