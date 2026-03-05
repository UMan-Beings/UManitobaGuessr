package com.umanbeing.umg.services;
import org.springframework.stereotype.Service;
import com.umanbeing.umg.repos.RoundRepo;
import com.umanbeing.umg.models.Round;
import com.umanbeing.umg.models.Location;
import com.umanbeing.umg.models.Game;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoundService {
    private RoundRepo roundRepo;
    private LocationService locationService;

    public RoundService(RoundRepo roundRepo, LocationService locationService) {
        this.roundRepo = roundRepo;
        this.locationService = locationService;
    }

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
        rounds.forEach(round ->{roundRepo.save(round);});

        return rounds;
    }
}
