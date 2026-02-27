package com.umanbeing.umg.controllers.dto;

import java.math.BigDecimal;

public class MakeGuessRequest {
    private BigDecimal corX;
    private BigDecimal corY;
    private Long guessTimeSeconds;

    // Getters and setters
    public BigDecimal getCorX() {
        return corX;
    }

    public void setCorX(BigDecimal corX) {
        this.corX = corX;
    }

    public BigDecimal getCorY() {
        return corY;
    }

    public void setCorY(BigDecimal corY) {
        this.corY = corY;
    }

    public Long getGuessTimeSeconds() {
        return guessTimeSeconds;
    }

    public void setGuessTimeSeconds(Long guessTimeSeconds) {
        this.guessTimeSeconds = guessTimeSeconds;
    }
}