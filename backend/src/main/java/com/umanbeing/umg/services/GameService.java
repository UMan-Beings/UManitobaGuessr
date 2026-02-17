package com.umanbeing.umg.services;

import org.springframework.stereotype.Service;
import com.umanbeing.umg.models.Game;
import com.umanbeing.umg.repos.GameRepo;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class GameService {

    @Autowired
    private GameRepo gameRepo;

    public Game createNewGame(int totalRounds, int maxTimerSeconds) {
        Game game = new Game();
        game.setTotalRounds(totalRounds);
        game.setMaxTimerSeconds(maxTimerSeconds);
        game.setCompleted(false);
        game.setCurrentRoundNumber(1);
        game.setGameState("GUESS");
        //TODO: Add rounds required for the game after implementing the Round entity
        return gameRepo.save(game);
    }

    public Game save(Game game) {
        return gameRepo.save(game);
    }

    public Game getGameById(Long gameId) {
        return gameRepo.findById(gameId).orElse(null);
    }

    public void deleteGameById(Long gameId) {
        gameRepo.deleteById(gameId);
    }


}

