package com.umanbeing.umg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.umanbeing.umg.controllers.dto.UserStatsResponse;
import com.umanbeing.umg.domain.GameState;
import com.umanbeing.umg.models.Game;
import com.umanbeing.umg.models.Guess;
import com.umanbeing.umg.models.Round;
import com.umanbeing.umg.models.User;
import com.umanbeing.umg.repos.GameRepo;
import com.umanbeing.umg.repos.projections.UserGameStatsProjection;
import com.umanbeing.umg.repos.projections.UserRoundStatsProjection;
import com.umanbeing.umg.services.GameService;
import com.umanbeing.umg.services.GuessService;
import com.umanbeing.umg.services.RoundService;
import com.umanbeing.umg.services.UserService;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {
    @Mock GameRepo gameRepo;
    @Mock RoundService roundService;
    @Mock GuessService guessService;
    @Mock UserService userService;

    @InjectMocks GameService gameService;

    @Test
    void createNewGame_whenTotalRoundsNonPositive_throwsBadRequest() {
        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> gameService.createNewGame(0, 30, null)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verifyNoInteractions(gameRepo, roundService, userService);
    }

    @Test
    void createNewGame_whenTotalRoundsBeyondLimit_throwsBadRequest() {
        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> gameService.createNewGame(Integer.MAX_VALUE, 30, null)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verifyNoInteractions(gameRepo, roundService, userService);
    }

    @Test
    void createNewGame_whenMaxTimerSecondsNegative_throwsBadRequest() {
        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> gameService.createNewGame(2, -1, null)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verifyNoInteractions(gameRepo, roundService, userService);
    }

    @Test
    void createNewGame_whenMaxTimerSecondsBeyondLimit_throwsBadRequest() {
        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> gameService.createNewGame(2, Integer.MAX_VALUE, null)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verifyNoInteractions(gameRepo, roundService, userService);
    }

    @Test
    void createNewGame_whenUserIdNull_createsGameAndRounds() {
        when(gameRepo.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<Round> rounds = List.of(new Round(), new Round());
        when(roundService.createRoundForGame(any(Game.class))).thenReturn(rounds);

        Game result = gameService.createNewGame(2, 30, null);

        assertNotNull(result);
        assertEquals(2, result.getTotalRounds());
        assertEquals(2, result.getRounds().size());
        assertTrue(result.getRounds().containsAll(rounds));
        assertEquals(30, result.getMaxTimerSeconds());
        assertEquals(GameState.GUESS, result.getGameState());
        assertEquals(1, result.getCurrentRoundNumber());
        assertEquals(0, result.getScore());
        assertFalse(result.isCompleted());
        assertNull(result.getUser());

        verifyNoInteractions(userService);
        verify(roundService).createRoundForGame(result);
        verify(gameRepo, times(2)).save(result);
    }

    @Test
    void createNewGame_whenUserIdGiven_setsUser() {
        when(gameRepo.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Long userId = 1L;
        
        User user = new User();
        when(userService.getUserById(userId)).thenReturn(user);

        List<Round> rounds = List.of(new Round(), new Round());
        when(roundService.createRoundForGame(any(Game.class))).thenReturn(rounds);

        Game result = gameService.createNewGame(2, 30, userId);

        assertNotNull(result);
        assertEquals(2, result.getTotalRounds());
        assertEquals(2, result.getRounds().size());
        assertTrue(result.getRounds().containsAll(rounds));
        assertEquals(30, result.getMaxTimerSeconds());
        assertEquals(GameState.GUESS, result.getGameState());
        assertEquals(1, result.getCurrentRoundNumber());
        assertEquals(0, result.getScore());
        assertFalse(result.isCompleted());
        assertSame(user, result.getUser());
        
        verify(userService).getUserById(userId);
        verify(roundService).createRoundForGame(result);
        verify(gameRepo, times(2)).save(result);
    }

    @Test
    void getGameById_whenMissingGameId_throwsBadRequest() {
        Long gameId = null;

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> gameService.getGameById(gameId)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(gameRepo);
    }

    @Test
    void getGameById_whenGameNotFound_throwsNotFound() {
        Long gameId = 1L;
        
        when(gameRepo.findById(gameId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> gameService.getGameById(gameId)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(gameRepo).findById(gameId);
    }

    @Test
    void getGameById_whenFound_returnsGame() {
        Long gameId = 1L;

        Game game = new Game();
        when(gameRepo.findById(gameId)).thenReturn(Optional.of(game));

        Game result = gameService.getGameById(gameId);

        assertSame(game, result);
        verify(gameRepo).findById(gameId);
    }

    @Test
    void submitGuess_whenNotGuessPhase_throwsConflict() {
        Long gameId = 1L;

        Game game = new Game();
        game.setGameState(GameState.REVEAL);

        when(gameRepo.findById(gameId)).thenReturn(Optional.of(game));

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> gameService.submitGuess(gameId, BigDecimal.ONE, BigDecimal.ONE, 5L)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verifyNoInteractions(guessService);
    }

    @Test
    void submitGuess_whenMissingCoordinateX_throwsBadRequest() {
        Long gameId = 1L;

        Game game = new Game();
        game.setGameState(GameState.GUESS);
        game.setCurrentRoundNumber(1);
        game.setRounds(List.of(new Round()));

        when(gameRepo.findById(gameId)).thenReturn(Optional.of(game));

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> gameService.submitGuess(gameId, null, BigDecimal.ONE, 5L)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(guessService);
    }

    @Test
    void submitGuess_whenMissingCoordinateY_throwsBadRequest() {
        Long gameId = 1L;

        Game game = new Game();
        game.setGameState(GameState.GUESS);
        game.setCurrentRoundNumber(1);
        game.setRounds(List.of(new Round()));

        when(gameRepo.findById(gameId)).thenReturn(Optional.of(game));

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> gameService.submitGuess(gameId, BigDecimal.ONE, null, 5L)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(guessService);
    }

    @Test
    void submitGuess_whenMissingGuessTime_throwsBadRequest() {
        Long gameId = 1L;

        Game game = new Game();
        game.setGameState(GameState.GUESS);
        game.setCurrentRoundNumber(1);
        game.setRounds(List.of(new Round()));

        when(gameRepo.findById(gameId)).thenReturn(Optional.of(game));

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> gameService.submitGuess(gameId, BigDecimal.ONE, BigDecimal.ONE, null)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(guessService);
    }

    @Test
    void submitGuess_whenValidInput_setsRevealStateAndSavesGame() {
        Long gameId = 1L;

        Game game = new Game();
        game.setGameState(GameState.GUESS);
        game.setMaxTimerSeconds(30);
        game.setCurrentRoundNumber(1);

        Round round = new Round();
        game.setRounds(List.of(round));

        when(gameRepo.findById(gameId)).thenReturn(Optional.of(game));
        when(gameRepo.save(game)).thenReturn(game);

        Game result = gameService.submitGuess(gameId, BigDecimal.ONE, BigDecimal.ONE, 10L);

        assertEquals(GameState.REVEAL, result.getGameState());

        verify(guessService).createGuess(eq(round), eq(BigDecimal.ONE), eq(BigDecimal.ONE), eq(10L));
        verify(gameRepo).save(game);
    }

    @Test
    void submitGuess_whenGuessTimeBeyondLimit_clampsToMaxTimer() {
        Long gameId = 1L;

        Game game = new Game();
        game.setGameState(GameState.GUESS);
        game.setMaxTimerSeconds(30);
        game.setCurrentRoundNumber(1);

        Round round = new Round();
        game.setRounds(List.of(round));

        when(gameRepo.findById(gameId)).thenReturn(Optional.of(game));
        when(gameRepo.save(game)).thenReturn(game);

        Game result = gameService.submitGuess(gameId, BigDecimal.ONE, BigDecimal.ONE, 45L);

        assertEquals(GameState.REVEAL, result.getGameState());

        verify(guessService).createGuess(eq(round), eq(BigDecimal.ONE), eq(BigDecimal.ONE), eq(30L));
        verify(gameRepo).save(game);
    }

    @Test
    void submitGuess_whenNoTimeLimit_clampsGuessTimeToMaxAllowed() {
        Long gameId = 1L;

        Game game = new Game();
        game.setGameState(GameState.GUESS);
        game.setMaxTimerSeconds(0);
        game.setCurrentRoundNumber(1);

        Round round = new Round();
        game.setRounds(List.of(round));

        when(gameRepo.findById(gameId)).thenReturn(Optional.of(game));
        when(gameRepo.save(game)).thenReturn(game);

        gameService.submitGuess(gameId, BigDecimal.ONE, BigDecimal.ONE, Long.MAX_VALUE);

        verify(guessService).createGuess(eq(round), eq(BigDecimal.ONE), eq(BigDecimal.ONE), eq(3600L));
    }

    @Test
    void timeout_whenTimerDisabled_throwsConflict() {
        Long gameId = 1L;

        Game game = new Game();
        game.setGameState(GameState.GUESS);
        game.setMaxTimerSeconds(0);

        when(gameRepo.findById(gameId)).thenReturn(Optional.of(game));

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> gameService.timeout(gameId)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verifyNoInteractions(guessService);
    }

    @Test
    void timeout_whenNotGuessPhase_throwsConflict() {
        Long gameId = 1L;

        Game game = new Game();
        game.setGameState(GameState.REVEAL);
        game.setMaxTimerSeconds(30);

        when(gameRepo.findById(gameId)).thenReturn(Optional.of(game));

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> gameService.timeout(gameId)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verifyNoInteractions(guessService);
    }

    @Test
    void timeout_whenValid_createsGuessWithNullCoordinatesThenSetsRevealAndSavesGame() {
        Long gameId = 1L;

        Round round = new Round();
        Game game = new Game();
        game.setMaxTimerSeconds(30);
        game.setGameState(GameState.GUESS);
        game.setCurrentRoundNumber(1);
        game.setRounds(List.of(round));

        when(gameRepo.findById(gameId)).thenReturn(Optional.of(game));
        when(gameRepo.save(game)).thenReturn(game);

        Game result = gameService.timeout(gameId);

        assertEquals(GameState.REVEAL, result.getGameState());

        verify(guessService).createGuess(eq(round), isNull(), isNull(), eq(30L));
        verify(gameRepo).save(game);
    }

    @Test
    void nextRound_whenNotRevealPhase_throwsConflict() {
        Long gameId = 1L;

        Game game = new Game();
        game.setGameState(GameState.GUESS);

        when(gameRepo.findById(gameId)).thenReturn(Optional.of(game));

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> gameService.nextRound(gameId)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(gameRepo, never()).save(any());
    }

    @Test
    void nextRound_whenGuessExists_addsScoreAndAdvancesRound() {
        Long gameId = 1L;

        Guess guess = new Guess();
        guess.setScore(50);

        Round round1 = new Round();
        round1.setGuess(guess);

        Round round2 = new Round();

        Game game = new Game();
        game.setGameState(GameState.REVEAL);
        game.setScore(50);
        game.setTotalRounds(2);
        game.setCurrentRoundNumber(1);
        game.setRounds(List.of(round1, round2));
        game.setCompleted(false);

        when(gameRepo.findById(gameId)).thenReturn(Optional.of(game));
        when(gameRepo.save(game)).thenReturn(game);

        Game result = gameService.nextRound(gameId);

        assertEquals(GameState.GUESS, result.getGameState());
        assertEquals(100, result.getScore());
        assertEquals(2, result.getCurrentRoundNumber());
        assertFalse(result.isCompleted());
        verify(gameRepo).save(game);
    }

    @Test
    void nextRound_whenGuessIsNull_doesNotChangeScoreAndAdvancesRound() {
        Long gameId = 1L;

        Round round1 = new Round();
        round1.setGuess(null);

        Round round2 = new Round();

        Game game = new Game();
        game.setGameState(GameState.REVEAL);
        game.setScore(50);
        game.setTotalRounds(2);
        game.setCurrentRoundNumber(1);
        game.setRounds(List.of(round1, round2));
        game.setCompleted(false);

        when(gameRepo.findById(gameId)).thenReturn(Optional.of(game));
        when(gameRepo.save(game)).thenReturn(game);

        Game result = gameService.nextRound(gameId);

        assertEquals(GameState.GUESS, result.getGameState());
        assertEquals(50, result.getScore());
        assertEquals(2, result.getCurrentRoundNumber());
        assertFalse(result.isCompleted());
        verify(gameRepo).save(game);
    }

    @Test
    void nextRound_whenLastRound_finishesGameAndSetsCompleted() {
        Long gameId = 1L;

        Guess guess = new Guess();
        guess.setScore(50);

        Round round = new Round();
        round.setGuess(guess);

        Game game = new Game();
        game.setGameState(GameState.REVEAL);
        game.setScore(50);
        game.setTotalRounds(1);
        game.setCurrentRoundNumber(1);
        game.setRounds(List.of(round));
        game.setCompleted(false);

        when(gameRepo.findById(gameId)).thenReturn(Optional.of(game));
        when(gameRepo.save(game)).thenReturn(game);

        Game result = gameService.nextRound(gameId);

        assertEquals(GameState.FINISHED, result.getGameState());
        assertEquals(100, result.getScore());
        assertTrue(result.isCompleted());
        assertEquals(1, result.getCurrentRoundNumber());
        verify(gameRepo).save(game);
    }

    @Test
    void getUserStats_whenGameStatsMissing_returnsAllZeros() {
        Long userId = 1L;

        when(gameRepo.getUserGameStats(userId)).thenReturn(null);
        when(gameRepo.getUserRoundStats(userId)).thenReturn(null);

        UserStatsResponse result = gameService.getUserStats(userId);

        assertEquals(0L, result.getTotalScore());
        assertEquals(0L, result.getTotalRounds());
        assertEquals(0L, result.getTotalGames());
        assertEquals(0.0, result.getAverageScore());
        assertEquals(0L, result.getTotalGuessTimeSeconds());
        assertEquals(0.0, result.getAverageGuessTimeSeconds());

        verify(gameRepo).getUserGameStats(userId);
        verify(gameRepo).getUserRoundStats(userId);
    }

    @Test
    void getUserStats_whenNoGamesPlayed_returnsAllZeros() {
        Long userId = 2L;

        UserGameStatsProjection gameStats = mock(UserGameStatsProjection.class);
        when(gameStats.getTotalGames()).thenReturn(0L);

        UserRoundStatsProjection roundStats = mock(UserRoundStatsProjection.class);

        when(gameRepo.getUserGameStats(userId)).thenReturn(gameStats);
        when(gameRepo.getUserRoundStats(userId)).thenReturn(roundStats);

        UserStatsResponse result = gameService.getUserStats(userId);

        assertEquals(0L, result.getTotalScore());
        assertEquals(0L, result.getTotalRounds());
        assertEquals(0L, result.getTotalGames());
        assertEquals(0.0, result.getAverageScore());
        assertEquals(0L, result.getTotalGuessTimeSeconds());
        assertEquals(0.0, result.getAverageGuessTimeSeconds());

        verify(gameRepo).getUserGameStats(userId);
        verify(gameRepo).getUserRoundStats(userId);
    }

    @Test
    void getUserStats_whenRoundStatsMissing_returnsGameStatsAndZeroTimes() {
        Long userId = 3L;

        UserGameStatsProjection gameStats = mock(UserGameStatsProjection.class);
        when(gameStats.getTotalScore()).thenReturn(500L);
        when(gameStats.getTotalRounds()).thenReturn(15L);
        when(gameStats.getTotalGames()).thenReturn(3L);
        when(gameStats.getAverageScore()).thenReturn(166.67);

        when(gameRepo.getUserGameStats(userId)).thenReturn(gameStats);
        when(gameRepo.getUserRoundStats(userId)).thenReturn(null);

        UserStatsResponse result = gameService.getUserStats(userId);

        assertEquals(500L, result.getTotalScore());
        assertEquals(15L, result.getTotalRounds());
        assertEquals(3L, result.getTotalGames());
        assertEquals(166.67, result.getAverageScore());
        assertEquals(0L, result.getTotalGuessTimeSeconds());
        assertEquals(0.0, result.getAverageGuessTimeSeconds());

        verify(gameRepo).getUserGameStats(userId);
        verify(gameRepo).getUserRoundStats(userId);
    }

    @Test
    void getUserStats_whenNoGuessesYet_returnsGameStatsAndZeroTimes() {
        Long userId = 4L;

        UserGameStatsProjection gameStats = mock(UserGameStatsProjection.class);
        when(gameStats.getTotalScore()).thenReturn(250L);
        when(gameStats.getTotalRounds()).thenReturn(8L);
        when(gameStats.getTotalGames()).thenReturn(2L);
        when(gameStats.getAverageScore()).thenReturn(125.0);

        UserRoundStatsProjection roundStats = mock(UserRoundStatsProjection.class);
        when(roundStats.getTotalGuessTimeSeconds()).thenReturn(0L);

        when(gameRepo.getUserGameStats(userId)).thenReturn(gameStats);
        when(gameRepo.getUserRoundStats(userId)).thenReturn(roundStats);

        UserStatsResponse result = gameService.getUserStats(userId);

        assertEquals(250L, result.getTotalScore());
        assertEquals(8L, result.getTotalRounds());
        assertEquals(2L, result.getTotalGames());
        assertEquals(125.0, result.getAverageScore());
        assertEquals(0L, result.getTotalGuessTimeSeconds());
        assertEquals(0.0, result.getAverageGuessTimeSeconds());

        verify(gameRepo).getUserGameStats(userId);
        verify(gameRepo).getUserRoundStats(userId);
    }

    @Test
    void getUserStats_whenAllDataPresent_returnsCombinedStats() {
        Long userId = 5L;

        UserGameStatsProjection gameStats = mock(UserGameStatsProjection.class);
        when(gameStats.getTotalScore()).thenReturn(900L);
        when(gameStats.getTotalRounds()).thenReturn(20L);
        when(gameStats.getTotalGames()).thenReturn(4L);
        when(gameStats.getAverageScore()).thenReturn(225.0);

        UserRoundStatsProjection roundStats = mock(UserRoundStatsProjection.class);
        when(roundStats.getTotalGuessTimeSeconds()).thenReturn(1200L);
        when(roundStats.getAverageGuessTimeSeconds()).thenReturn(60.0);

        when(gameRepo.getUserGameStats(userId)).thenReturn(gameStats);
        when(gameRepo.getUserRoundStats(userId)).thenReturn(roundStats);

        UserStatsResponse result = gameService.getUserStats(userId);

        assertEquals(900L, result.getTotalScore());
        assertEquals(20L, result.getTotalRounds());
        assertEquals(4L, result.getTotalGames());
        assertEquals(225.0, result.getAverageScore());
        assertEquals(1200L, result.getTotalGuessTimeSeconds());
        assertEquals(60.0, result.getAverageGuessTimeSeconds());

        verify(gameRepo).getUserGameStats(userId);
        verify(gameRepo).getUserRoundStats(userId);
    }

    @Test
    void getUserStats_whenUserIdNull_returnsAllZeros_andSkipsRepoCalls() {
        UserStatsResponse result = gameService.getUserStats(null);

        assertEquals(0L, result.getTotalScore());
        assertEquals(0L, result.getTotalRounds());
        assertEquals(0L, result.getTotalGames());
        assertEquals(0.0, result.getAverageScore());
        assertEquals(0L, result.getTotalGuessTimeSeconds());
        assertEquals(0.0, result.getAverageGuessTimeSeconds());

        verifyNoInteractions(gameRepo);
    }

    @Test
    void getUserStats_whenTotalGamesIsNull_returnsAllZeros() {
        Long userId = 6L;

        UserGameStatsProjection gameStats = mock(UserGameStatsProjection.class);
        when(gameStats.getTotalGames()).thenReturn(null);

        UserRoundStatsProjection roundStats = mock(UserRoundStatsProjection.class);

        when(gameRepo.getUserGameStats(userId)).thenReturn(gameStats);
        when(gameRepo.getUserRoundStats(userId)).thenReturn(roundStats);

        UserStatsResponse result = gameService.getUserStats(userId);

        assertEquals(0L, result.getTotalScore());
        assertEquals(0L, result.getTotalRounds());
        assertEquals(0L, result.getTotalGames());
        assertEquals(0.0, result.getAverageScore());
        assertEquals(0L, result.getTotalGuessTimeSeconds());
        assertEquals(0.0, result.getAverageGuessTimeSeconds());
    }

    @Test
    void getUserStats_whenTotalGuessTimeIsNull_returnsGameStatsAndZeroTimes() {
        Long userId = 7L;

        UserGameStatsProjection gameStats = mock(UserGameStatsProjection.class);
        when(gameStats.getTotalScore()).thenReturn(300L);
        when(gameStats.getTotalRounds()).thenReturn(10L);
        when(gameStats.getTotalGames()).thenReturn(2L);
        when(gameStats.getAverageScore()).thenReturn(150.0);

        UserRoundStatsProjection roundStats = mock(UserRoundStatsProjection.class);
        when(roundStats.getTotalGuessTimeSeconds()).thenReturn(null);

        when(gameRepo.getUserGameStats(userId)).thenReturn(gameStats);
        when(gameRepo.getUserRoundStats(userId)).thenReturn(roundStats);

        UserStatsResponse result = gameService.getUserStats(userId);

        assertEquals(300L, result.getTotalScore());
        assertEquals(10L, result.getTotalRounds());
        assertEquals(2L, result.getTotalGames());
        assertEquals(150.0, result.getAverageScore());
        assertEquals(0L, result.getTotalGuessTimeSeconds());
        assertEquals(0.0, result.getAverageGuessTimeSeconds());
    }
}