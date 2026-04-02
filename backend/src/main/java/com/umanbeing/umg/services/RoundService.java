package com.umanbeing.umg.services;

import com.umanbeing.umg.models.Game;
import com.umanbeing.umg.models.Location;
import com.umanbeing.umg.models.Round;
import com.umanbeing.umg.repos.RoundRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Service class for managing round-related operations.
 */
@Service
public class RoundService {

    private RoundRepo roundRepo;
    private LocationService locationService;

    public RoundService(RoundRepo roundRepo, LocationService locationService) {
        this.roundRepo = roundRepo;
        this.locationService = locationService;
    }

    /**
     * Creates rounds for a given game by selecting random locations and associating them with the game.
     * @param game The game for which rounds are to be created.
     * @return A list of created rounds.
     * @throws IllegalStateException if there are not enough locations in the repository to create the required number of rounds.
     */
    @Transactional
    public List<Round> createRoundForGame(Game game) {
        // Fetch all locations and pick random ones
        List<Location> randomLocations = locationService.getRandomLocations(game.getTotalRounds());
        List<Round> rounds = randomLocations.stream().map(location -> {
            Round round = new Round();
            round.setGame(game); // Ensure proper association
            round.setLocation(location);
            return round;
        }).collect(Collectors.toList());
        // For each round, set the round number based on its index in the list
        AtomicInteger roundNumber = new AtomicInteger(1);
        rounds.forEach(round -> {
            round.setRoundNumber(roundNumber.getAndIncrement());
        });
        // Save rounds
        rounds.forEach(round -> {
            roundRepo.save(round);
        });
        return rounds;
    }

}
