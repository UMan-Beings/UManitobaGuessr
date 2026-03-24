package com.umanbeing.umg.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.umanbeing.umg.controllers.dto.UserStatsResponse;
import com.umanbeing.umg.domain.GameState;
import com.umanbeing.umg.models.Game;
import com.umanbeing.umg.models.Guess;
import com.umanbeing.umg.models.Round;
import com.umanbeing.umg.models.User;
import com.umanbeing.umg.repos.GameRepo;
import com.umanbeing.umg.repos.projections.UserGameStatsProjection;
import com.umanbeing.umg.repos.projections.UserRoundStatsProjection;

@Service
public class GameService {

    private static final int MAX_ROUNDS = 20;
    private static final int MAX_TIME_LIMIT_SECONDS = 300;
    private static final int MAX_GUESS_TIME_SECONDS = 3600;
    
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Total rounds must be greater than 0");
        }

        if (totalRounds > MAX_ROUNDS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Total rounds cannot exceed " + MAX_ROUNDS);
        }

        if (maxTimerSeconds < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Max timer seconds must be greater than or equal to 0");
        }

        if (maxTimerSeconds > MAX_TIME_LIMIT_SECONDS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Max timer seconds cannot exceed " + MAX_TIME_LIMIT_SECONDS);
        }
        
        Game game = new Game();

        // Get the user from the UserService and set it to the game
        User user = userId == null ? null : userService.getUserById(userId);

        game.setTotalRounds(totalRounds);
        game.setMaxTimerSeconds(maxTimerSeconds);
        game.setCompleted(false);
        game.setCurrentRoundNumber(1);
        game.setGameState(GameState.GUESS);
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

        if (GameState.GUESS != game.getGameState()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Game is not in a state to accept guesses");
        }

        if (guessedX == null || guessedY == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Guessed coordinates must be provided");
        }

        if (guessTimeSeconds == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Guess time must be provided");
        }

        if (game.getMaxTimerSeconds() > 0) {
            guessTimeSeconds = Math.min(guessTimeSeconds, game.getMaxTimerSeconds());
        } else {
            guessTimeSeconds = Math.min(guessTimeSeconds, MAX_GUESS_TIME_SECONDS);
        }
        
        Round currentRound = game.getCurrentRound();

        guessService.createGuess(currentRound, guessedX, guessedY, guessTimeSeconds);
        game.setGameState(GameState.REVEAL);

        return gameRepo.save(game);
    }

    @Transactional
    public Game timeout(Long gameId) {
        Game game = getGameById(gameId);

        if (game.getMaxTimerSeconds() <= 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Game does not have a timer set");
        }

        if (GameState.GUESS != game.getGameState()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Game is not in a state to accept guesses");
        }

        Round currentRound = game.getCurrentRound();
        long guessTime = game.getMaxTimerSeconds();

        guessService.createGuess(currentRound, null, null, guessTime);

        game.setGameState(GameState.REVEAL);

        return gameRepo.save(game);
    }

    @Transactional
    public Game nextRound(Long gameId) {
        Game game = getGameById(gameId);

        if (GameState.REVEAL != game.getGameState()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Game is not in a state to proceed to the next round");
        }

        int currentRoundNumber = game.getCurrentRoundNumber();
        Round currentRound = game.getCurrentRound();
        Guess guess = currentRound.getGuess();

        if (guess != null) {
            game.addScore(guess.getScore());
        }

        if (currentRoundNumber == game.getTotalRounds()) {
            game.setGameState(GameState.FINISHED);
            game.setCompleted(true);
        } else {
            game.setGameState(GameState.GUESS);
            game.incrementCurrentRoundNumber();
        }

        return gameRepo.save(game);
    }

    public Game getGameById(Long gameId) {
        if (gameId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game ID must be provided");
        }

        Game game = gameRepo.findById(gameId).orElse(null);

        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }

        return game;
    }

    public UserStatsResponse getUserStats(Long userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID must be provided");
        }

        UserGameStatsProjection gameStats = gameRepo.getUserGameStats(userId);
        UserRoundStatsProjection roundStats = gameRepo.getUserRoundStats(userId);

        return new UserStatsResponse(
            gameStats.getTotalScore(),
            gameStats.getTotalRounds(),
            gameStats.getTotalGames(),
            gameStats.getAverageScore(),
            roundStats.getTotalGuessTimeSeconds(),
            roundStats.getAverageGuessTimeSeconds()
        );
    }
}