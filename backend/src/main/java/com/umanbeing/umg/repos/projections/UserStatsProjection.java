package com.umanbeing.umg.repos.projections;

public interface UserStatsProjection {
    Long getTotalScore();
    Long getTotalRounds();
    Long getTotalGames();
    Double getAverageScore();
}