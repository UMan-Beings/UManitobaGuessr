package com.umanbeing.umg.services;

import java.math.BigDecimal;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.umanbeing.umg.models.Guess;
import com.umanbeing.umg.models.Location;
import com.umanbeing.umg.repos.GuessRepo;
import com.umanbeing.umg.models.Round;
import java.lang.Math;

@Service
public class GuessService {

    private final GuessRepo guessRepo;
    
    private static final double FULL_SCORE_DISTANCE = 50;
    private static final double MAX_DISTANCE = 350;
    private static final int MAX_SCORE = 1000;

    public GuessService(GuessRepo guessRepo) {
        this.guessRepo = guessRepo;
    }

    public Guess save(Guess guess) {
        return guessRepo.save(guess);
    }

    public Guess createGuess(Round round, BigDecimal guessedX, BigDecimal guessedY, Long guessTimeSeconds) {
        Guess guess = new Guess();
        guess.setRound(round);
        guess.setGuessedX(guessedX);
        guess.setGuessedY(guessedY);
        guess.setGuessTimeSeconds(guessTimeSeconds);

        if (guessedX == null || guessedY == null) {
            guess.setDistanceMeters(null);
            guess.setScore(0);
        } else {
            Location location = round.getLocation();
            Integer distance = calculateDistance(guessedX, guessedY, location.getCorX(), location.getCorY());
            guess.setDistanceMeters(distance);
            guess.setScore(calculateScore(distance));
        }

        try {
            Guess savedGuess = guessRepo.save(guess);
            round.setGuess(savedGuess);
            return savedGuess;
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    private Integer calculateDistance(BigDecimal x1, BigDecimal y1, BigDecimal x2, BigDecimal y2) {
        return (int) Math.hypot(x1.doubleValue() - x2.doubleValue(), y1.doubleValue() - y2.doubleValue());
    }

    private int calculateScore(int distance) {
        int calculatedScore = 0;

        if (distance <= FULL_SCORE_DISTANCE) {
            calculatedScore = MAX_SCORE;
        } else {
            double scaledDistance = distance - FULL_SCORE_DISTANCE;
            double scoringRange = MAX_DISTANCE - FULL_SCORE_DISTANCE;

            calculatedScore = Math.max(
                0, 
                (int) Math.round(MAX_SCORE * (1 - scaledDistance / scoringRange))
            );
        }

        return calculatedScore;
    }

}
