package com.umanbeing.umg.controllers;
import java.math.BigDecimal;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/v1")
public class GameController {
    
    //TODO: Implement the game creation logic here
    //Return game ID, initial game state (GUESS phase)
    //Receive total rouns, count down seconds, and user ID as parameters
    @RequestMapping(value = "/games", method = RequestMethod.POST)
    public String createNewGame(@RequestParam Integer totalRounds, @RequestParam Integer maxTimerSeconds, @RequestParam Long userId) {
        
        return "Hello, World!";
    }

    //TODO: Implement the game update logic here
    //Return the current game state (GUESS phase, REVEAL phase, or FINISHED)
    @RequestMapping(value = "/games/{gameId}", method = RequestMethod.GET)
    public String getGameById(@RequestParam Long gameId) {
        return new String();
    }

    //TODO: Implement the guess submission logic here
    //Return the result of the guess (actual location, score for the round, and updated game state)
    @RequestMapping(value = "/games/{gameId}/guess", method = RequestMethod.POST)
    public String makeGuess(@RequestParam Long gameId, @RequestParam BigDecimal lat, @RequestParam BigDecimal lng) {
        return new String();
    }
    
    //TODO: Implement the logic to move to the next round here
    //Return the new game state (GUESS phase for the next round, or FINISHED if it was the last round)
    @RequestMapping(value = "/games/{gameId}/next", method=RequestMethod.POST)
    public String requestNextRound(@RequestParam Long gameId) {
        return new String();
    }
    

}
