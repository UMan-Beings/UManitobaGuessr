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

    public Guess createGuess(Round round, BigDecimal guessedX, BigDecimal guessedY, Long guessTimeSeconds) {
        Guess guess = new Guess();
        guess.setRound(round);
        guess.setGuessedX(guessedX);
        guess.setGuessedY(guessedY);
        guess.setGuessTimeSeconds(guessTimeSeconds);
        guess.setDistanceMeters(calculateDistance(guessedX, guessedY, round.getLocation().getCorX(), round.getLocation().getCorY()));
        guess.setScore(calculateScore(guessedX, guessedY, round.getLocation().getCorX(), round.getLocation().getCorY()));
        round.getGame().setScore(round.getGame().getScore() + guess.getScore());
        

        return guessRepo.save(guess);
    }

    private Integer calculateDistance(BigDecimal x1, BigDecimal y1, BigDecimal x2, BigDecimal y2) {
        return (int) Math.hypot(x1.doubleValue() - x2.doubleValue(), y1.doubleValue() - y2.doubleValue());
    }

    public Integer calculateScore(BigDecimal x1, BigDecimal y1, BigDecimal x2, BigDecimal y2) {

        double calculatedScore = 0;

        double distance = calculateDistance(x1, y1, x2, y2);

        double fullScoreDistance = 50;
        double maxDistance = 350;
        double maxScore = 1000;

        if (distance <= fullScoreDistance) {
            calculatedScore = maxScore;
        } else {
            double scaledDistance = distance - fullScoreDistance;
            double scoringRange = maxDistance - fullScoreDistance;

            calculatedScore = Math.max(0, Math.round(maxScore * (1 - scaledDistance / scoringRange)));
        }

        return (int) calculatedScore;
    }

}
