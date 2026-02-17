package com.umanbeing.umg.services;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import com.umanbeing.umg.models.Guess;
import com.umanbeing.umg.repos.GuessRepo;
import com.umanbeing.umg.models.Round;

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
        return guessRepo.save(guess);
    }

}
