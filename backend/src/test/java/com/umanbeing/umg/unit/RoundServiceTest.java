package com.umanbeing.umg.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.umanbeing.umg.models.Game;
import com.umanbeing.umg.models.Location;
import com.umanbeing.umg.models.Round;
import com.umanbeing.umg.repos.RoundRepo;
import com.umanbeing.umg.services.LocationService;
import com.umanbeing.umg.services.RoundService;

@ExtendWith(MockitoExtension.class)
class RoundServiceTest {
    @Mock RoundRepo roundRepo;
    @Mock LocationService locationService;

    @InjectMocks RoundService roundService;

    @Test
    void createRoundForGame_whenValidInput_returnsRounds() {
        Game game = new Game();
        game.setTotalRounds(2);

        Location location1 = new Location();
        Location location2 = new Location();

        when(locationService.getRandomLocations(2)).thenReturn(List.of(location1, location2));
        when(roundRepo.save(any(Round.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<Round> result = roundService.createRoundForGame(game);
        
        verify(locationService).getRandomLocations(2);
        verify(roundRepo, times(2)).save(any(Round.class));

        assertNotNull(result);
        assertEquals(2, result.size());

        Round round1 = result.get(0);
        assertEquals(1, round1.getRoundNumber());
        assertSame(game, round1.getGame());
        assertSame(location1, round1.getLocation());

        Round round2 = result.get(1);
        assertEquals(2, round2.getRoundNumber());
        assertSame(game, round2.getGame());
        assertSame(location2, round2.getLocation());
    }
}
