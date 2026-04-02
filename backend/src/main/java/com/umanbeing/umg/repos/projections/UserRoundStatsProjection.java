package com.umanbeing.umg.repos.projections;

public interface UserRoundStatsProjection {

  Long getTotalGuessTimeSeconds();

  Double getAverageGuessTimeSeconds();
}
