package com.umanbeing.umg.services;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.umanbeing.umg.repos.RoundRepo;
import com.umanbeing.umg.models.Round;
import com.umanbeing.umg.models.Location;
import com.umanbeing.umg.repos.LocationRepo;
import com.umanbeing.umg.models.Game;
import java.util.List;
import java.util.Random;

@Service
public class RoundService {
    @Autowired
    private RoundRepo roundRepo;

    @Autowired
    private LocationRepo locationRepo;

    public List<Round> createRoundForGame(Game game) {
        // Round round = new Round();
        // round.setGame(game);

        
        // Fetch all locations and pick random ones
        List<Location> allLocations = locationRepo.findAll();
        if (allLocations.size() < game.getTotalRounds()) {
            throw new IllegalStateException("Not enough locations to create a round.");
        }

        Random random = new Random();
        List<Location> randomLocations = random.ints(0, allLocations.size())
            .distinct()
            .limit(game.getTotalRounds())
            .mapToObj(allLocations::get)
            .toList();

        List<Round> rounds = randomLocations.stream().map(location -> {
            Round round = new Round();
            round.setGame(game);
            round.setLocation(location);
            return round;
        }).toList();
        rounds.forEach(this::save);
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
