package com.umanbeing.umg.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.umanbeing.umg.models.Game;
import com.umanbeing.umg.repos.GameRepo;
import com.umanbeing.umg.models.User;
import com.umanbeing.umg.models.Round;
import java.util.List;

@Service
public class GameService {

    
    private final GameRepo gameRepo;

    private final RoundService roundService;

    private final UserService userService;


    public GameService(GameRepo gameRepo, RoundService roundService, UserService userService) {
        this.gameRepo = gameRepo;
        this.roundService = roundService;
        this.userService = userService;
    }

    @Transactional
    public Game createNewGame(int totalRounds, int maxTimerSeconds, Long userId) {
        Game game = new Game();

        // Get the user from the UserService and set it to the game
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User with ID " + userId + " not found");
        }

        game.setTotalRounds(totalRounds);
        game.setMaxTimerSeconds(maxTimerSeconds);
        game.setCompleted(false);
        game.setCurrentRoundNumber(1);
        game.setGameState("GUESS");
        game.setScore(0);
        game.setUser(user);
        // Save the game first to ensure it has an ID
        Game savedGame = gameRepo.save(game);

        // Use RoundService to create rounds for the saved game
        List<Round> rounds = roundService.createRoundForGame(savedGame);

        savedGame.getRounds().addAll(rounds);

        return gameRepo.save(savedGame);
    }

    @Transactional
    public Game save(Game game) {
        return gameRepo.save(game);
    }

    public Game getGameById(Long gameId) {
        Game game = gameRepo.findById(gameId).orElse(null);
        if (game != null) {
            game.setRounds(roundService.getRoundsForGame(game));
        }
        return game;
    }

    public void deleteGameById(Long gameId) {
        gameRepo.deleteById(gameId);
    }


}

