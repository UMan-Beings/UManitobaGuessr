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

    public Guess createGuess(Round round, BigDecimal guessedLatitude, BigDecimal guessedLongitude) {
        Guess guess = new Guess();
        guess.setRound(round);
        guess.setGuessedLatitude(guessedLatitude);
        guess.setGuessedLongitude(guessedLongitude);
        //TODO: Calculate guessTimeMs, distanceMeters, and score based on the round's location and the guessed coordinates
        guess.setDistanceMeters(distance(guessedLatitude, guessedLongitude, guessedLatitude, guessedLongitude));
        guess.setScore(calculateScore(guessedLatitude, guessedLongitude, guessedLatitude, guessedLongitude));
        round.getGame().setScore(round.getGame().getScore() + guess.getScore());
        return guessRepo.save(guess);
    }

    private Integer distance(BigDecimal lat1, BigDecimal lon1, BigDecimal lat2, BigDecimal lon2) {
        final int R = 6371; // Mean radius of the Earth in kilometers
        double latDistance = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double lonDistance = Math.toRadians(lon2.doubleValue() - lon1.doubleValue());
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1.doubleValue())) * Math.cos(Math.toRadians(lat2.doubleValue()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (int) (R * c * 1000); // Distance in meters
    }

    //TODO: Add time in consideration for scoring
    public Integer calculateScore(BigDecimal lat1, BigDecimal lon1, BigDecimal lat2, BigDecimal lon2) {
        Integer distanceMeters = distance(lat1, lon1, lat2, lon2);
        // Simple scoring algorithm: 1000 points for a perfect guess, minus 1 point per meter and 1 point per second
        //int score = 1000 - distanceMeters - (guessTimeMs / 1000);
        return (int) (1000 * Math.exp(-0.01 * distanceMeters));
    }

}
