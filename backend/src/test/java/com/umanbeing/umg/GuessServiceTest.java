package com.umanbeing.umg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.umanbeing.umg.models.Guess;
import com.umanbeing.umg.models.Location;
import com.umanbeing.umg.models.Round;
import com.umanbeing.umg.repos.GuessRepo;
import com.umanbeing.umg.services.GuessService;

@ExtendWith(MockitoExtension.class)
class GuessServiceTest {
    @Mock 
    GuessRepo guessRepo;

    @InjectMocks 
    GuessService guessService;
    
    private Round round;
    private Location location;
    private long arbitraryGuessTime;

    @BeforeEach
    void setUp() {
        // new round and location for each test 
        round = new Round();
        location = new Location();
        round.setLocation(location);

        arbitraryGuessTime = 5L;

        // Mock save to return the same Guess object (lenient for tests that override this)
        lenient().when(guessRepo.save(any(Guess.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

// =========================== Invalid Coordinate Tests =========================== 
    @Test
    void missingX_setsCoordinatesNull() {
        Guess guess = guessService.createGuess(round, null, BigDecimal.ONE, arbitraryGuessTime);
        assertNull(guess.getGuessedX(), "Guessed X should be null when X coordinate is missing");
        assertNull(guess.getGuessedY(), "Guessed Y should be null when X coordinate is missing");
    }

    @Test
    void missingX_setsDistanceNull() {
        Guess guess = guessService.createGuess(round, null, BigDecimal.ONE, arbitraryGuessTime);
        assertNull(guess.getDistanceMeters(), "Distance should be null when X coordinate is missing");
        verify(guessRepo).save(any(Guess.class));
    }

    @Test
    void missingX_setsScoreZero() {
        Guess guess = guessService.createGuess(round, null, BigDecimal.ONE, arbitraryGuessTime);
        assertEquals(0, guess.getScore(), "Score should be 0 when X coordinate is missing");
    }

    @Test
    void missingY_setsCoordinatesNull() {
        Guess guess = guessService.createGuess(round, BigDecimal.ONE, null, arbitraryGuessTime);
        assertNull(guess.getGuessedX(), "Guessed X should be null when Y coordinate is missing");
        assertNull(guess.getGuessedY(), "Guessed Y should be null when Y coordinate is missing");
    }

    @Test
    void missingY_setsDistanceNull() {
        Guess guess = guessService.createGuess(round, BigDecimal.ONE, null, arbitraryGuessTime);
        assertNull(guess.getDistanceMeters(), "Distance should be null when Y coordinate is missing");
        verify(guessRepo).save(any(Guess.class));
    }
    
    @Test
    void missingY_setsScoreZero() {
        Guess guess = guessService.createGuess(round, BigDecimal.ONE, null, arbitraryGuessTime);
        assertEquals(0, guess.getScore(), "Score should be 0 when Y coordinate is missing");
    }

    @Test
    void missingXY_setsDistanceNull() {
        Guess guess = guessService.createGuess(round, null, null, arbitraryGuessTime);
        assertNull(guess.getDistanceMeters());
    }

    @Test
    void missingXY_setsScoreZero() {
        Guess guess = guessService.createGuess(round, null, null, arbitraryGuessTime);
        assertEquals(0, guess.getScore());
    }

// =========================== Round and Repository Tests ===========================
    @Test
    void guessHasRoundAssigned() {
        location.setCorX(BigDecimal.ZERO);
        location.setCorY(BigDecimal.ZERO);
        
        Guess guess = guessService.createGuess(round, BigDecimal.ONE, BigDecimal.ONE, arbitraryGuessTime);
        assertSame(round, guess.getRound(), "Round should be assigned to the guess in repository");
    }

    @Test
    void roundReferencesCreatedGuess() {
        location.setCorX(BigDecimal.ZERO);
        location.setCorY(BigDecimal.ZERO);
        
        Guess guess = guessService.createGuess(round, BigDecimal.ONE, BigDecimal.ONE, arbitraryGuessTime);
        assertSame(round.getGuess(), guess, "Round should reference the created guess in repository");
    }

    @Test
    void savesGuessToRepository() {
        location.setCorX(BigDecimal.ZERO);
        location.setCorY(BigDecimal.ZERO);
        
        Guess guess = guessService.createGuess(round, BigDecimal.ONE, BigDecimal.ONE, arbitraryGuessTime);
        verify(guessRepo).save(guess);
    }

// =========================== Scoring Tests ===========================
    @Test
    void perfectGuess_awardsFullScore() {
        location.setCorX(BigDecimal.ONE);
        location.setCorY(BigDecimal.ONE);

        Guess guess = guessService.createGuess(round, BigDecimal.ONE, BigDecimal.ONE, arbitraryGuessTime);
        assertEquals(0, guess.getDistanceMeters(), "Perfect guess should have 0 distance apart from the location");
        assertEquals(GuessService.MAX_SCORE, guess.getScore(), "Perfect guess should award full score");
    }

    @Test
    void guessWithinFullScoreRange_awardsFullScore() {
        location.setCorX(BigDecimal.ZERO);
        location.setCorY(BigDecimal.ZERO);

        Guess guess = guessService.createGuess(round, BigDecimal.valueOf(GuessService.FULL_SCORE_DISTANCE - 1), BigDecimal.ZERO, arbitraryGuessTime);
        assertEquals(GuessService.MAX_SCORE, guess.getScore(), "Guess within full score range should award MAX_SCORE");
    }

    @Test
    void guessAtFullScoreDistance_awardsFullScore() {
        location.setCorX(BigDecimal.ZERO);
        location.setCorY(BigDecimal.ZERO);
        
        Guess guess = guessService.createGuess(round, BigDecimal.valueOf(GuessService.FULL_SCORE_DISTANCE), BigDecimal.ZERO, arbitraryGuessTime);
        assertEquals(GuessService.MAX_SCORE, guess.getScore(), "Guess at full score distance should award MAX_SCORE");
    }

    @Test
    void guessJustOutsideFullScoreDistance_awardsPartialScore() {
        location.setCorX(BigDecimal.ZERO);
        location.setCorY(BigDecimal.ZERO);
        
        BigDecimal guessDistancePixels = BigDecimal.valueOf(GuessService.FULL_SCORE_DISTANCE + 1);

        Guess guess = guessService.createGuess(round, guessDistancePixels, BigDecimal.ZERO, arbitraryGuessTime);
        assertTrue(guess.getScore() < GuessService.MAX_SCORE, "Guess just outside full score distance boundary should NOT award MAX_SCORE");
    }

    @Test
    void withinScaledScoreRange_awardsCorrectPartialScore() {
        location.setCorX(BigDecimal.ZERO);
        location.setCorY(BigDecimal.ZERO);

        // full score range: 0-50px
        // partial score range: 50-350px (300px)
        
        // guess distance: 200px
        // distance from the full score range: 200-50 = 150px
        // so the guess is halfway through the partial score range: 1 - (150 / 300) = 0.5
        
        // full score value: GuessService.MAX_SCORE
        // expectedScore: GuessService.MAX_SCORE * 0.5 = 500
        BigDecimal guessDistancePixels = BigDecimal.valueOf(200);
        double scaledDistance = guessDistancePixels.doubleValue() - GuessService.FULL_SCORE_DISTANCE;
        double scoringRange = GuessService.MAX_DISTANCE - GuessService.FULL_SCORE_DISTANCE;
        int expectedScore = (int) Math.round(GuessService.MAX_SCORE * (1 - scaledDistance / scoringRange));

        Guess guess = guessService.createGuess(round, guessDistancePixels, BigDecimal.ZERO, arbitraryGuessTime);
        assertEquals(expectedScore, guess.getScore(), "Guess within scaled score range should award correct partial score");
    }

    @Test
    void guessBeyondScoreRange_awardsZeroScore() {
        location.setCorX(BigDecimal.ZERO);
        location.setCorY(BigDecimal.ZERO);

        Guess guess = guessService.createGuess(round, BigDecimal.valueOf(GuessService.MAX_DISTANCE + 1), BigDecimal.ZERO, arbitraryGuessTime);
        assertEquals(0, guess.getScore(), "Guess beyond MAX_DISTANCE should award 0 score");
    }

// =========================== Repository Exception Tests ===========================
    @Test
    void repoThrowsDataIntegrityViolationException_throwsConflict() {
        when(guessRepo.save(any(Guess.class))).thenThrow(new DataIntegrityViolationException("kablooie"));

        location.setCorX(BigDecimal.ZERO);
        location.setCorY(BigDecimal.ZERO);

        double arbitraryDistance = 100;
        BigDecimal arbitraryGuessCoordinate = BigDecimal.valueOf(arbitraryDistance);

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> guessService.createGuess(round, arbitraryGuessCoordinate, arbitraryGuessCoordinate, arbitraryGuessTime)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode(), "Exception status should be CONFLICT when repository throws DataIntegrityViolationException");
        verify(guessRepo).save(any(Guess.class));
    }   
    
}
