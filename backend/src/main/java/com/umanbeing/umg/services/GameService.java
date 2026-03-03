package com.umanbeing.umg.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.umanbeing.umg.models.Game;
import com.umanbeing.umg.models.Guess;
import com.umanbeing.umg.repos.GameRepo;
import com.umanbeing.umg.models.User;
import com.umanbeing.umg.models.Round;

import java.math.BigDecimal;
import java.util.List;

@Service
public class GameService {

    
    private final GameRepo gameRepo;

    private final RoundService roundService;

    private final GuessService guessService;

    private final UserService userService;


    public GameService(GameRepo gameRepo, RoundService roundService, GuessService guessService, UserService userService) {
        this.gameRepo = gameRepo;
        this.roundService = roundService;
        this.guessService = guessService;
        this.userService = userService;
    }

    @Transactional
    public Game createNewGame(int totalRounds, int maxTimerSeconds, Long userId) {
        if (totalRounds <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (maxTimerSeconds < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        
        Game game = new Game();

        // Get the user from the UserService and set it to the game
        User user = userId == null ? null : userService.getUserById(userId);

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
    public Game submitGuess(Long gameId, BigDecimal guessedX, BigDecimal guessedY, Long guessTimeSeconds) {
        Game game = getGameById(gameId);

        if (!"GUESS".equals(game.getGameState())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        if (guessedX == null || guessedY == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (guessTimeSeconds == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        
        Round currentRound = game.getRounds().get(game.getCurrentRoundNumber() - 1);

        guessService.createGuess(currentRound, guessedX, guessedY, guessTimeSeconds);
        game.setGameState("REVEAL");

        return gameRepo.save(game);
    }

    @Transactional
    public Game nextRound(Long gameId) {
        Game game = getGameById(gameId);

        if (!"REVEAL".equals(game.getGameState())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        int currentRoundNumber = game.getCurrentRoundNumber();
        Round currentRound = game.getRounds().get(currentRoundNumber - 1);
        Guess guess = currentRound.getGuess();

        if (guess != null) {
            game.setScore(game.getScore() + guess.getScore());
        }

        if (currentRoundNumber == game.getTotalRounds()) {
            game.setGameState("FINISHED");
            game.setCompleted(true);
        } else {
            game.setGameState("GUESS");
            game.setCurrentRoundNumber(currentRoundNumber + 1); 
        }

        return gameRepo.save(game);
    }

    @Transactional
    public Game save(Game game) {
        return gameRepo.save(game);
    }

    public Game getGameById(Long gameId) {
        if (gameId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Game game = gameRepo.findById(gameId).orElse(null);

        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return game;
    }

    public void deleteGameById(Long gameId) {
        gameRepo.deleteById(gameId);
    }


}

