package com.umanbeing.umg.services;
import org.springframework.stereotype.Service;
import com.umanbeing.umg.repos.RoundRepo;
import com.umanbeing.umg.models.Round;
import com.umanbeing.umg.models.Location;
import com.umanbeing.umg.repos.LocationRepo;
import com.umanbeing.umg.models.Game;
import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoundService {
    private RoundRepo roundRepo;
    private LocationRepo locationRepo;
    private final SecureRandom random = new SecureRandom(); // Reuse SecureRandom instance

    public RoundService(RoundRepo roundRepo, LocationRepo locationRepo) {
        this.roundRepo = roundRepo;
        this.locationRepo = locationRepo;
    }

    public List<Round> getRoundsForGame(Game game) {
        return roundRepo.findAll().stream()
            .filter(round -> round.getGame().getGameId().equals(game.getGameId()))
            .collect(Collectors.toList());
    }

    @Transactional
    public List<Round> createRoundForGame(Game game) {
        // Fetch all locations and pick random ones
        List<Location> allLocations = locationRepo.findAll();
        if (allLocations.size() < game.getTotalRounds()) {
            throw new IllegalStateException("Not enough locations to create a round.");
        }

        List<Location> randomLocations = random.ints(0, allLocations.size())
            .distinct()
            .limit(game.getTotalRounds())
            .mapToObj(allLocations::get)
            .toList();
        
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
