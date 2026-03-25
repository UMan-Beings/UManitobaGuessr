package com.umanbeing.umg.controllers.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


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