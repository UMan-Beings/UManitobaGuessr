package com.umanbeing.umg.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.umanbeing.umg.models.Game;

@Repository
public interface GameRepo extends JpaRepository<Game, Long> {
    List<Game> findByUser_UserId(Long userId);

    List<Game> findByGameId(Long gameId);
}
