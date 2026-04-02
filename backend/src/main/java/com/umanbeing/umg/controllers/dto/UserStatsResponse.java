package com.umanbeing.umg.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Data transfer object for user statistics.
 * It is used for transferring user statistics data between the controller and service layers.
 * <p>It must contain:
 * <li>a valid total score</li>
 * <li>a valid total number of rounds played</li>
 * <li>a valid total number of games played</li>
 * <li>a valid average score</li>
 * <li>a valid total time taken to make guesses in seconds</li>
 * <li>a valid average time taken to make guesses in seconds</li></p>
 */
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