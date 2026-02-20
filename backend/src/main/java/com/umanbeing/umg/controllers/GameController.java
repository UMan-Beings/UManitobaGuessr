package com.umanbeing.umg.controllers;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.umanbeing.umg.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import com.umanbeing.umg.models.Game;
import com.umanbeing.umg.models.Guess;
import com.umanbeing.umg.services.GuessService;
import com.umanbeing.umg.models.Round;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import com.umanbeing.umg.controllers.dto.CreateGameRequest;
import com.umanbeing.umg.controllers.dto.MakeGuessRequest;


@RestController
@RequestMapping("/api/v1")
public class GameController {
    
    @Autowired
    private GameService gameService;

    @Autowired
    private GuessService guessService;

    //TODO: Implement the game creation logic here
    //Return game ID, initial game state (GUESS phase)
    //Receive total rouns, count down seconds, and user ID as parameters
    @RequestMapping(value = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createNewGame(@RequestBody CreateGameRequest request) {
        Game game = gameService.createNewGame(request.getTotalRounds(), request.getMaxTimerSeconds());
        
        Map<String, Object> response = new HashMap<>();
        response.put("gameId", game.getGameId());
        response.put("phase", game.getGameState());
        response.put("round", game.getCurrentRoundNumber());
        response.put("totalRounds", game.getTotalRounds());
        response.put("imageUrl", game.getRounds().get(game.getCurrentRoundNumber() - 1).getLocation().getImageUrl());
        response.put("score", 0);
        response.put("timeLimitSeconds", game.getMaxTimerSeconds());

        return ResponseEntity.ok(response);
    }

    //TODO: Implement the game update logic here
    //Return the current game state (GUESS phase, REVEAL phase, or FINISHED)
    @RequestMapping(value = "/games/{gameId}", method = RequestMethod.GET)
    public String getGameById(@RequestParam Long gameId) {
        Game game = gameService.getGameById(gameId);
        Map<String, Object> response = new HashMap<>();
        
        if (game == null) {
            return "Game not found";
        }

        response.put("phase", game.getGameState());
        return ResponseEntity.ok(response).toString();
    }

    //TODO: Implement the guess submission logic here
    //Return the result of the guess (actual location, score for the round, and updated game state)
    @RequestMapping(value = "/games/{gameId}/guess", method = RequestMethod.POST)
    public String makeGuess(@RequestParam Long gameId, @RequestBody MakeGuessRequest request) {
        Game game = gameService.getGameById(gameId);
        Round currentRound = game.getRounds().get(game.getCurrentRoundNumber() - 1);
        Guess guess = guessService.createGuess(currentRound, request.getCorX(), request.getCorY());

        game.setGameState("REVEAL");
        game.setCurrentRoundNumber(game.getCurrentRoundNumber() + 1);

        Map<String, Object> response = new HashMap<>();
        response.put("phase", game.getGameState());
        response.put("round", game.getCurrentRoundNumber());
        response.put("totalRounds", game.getTotalRounds());
        response.put("imageUrl", game.getRounds().get(game.getCurrentRoundNumber() - 1).getLocation().getImageUrl());
        response.put("timeLimitSeconds", game.getMaxTimerSeconds());
        response.put("guessedX", request.getCorX());
        response.put("guessedY", request.getCorY());
        response.put("actualX", currentRound.getLocation().getCorX());
        response.put("actualY", currentRound.getLocation().getCorY());
        response.put("score", game.getScore());
        response.put("scoreReceived", guess.getScore());

        return ResponseEntity.ok(response).toString();
    }
    
    //TODO: Implement the logic to move to the next round here
    //Return the new game state (GUESS phase for the next round, or FINISHED if it was the last round)
    @RequestMapping(value = "/games/{gameId}/next", method=RequestMethod.POST)
    public String requestNextRound(@RequestParam Long gameId) {
        Game game = gameService.getGameById(gameId);
        Map<String, Object> response = new HashMap<>();
        if (game.getCurrentRoundNumber() > game.getTotalRounds()) {
            game.setGameState("FINISHED");
            game.setCompleted(true);
            response.put("phase", game.getGameState());
            response.put("score", game.getScore());
        } else {
            game.setGameState("GUESS");
            game.setCurrentRoundNumber(game.getCurrentRoundNumber() + 1);
            response.put("phase", game.getGameState());
            response.put("round", game.getCurrentRoundNumber());
            response.put("totalRounds", game.getTotalRounds());
            response.put("imageUrl", game.getRounds().get(game.getCurrentRoundNumber() - 1).getLocation().getImageUrl());
            response.put("timeLimitSeconds", game.getMaxTimerSeconds());
        }
        

        return ResponseEntity.ok(response).toString();
    }
    

}
