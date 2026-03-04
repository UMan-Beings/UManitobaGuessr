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

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

import com.domain.GameState;
import com.umanbeing.umg.controllers.dto.CreateGameRequest;
import com.umanbeing.umg.controllers.dto.MakeGuessRequest;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1")
public class GameController {
    
    @Autowired
    private GameService gameService;

    //Implement the game creation logic here
    //Return game ID, initial game state (GUESS phase)
    //Receive total rouns, count down seconds, and user ID as parameters
    @RequestMapping(value = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createNewGame(@RequestBody CreateGameRequest request) {
        Game game = gameService.createNewGame(request.getTotalRounds(), request.getMaxTimerSeconds(), request.getUserId());

        Map<String, Object> response = new HashMap<>();
        response.put("gameId", game.getGameId());
        response.put("phase", game.getGameState());
        response.put("round", game.getCurrentRoundNumber());
        response.put("totalRounds", game.getTotalRounds());
        response.put("imageUrl", game.getCurrentRound().getLocation().getImageUrl());
        response.put("score", game.getScore());
        response.put("timeLimitSeconds", game.getMaxTimerSeconds());

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
        response.put("phase", phase);
        response.put("score", game.getScore());
        response.put("totalRounds", game.getTotalRounds());
        response.put("round", game.getCurrentRoundNumber());
        response.put("timeLimitSeconds", game.getMaxTimerSeconds());

        if (GameState.GUESS == phase) {
            response.put("imageUrl", currentRound.getLocation().getImageUrl());
        }
        else if (GameState.REVEAL == phase) {
            response.put("actualX", currentRound.getLocation().getCorX());
            response.put("actualY", currentRound.getLocation().getCorY());

            if (guess != null) {
                response.put("guessedX", guess.getGuessedX());
                response.put("guessedY", guess.getGuessedY());
                response.put("scoreReceived", guess.getScore());
                response.put("guessTimeSeconds", guess.getGuessTimeSeconds());
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
        response.put("phase", game.getGameState());
        response.put("round", game.getCurrentRoundNumber());
        response.put("totalRounds", game.getTotalRounds());
        response.put("imageUrl", currentRound.getLocation().getImageUrl());
        response.put("timeLimitSeconds", game.getMaxTimerSeconds());
        response.put("guessedX", guess.getGuessedX());
        response.put("guessedY", guess.getGuessedY());
        response.put("actualX", currentRound.getLocation().getCorX());
        response.put("actualY", currentRound.getLocation().getCorY());
        response.put("score", game.getScore());
        response.put("scoreReceived", guess.getScore());
        response.put("guessTimeSeconds", guess.getGuessTimeSeconds());

        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/games/{gameId}/timeout", method=RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> timeout(@PathVariable Long gameId) {
        Game game = gameService.timeout(gameId);
        Round currentRound = game.getCurrentRound();
        Guess guess = currentRound.getGuess();

        Map<String, Object> response = new HashMap<>();
        response.put("phase", game.getGameState());
        response.put("round", game.getCurrentRoundNumber());
        response.put("totalRounds", game.getTotalRounds());
        response.put("imageUrl", currentRound.getLocation().getImageUrl());
        response.put("timeLimitSeconds", game.getMaxTimerSeconds());
        response.put("actualX", currentRound.getLocation().getCorX());
        response.put("actualY", currentRound.getLocation().getCorY());
        response.put("score", game.getScore());
        response.put("scoreReceived", guess.getScore());
        response.put("guessTimeSeconds", guess.getGuessTimeSeconds());

        return ResponseEntity.ok(response);
    }
    
    //Implement the logic to move to the next round here
    //Return the new game state (GUESS phase for the next round, or FINISHED if it was the last round)
    @RequestMapping(value = "/games/{gameId}/next", method=RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> requestNextRound(@PathVariable Long gameId) {
        Game game = gameService.nextRound(gameId);

        Map<String, Object> response = new HashMap<>();
        response.put("phase", game.getGameState());
        response.put("score", game.getScore());
        response.put("totalRounds", game.getTotalRounds());
        response.put("round", game.getCurrentRoundNumber());
        response.put("timeLimitSeconds", game.getMaxTimerSeconds());

        if (!game.isCompleted()) {
            response.put(
                "imageUrl",
                game.getCurrentRound().getLocation().getImageUrl()
            );
        }

        return ResponseEntity.ok(response);
    }
    

}
