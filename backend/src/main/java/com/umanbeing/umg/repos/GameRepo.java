package com.umanbeing.umg.repos;

import com.umanbeing.umg.models.Game;
import com.umanbeing.umg.repos.projections.UserGameStatsProjection;
import com.umanbeing.umg.repos.projections.UserRoundStatsProjection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepo extends JpaRepository<Game, Long> {

    List<Game> findByUser_UserId(Long userId);

    List<Game> findByGameId(Long gameId);

    @Query("SELECT COALESCE(SUM(g.score), 0L) AS totalScore, " +
            "COALESCE(SUM(g.totalRounds), 0L) AS totalRounds, " +
            "COUNT(g) AS totalGames, " +
            "COALESCE(AVG(g.score), 0.0) AS averageScore " +
            "FROM Game g " +
            "WHERE g.user.userId = ?1 AND g.isCompleted = true")
    UserGameStatsProjection getUserGameStats(Long userId);

    @Query("SELECT COALESCE(SUM(gu.guessTimeSeconds), 0L) AS totalGuessTimeSeconds, " +
            "COALESCE(AVG(gu.guessTimeSeconds), 0.0) AS averageGuessTimeSeconds " +
            "FROM Game g " +
            "JOIN g.rounds r " +
            "JOIN r.guess gu " +
            "WHERE g.user.userId = ?1 AND g.isCompleted = true")
    UserRoundStatsProjection getUserRoundStats(Long userId);

}