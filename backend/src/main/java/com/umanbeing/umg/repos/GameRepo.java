package com.umanbeing.umg.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.umanbeing.umg.models.Game;

@Repository
public interface GameRepo extends JpaRepository<Game, Long> {
    List<Game> findByUser_UserId(Long userId);

    List<Game> findByGameId(Long gameId);

    @Query("SELECT SUM(g.score) FROM Game g WHERE g.user.userId = ?1")
    Long getTotalScoreByUserId(Long UserId);

    @Query("SELECT COUNT(r) FROM Game g JOIN g.rounds r WHERE g.user.userId = ?1")
    Long getTotalRoundsByUserId(Long userId);

    @Query("SELECT COUNT(g) FROM Game g WHERE g.user.userId = ?1")
    Long getTotalGamesByUserId(Long userId);

    @Query("SELECT AVG(g.score) FROM Game g WHERE g.user.userId = ?1")
    Double getAverageScoreByUserId(Long userId);
}