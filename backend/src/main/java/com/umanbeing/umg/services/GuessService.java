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

@Service
public class GuessService {

    public static final double FULL_SCORE_DISTANCE = 50;
    public static final double MAX_DISTANCE = 350;
    public static final int MAX_SCORE = 1000;

    private final GuessRepo guessRepo;

    public GuessService(GuessRepo guessRepo) {
        this.guessRepo = guessRepo;
    }

    public Guess createGuess(Round round, BigDecimal guessedX, BigDecimal guessedY, Long guessTimeSeconds) {
        Guess guess = new Guess();
        guess.setRound(round);
        guess.setGuessTimeSeconds(guessTimeSeconds);

        if (guessedX == null || guessedY == null) {
            markGuessAsInvalid(guess);
        } else {
            setGuessAndScore(guess, round, guessedX, guessedY);
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

    private void markGuessAsInvalid(Guess guess) {
        guess.setDistanceMeters(null);
        guess.setScore(0);
    }

    private void setGuessAndScore(Guess guess, Round round, BigDecimal guessedX, BigDecimal guessedY) {
        Location location = round.getLocation();
        Integer distance = calculateDistance(guessedX, guessedY, location.getCorX(), location.getCorY());

        guess.setGuessedX(guessedX);
        guess.setGuessedY(guessedY);
        guess.setDistanceMeters(distance);
        guess.setScore(calculateScore(distance));
    }

}
