package com.umanbeing.umg.repos.projections;

public interface UserGameStatsProjection {

    Long getTotalScore();

    Long getTotalRounds();

    Long getTotalGames();

    Double getAverageScore();

}