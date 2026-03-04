package com.umanbeing.umg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

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
    @Mock GuessRepo guessRepo;

    @InjectMocks GuessService guessService;
    
    @Test
    void createGuess_whenMissingCoordinateX_setsNullCoordinatesNullDistanceZeroScoreThenSetsRoundGuess() {
        when(guessRepo.save(any(Guess.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Round round = new Round();
        Guess result = guessService.createGuess(round, null, BigDecimal.ONE, 5L);

        verify(guessRepo).save(result);
        assertNull(result.getGuessedX());
        assertNull(result.getGuessedY());
        assertNull(result.getDistanceMeters());
        assertEquals(5L, result.getGuessTimeSeconds());
        assertEquals(0, result.getScore());
        assertSame(round, result.getRound());
        assertSame(round.getGuess(), result);
    }

    @Test
    void createGuess_whenMissingCoordinateY_setsNullCoordinatesNullDistanceZeroScoreThenSetsRoundGuess() {
        when(guessRepo.save(any(Guess.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Round round = new Round();
        Guess result = guessService.createGuess(round, BigDecimal.ONE, null, 5L);

        verify(guessRepo).save(result);
        assertNull(result.getGuessedX());
        assertNull(result.getGuessedY());
        assertNull(result.getDistanceMeters());
        assertEquals(5L, result.getGuessTimeSeconds());
        assertEquals(0, result.getScore());
        assertSame(round, result.getRound());
        assertSame(round.getGuess(), result);
    }

    @Test
    void createGuess_whenDistanceWithinFullScoreRange_awardsFullScore() {
        when(guessRepo.save(any(Guess.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Location location = new Location();
        location.setCorX(BigDecimal.ONE);
        location.setCorY(BigDecimal.ONE);

        Round round = new Round();
        round.setLocation(location);

        Guess result = guessService.createGuess(round, BigDecimal.ONE, BigDecimal.ONE, 5L);

        verify(guessRepo).save(result);
        assertEquals(BigDecimal.ONE, result.getGuessedX());
        assertEquals(BigDecimal.ONE, result.getGuessedY());
        assertEquals(0, result.getDistanceMeters());
        assertEquals(5L, result.getGuessTimeSeconds());
        assertEquals(1000, result.getScore());
        assertSame(round, result.getRound());
        assertSame(round.getGuess(), result);
    }

    @Test
    void createGuess_whenDistanceWithinScaledScoreRange_awardsPartialScore() {
        when(guessRepo.save(any(Guess.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // full score value = 1000

        // full score range: 0-50px
        // partial score range: 50-350px (300px)
        
        // guess distance: 200px
        // distance from the full score range: 200-50 = 150px

        // so the guess is in the middle of the partial score range: 1 - (150 / 300) = 0.5
        // guess score: 1000 * 0.5 = 500

        Location location = new Location();
        location.setCorX(BigDecimal.ZERO);
        location.setCorY(BigDecimal.ZERO);

        Round round = new Round();
        round.setLocation(location);
        
        Guess result = guessService.createGuess(round, BigDecimal.valueOf(200), BigDecimal.valueOf(0), 5L);

        verify(guessRepo).save(result);
        assertEquals(BigDecimal.valueOf(200), result.getGuessedX());
        assertEquals(BigDecimal.valueOf(0), result.getGuessedY());
        assertEquals(200, result.getDistanceMeters());
        assertEquals(5L, result.getGuessTimeSeconds());
        assertEquals(500, result.getScore());
        assertSame(round, result.getRound());
        assertSame(round.getGuess(), result);
    }

    @Test
    void createGuess_whenCoordinatesOutsideScoreRange_awardsZeroScore() {
        when(guessRepo.save(any(Guess.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Location location = new Location();
        location.setCorX(BigDecimal.ZERO);
        location.setCorY(BigDecimal.ZERO);

        Round round = new Round();
        round.setLocation(location);
        
        Guess result = guessService.createGuess(round, BigDecimal.valueOf(0), BigDecimal.valueOf(9999), 5L);

        verify(guessRepo).save(result);
        assertEquals(BigDecimal.valueOf(0), result.getGuessedX());
        assertEquals(BigDecimal.valueOf(9999), result.getGuessedY());
        assertEquals(9999, result.getDistanceMeters());
        assertEquals(5L, result.getGuessTimeSeconds());
        assertEquals(0, result.getScore());
        assertSame(round, result.getRound());
        assertSame(round.getGuess(), result);
    }

    @Test
    void createGuess_whenRepoThrowsDataIntegrityViolationException_throwsConflict() {
        when(guessRepo.save(any(Guess.class))).thenThrow(new DataIntegrityViolationException("kablooie"));

        Location location = new Location();
        location.setCorX(BigDecimal.ZERO);
        location.setCorY(BigDecimal.ZERO);

        Round round = new Round();
        round.setLocation(location);
        
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> guessService.createGuess(round, BigDecimal.valueOf(0), BigDecimal.valueOf(9999), 5L)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(guessRepo).save(any(Guess.class));
    }
}
