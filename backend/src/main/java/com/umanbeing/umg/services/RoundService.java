package com.umanbeing.umg.services;
import org.springframework.stereotype.Service;
import com.umanbeing.umg.repos.RoundRepo;
import com.umanbeing.umg.models.Round;
import com.umanbeing.umg.models.Location;
import com.umanbeing.umg.repos.LocationRepo;
import com.umanbeing.umg.models.Game;
import java.util.Random;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoundService {
    private RoundRepo roundRepo;
    private LocationRepo locationRepo;
    private final Random random = new Random(); // Reuse Random instance

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
            System.out.println("Creating round: " + round.getRoundNumber() + ", Game ID: " + game.getGameId() + ", Location ID: " + round.getLocation().getLocationId());
        });

        // Save rounds
        rounds.forEach(round -> {System.out.println("Saving round: " + round.getRoundNumber() + ", Game ID: " + game.getGameId() + ", Location ID: " + round.getLocation().getLocationId()); roundRepo.save(round);});

        // Log the total number of rounds created
        System.out.println("Total rounds created: " + rounds.size() + " for game: " + game.getGameId());

        return rounds;
    }

    public Round save(Round round) {
        return roundRepo.save(round);
    }

    public Round getRoundById(Long roundId) {
        return roundRepo.findById(roundId).orElse(null);
    }

    public void deleteRoundById(Long roundId) {
        roundRepo.deleteById(roundId);
    }
}
