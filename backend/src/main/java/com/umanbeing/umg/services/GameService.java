package com.umanbeing.umg.services;

import org.springframework.stereotype.Service;
import com.umanbeing.umg.models.Game;
import com.umanbeing.umg.repos.GameRepo;

@Service
public class GameService {

    
    private final GameRepo gameRepo;

    private final RoundService roundService;

    public GameService(GameRepo gameRepo, RoundService roundService) {
        this.gameRepo = gameRepo;
        this.roundService = roundService;
    }

    public Game createNewGame(int totalRounds, int maxTimerSeconds) {
        Game game = new Game();
        game.setTotalRounds(totalRounds);
        game.setMaxTimerSeconds(maxTimerSeconds);
        game.setCompleted(false);
        game.setCurrentRoundNumber(1);
        game.setGameState("GUESS");
        game.setScore(0);

        // Save the game first to ensure it has an ID
        Game savedGame = gameRepo.save(game);

        // Use RoundService to create rounds for the saved game
        savedGame.setRounds(roundService.createRoundForGame(savedGame));

        return gameRepo.save(savedGame);
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

