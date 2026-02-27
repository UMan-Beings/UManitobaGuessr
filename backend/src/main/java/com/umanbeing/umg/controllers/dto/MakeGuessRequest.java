package com.umanbeing.umg.controllers.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class MakeGuessRequest {

    private BigDecimal corX;

    private BigDecimal corY;
    
    private Long guessTimeSeconds;

}