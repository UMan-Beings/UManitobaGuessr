package com.umanbeing.umg.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.umanbeing.umg.models.Game;
import com.umanbeing.umg.repos.projections.UserGameStatsProjection;
import com.umanbeing.umg.repos.projections.UserRoundStatsProjection;

@Repository
public interface GameRepo extends JpaRepository<Game, Long> {
    List<Game> findByUser_UserId(Long userId);

    List<Game> findByGameId(Long gameId);

    @Query("SELECT SUM(g.score) AS totalScore, " +
        "SUM(g.totalRounds) AS totalRounds, " +
        "COUNT(g) AS totalGames, " +
        "AVG(g.score) AS averageScore " +
        "FROM Game g " + 
        "WHERE g.user.userId = ?1 AND g.isCompleted = true")
    UserGameStatsProjection getUserGameStats(Long userId);

    @Query("SELECT SUM(gu.guessTimeSeconds) AS totalGuessTimeSeconds, " +
       "AVG(gu.guessTimeSeconds) AS averageGuessTimeSeconds " +
       "FROM Game g " +
       "JOIN g.rounds r " +
       "JOIN r.guess gu " +
       "WHERE g.user.userId = ?1")
    UserRoundStatsProjection getUserRoundStats(Long userId);
}