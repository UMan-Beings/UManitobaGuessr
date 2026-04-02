package com.umanbeing.umg.services;

import com.umanbeing.umg.models.Location;
import com.umanbeing.umg.repos.LocationRepo;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

/**
 * Service class for managing location-related operations.
 */
@Service
public class LocationService {

    private final LocationRepo locationRepo;
    private final SecureRandom random = new SecureRandom();

    public LocationService(LocationRepo locationRepo) {
        this.locationRepo = locationRepo;
    }

    /**
     * Retrieves a random selection of locations from the repository.
     * @param amount The number of random locations to retrieve.
     * @return A list of random locations.
     * @throws IllegalStateException if there are not enough locations in the repository.
     */
    public List<Location> getRandomLocations(int amount) {
        List<Location> allLocations = locationRepo.findAll();
        if (allLocations.size() < amount) {
            throw new IllegalStateException("Not enough locations.");
        }
        return random.ints(0, allLocations.size())
                .distinct()
                .limit(amount)
                .mapToObj(allLocations::get)
                .toList();
    }

}
