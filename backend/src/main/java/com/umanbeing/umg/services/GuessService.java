package com.umanbeing.umg.services;

import com.umanbeing.umg.models.Guess;
import com.umanbeing.umg.models.Location;
import com.umanbeing.umg.models.Round;
import com.umanbeing.umg.repos.GuessRepo;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

/**
 * Service class for managing Guess entities contained in Rounds.
 */
@Service
public class GuessService {

    private static final double FULL_SCORE_DISTANCE = 50;
    private static final double MAX_DISTANCE = 350;
    private static final int MAX_SCORE = 1000;

    private final GuessRepo guessRepo;

    public GuessService(GuessRepo guessRepo) {
        this.guessRepo = guessRepo;
    }

    /**
     * Creates a new Guess entity for a given Round and guess coordinates.
     * @param round the Round entity
     * @param guessedX the guessed X coordinate
     * @param guessedY the guessed Y coordinate
     * @param guessTimeSeconds the time taken for the guess
     * @return the created Guess entity
     * @throws ResponseStatusException if the guessed coordinates are null, handled by GlobalExceptionHandler.
     */
    public Guess createGuess(Round round, BigDecimal guessedX, BigDecimal guessedY, Long guessTimeSeconds) {
        Guess guess = new Guess();
        guess.setRound(round);
        guess.setGuessTimeSeconds(guessTimeSeconds);
        if (guessedX == null || guessedY == null) {
            markGuessAsInvalid(guess);
        } else {
            setGuessAndScore(guess, round, guessedX, guessedY);
        }
        return saveGuess(round, guess);
    }
// =========================== Helper Methods ===========================

    /**
     * Marks a guess as invalid by setting its distance and score to null.
     * @param guess Object to mark as invalid.
     */
    private void markGuessAsInvalid(Guess guess) {
        guess.setDistanceMeters(null);
        guess.setScore(0);
    }

    /**
     * Sets the guess coordinates, distance, and score for a given guess and round.
     * @param guess Object to set the guess coordinates, distance, and score.
     * @param round Round entity for which the guess is being made.
     * @param guessedX Guessed X coordinate.
     * @param guessedY Guessed Y coordinate.
     */
    private void setGuessAndScore(Guess guess, Round round, BigDecimal guessedX, BigDecimal guessedY) {
        Location location = round.getLocation();
        Integer distance = calculateDistance(guessedX, guessedY, location.getCorX(), location.getCorY());
        guess.setGuessedX(guessedX);
        guess.setGuessedY(guessedY);
        guess.setDistanceMeters(distance);
        guess.setScore(calculateScore(distance));
    }

    /**
     * Saves a guess to the database with associated round.
     * @param round Round entity for which the guess is being made.
     * @param guess Object to save.
     * @return A saved guess object.
     * @throws ResponseStatusException if data integrity violation occurs, handled by GlobalExceptionHandler.
     */
    private Guess saveGuess(Round round, Guess guess) {
        try {
            Guess savedGuess = guessRepo.save(guess);
            round.setGuess(savedGuess);
            return savedGuess;
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Data integrity violation: " + exception.getMessage());
        }
    }

    /**
     * Calculates the Euclidean distance between two points in a 2D space.
     * @param x1 X coordinate of the first point.
     * @param y1 Y coordinate of the first point.
     * @param x2 X coordinate of the second point.
     * @param y2 Y coordinate of the second point.
     * @return The calculated distance as an integer.
     */
    private Integer calculateDistance(BigDecimal x1, BigDecimal y1, BigDecimal x2, BigDecimal y2) {
        return (int) Math.hypot(x1.doubleValue() - x2.doubleValue(), y1.doubleValue() - y2.doubleValue());
    }

    /**
     * Calculates the score based on the distance from the target.
     * @param distance The distance from the target.
     * @return The calculated score.
     */
    private int calculateScore(int distance) {
        int calculatedScore;
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
