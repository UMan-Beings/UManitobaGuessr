package com.umanbeing.umg.controllers;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import com.umanbeing.umg.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import com.umanbeing.umg.models.Game;
import com.umanbeing.umg.models.Guess;
import com.umanbeing.umg.models.Round;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

import com.umanbeing.umg.controllers.dto.CreateGameRequest;
import com.umanbeing.umg.controllers.dto.MakeGuessRequest;
import com.umanbeing.umg.domain.GameState;

@RestController
@RequestMapping("/api/v1")
public class GameController {
    
    @Autowired
    private GameService gameService;

    private static final String JSON_GAME_ID = "gameId";
    private static final String JSON_PHASE = "phase";
    private static final String JSON_ROUND = "round";
    private static final String JSON_TOTAL_ROUNDS = "totalRounds";
    private static final String JSON_IMAGE_URL = "imageUrl";
    private static final String JSON_SCORE = "score";
    private static final String JSON_TIME_LIMIT_SECONDS = "timeLimitSeconds";
    private static final String JSON_ACTUAL_X = "actualX";
    private static final String JSON_ACTUAL_Y = "actualY";
    private static final String JSON_GUESSED_X = "guessedX";
    private static final String JSON_GUESSED_Y = "guessedY";
    private static final String JSON_SCORE_RECEIVED = "scoreReceived";
    private static final String JSON_GUESS_TIME_SECONDS = "guessTimeSeconds";

    //Implement the game creation logic here
    //Return game ID, initial game state (GUESS phase)
    //Receive total rouns, count down seconds, and user ID as parameters
    @RequestMapping(value = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createNewGame(@RequestBody CreateGameRequest request) {
        Game game = gameService.createNewGame(request.getTotalRounds(), request.getMaxTimerSeconds(), request.getUserId());

        Map<String, Object> response = new HashMap<>();
        response.put(JSON_GAME_ID, game.getGameId());
        response.put(JSON_PHASE, game.getGameState());
        response.put(JSON_ROUND, game.getCurrentRoundNumber());
        response.put(JSON_TOTAL_ROUNDS, game.getTotalRounds());
        response.put(JSON_IMAGE_URL, game.getCurrentRound().getLocation().getImageUrl());
        response.put(JSON_SCORE, game.getScore());
        response.put(JSON_TIME_LIMIT_SECONDS, game.getMaxTimerSeconds());

        return ResponseEntity.ok(response);
    }

    //Implement the game update logic here
    //Return the current game state (GUESS phase, REVEAL phase, or FINISHED)
    @RequestMapping(value = "/games/{gameId}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getGameById(@PathVariable Long gameId) {
        Game game = gameService.getGameById(gameId);

        GameState phase = game.getGameState();
        Round currentRound = game.getCurrentRound();
        Guess guess = currentRound.getGuess();
        
        Map<String, Object> response = new HashMap<>();
        response.put(JSON_PHASE, phase);
        response.put(JSON_SCORE, game.getScore());
        response.put(JSON_TOTAL_ROUNDS, game.getTotalRounds());
        response.put(JSON_ROUND, game.getCurrentRoundNumber());
        response.put(JSON_TIME_LIMIT_SECONDS, game.getMaxTimerSeconds());

        if (GameState.GUESS == phase) {
            response.put(JSON_IMAGE_URL, currentRound.getLocation().getImageUrl());
        }
        else if (GameState.REVEAL == phase) {
            response.put(JSON_ACTUAL_X, currentRound.getLocation().getCorX());
            response.put(JSON_ACTUAL_Y, currentRound.getLocation().getCorY());

            if (guess != null) {
                response.put(JSON_GUESSED_X, guess.getGuessedX());
                response.put(JSON_GUESSED_Y, guess.getGuessedY());
                response.put(JSON_SCORE_RECEIVED, guess.getScore());
                response.put(JSON_GUESS_TIME_SECONDS, guess.getGuessTimeSeconds());
            }
        }

        return ResponseEntity.ok(response);
    }

    //Implement the guess submission logic here
    //Return the result of the guess (actual location, score for the round, and updated game state)
    @RequestMapping(value = "/games/{gameId}/guess", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> makeGuess(@PathVariable Long gameId, @RequestBody MakeGuessRequest request) {
        Game game = gameService.submitGuess(gameId, request.getCorX(), request.getCorY(), request.getGuessTimeSeconds());
        Round currentRound = game.getCurrentRound();
        Guess guess = currentRound.getGuess();

        Map<String, Object> response = new HashMap<>();
        response.put(JSON_PHASE, game.getGameState());
        response.put(JSON_ROUND, game.getCurrentRoundNumber());
        response.put(JSON_TOTAL_ROUNDS, game.getTotalRounds());
        response.put(JSON_IMAGE_URL, currentRound.getLocation().getImageUrl());
        response.put(JSON_TIME_LIMIT_SECONDS, game.getMaxTimerSeconds());
        response.put(JSON_GUESSED_X, guess.getGuessedX());
        response.put(JSON_GUESSED_Y, guess.getGuessedY());
        response.put(JSON_ACTUAL_X, currentRound.getLocation().getCorX());
        response.put(JSON_ACTUAL_Y, currentRound.getLocation().getCorY());
        response.put(JSON_SCORE, game.getScore());
        response.put(JSON_SCORE_RECEIVED, guess.getScore());
        response.put(JSON_GUESS_TIME_SECONDS, guess.getGuessTimeSeconds());

        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/games/{gameId}/timeout", method=RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> timeout(@PathVariable Long gameId) {
        Game game = gameService.timeout(gameId);
        Round currentRound = game.getCurrentRound();
        Guess guess = currentRound.getGuess();

        Map<String, Object> response = new HashMap<>();
        response.put(JSON_PHASE, game.getGameState());
        response.put(JSON_ROUND, game.getCurrentRoundNumber());
        response.put(JSON_TOTAL_ROUNDS, game.getTotalRounds());
        response.put(JSON_IMAGE_URL, currentRound.getLocation().getImageUrl());
        response.put(JSON_TIME_LIMIT_SECONDS, game.getMaxTimerSeconds());
        response.put(JSON_ACTUAL_X, currentRound.getLocation().getCorX());
        response.put(JSON_ACTUAL_Y, currentRound.getLocation().getCorY());
        response.put(JSON_SCORE, game.getScore());
        response.put(JSON_SCORE_RECEIVED, guess.getScore());
        response.put(JSON_GUESS_TIME_SECONDS, guess.getGuessTimeSeconds());

        return ResponseEntity.ok(response);
    }
    
    //Implement the logic to move to the next round here
    //Return the new game state (GUESS phase for the next round, or FINISHED if it was the last round)
    @RequestMapping(value = "/games/{gameId}/next", method=RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> requestNextRound(@PathVariable Long gameId) {
        Game game = gameService.nextRound(gameId);

        Map<String, Object> response = new HashMap<>();
        response.put(JSON_PHASE, game.getGameState());
        response.put(JSON_SCORE, game.getScore());
        response.put(JSON_TOTAL_ROUNDS, game.getTotalRounds());
        response.put(JSON_ROUND, game.getCurrentRoundNumber());
        response.put(JSON_TIME_LIMIT_SECONDS, game.getMaxTimerSeconds());

        if (!game.isCompleted()) {
            response.put(
                JSON_IMAGE_URL,
                game.getCurrentRound().getLocation().getImageUrl()
            );
        }

        return ResponseEntity.ok(response);
    }
    

}
