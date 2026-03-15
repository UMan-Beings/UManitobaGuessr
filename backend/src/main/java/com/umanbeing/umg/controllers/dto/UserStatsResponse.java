package com.umanbeing.umg.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserStatsResponse {
    private Long totalScore;
    private Long totalRounds;
    private Long totalGames;
    private Double averageScore;
    private Long totalGuessTimeSeconds;
    private Double averageGuessTimeSeconds;
}