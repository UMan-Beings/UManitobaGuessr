package com.umanbeing.umg.controllers;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RequestMethod;
import com.umanbeing.umg.services.GameService;
import com.umanbeing.umg.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import com.umanbeing.umg.models.Game;
import com.umanbeing.umg.models.Guess;
import com.umanbeing.umg.models.Round;
import com.umanbeing.umg.models.User;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.umanbeing.umg.controllers.dto.CreateGameRequest;
import com.umanbeing.umg.controllers.dto.MakeGuessRequest;
import com.umanbeing.umg.domain.GameState;

@RestController
@RequestMapping("/api/v1")
public class GameController {
    
    @Autowired
    private GameService gameService;

    @Autowired
    private UserService userService;

    //Implement the game creation logic here
    //Return game ID, initial game state (GUESS phase)
    //Receive total rouns, count down seconds, and user ID as parameters
    @RequestMapping(value = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createNewGame(@RequestBody CreateGameRequest request, Authentication authentication) {
        User user = getAuthenticatedUserOrNull(authentication);
        Long userId = user != null ? user.getUserId() : null;

        Game game = gameService.createNewGame(request.getTotalRounds(), request.getMaxTimerSeconds(), userId);

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
    public ResponseEntity<Map<String, Object>> getGameById(@PathVariable Long gameId, Authentication authentication) {
        Game game = gameService.getGameById(gameId);
        checkGameOwnership(game, authentication);

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
    public ResponseEntity<Map<String, Object>> makeGuess(@PathVariable Long gameId, @RequestBody MakeGuessRequest request, Authentication authentication) {
        checkGameOwnership(gameService.getGameById(gameId), authentication);

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
    public ResponseEntity<Map<String, Object>> timeout(@PathVariable Long gameId, Authentication authentication) {
        checkGameOwnership(gameService.getGameById(gameId), authentication);

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
    public ResponseEntity<Map<String, Object>> requestNextRound(@PathVariable Long gameId, Authentication authentication) {
        checkGameOwnership(gameService.getGameById(gameId), authentication);

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

    private User getAuthenticatedUserOrNull(Authentication authentication) {
        return authentication != null ? userService.getUserByEmail(authentication.getName()) : null;
    }

    private void checkGameOwnership(Game game, Authentication authentication) {
        User user = getAuthenticatedUserOrNull(authentication);
        User gameUser = game.getUser();
        if (gameUser != null && (user == null || !gameUser.getUserId().equals(user.getUserId()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
    

}
