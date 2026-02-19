package com.umanbeing.umg.services;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import com.umanbeing.umg.models.Guess;
import com.umanbeing.umg.repos.GuessRepo;
import com.umanbeing.umg.models.Round;
import java.lang.Math;

@Service
public class GuessService {

    private final GuessRepo guessRepo;

    public GuessService(GuessRepo guessRepo) {
        this.guessRepo = guessRepo;
    }

    public Guess save(Guess guess) {
        return guessRepo.save(guess);
    }

    public Guess createGuess(Round round, BigDecimal guessedX, BigDecimal guessedY) {
        Guess guess = new Guess();
        guess.setRound(round);
        guess.setGuessedX(guessedX);
        guess.setGuessedY(guessedY);
        //TODO: Calculate guessTimeMs, distanceMeters, and score based on the round's location and the guessed coordinates
        guess.setDistanceMeters(distance(guessedX, guessedY, round.getLocation().getCorX(), round.getLocation().getCorY()));
        guess.setScore(calculateScore(guessedX, guessedY, round.getLocation().getCorX(), round.getLocation().getCorY()));
        round.getGame().setScore(round.getGame().getScore() + guess.getScore());
        return guessRepo.save(guess);
    }

    // private Integer distance(BigDecimal x1, BigDecimal y1, BigDecimal x2, BigDecimal y2) {
    //     final int R = 6371; // Mean radius of the Earth in kilometers
    //     double latDistance = Math.toRadians(x2.doubleValue() - x1.doubleValue());
    //     double lonDistance = Math.toRadians(y2.doubleValue() - y1.doubleValue());
    //     double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
    //             + Math.cos(Math.toRadians(x1.doubleValue())) * Math.cos(Math.toRadians(x2.doubleValue()))
    //             * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
    //     double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    //     return (int) (R * c * 1000); // Distance in meters
    // }

    private Integer distance(BigDecimal x1, BigDecimal y1, BigDecimal x2, BigDecimal y2) {
        return (int) Math.sqrt(Math.pow(x2.doubleValue() - x1.doubleValue(), 2) + Math.pow(y2.doubleValue() - y1.doubleValue(), 2));
    }

    

    //TODO: Add time in consideration for scoring
    public Integer calculateScore(BigDecimal x1, BigDecimal y1, BigDecimal x2, BigDecimal y2) {
        Integer distanceInteger = distance(x1, y1, x2, y2);
        // Simple scoring algorithm: 1000 points for a perfect guess, minus 1 point per meter and 1 point per second
        //int score = 1000 - distanceMeters - (guessTimeMs / 1000);
        return (int) (1000 * Math.exp(-0.01 * distanceInteger));
    }

}
