package com.umanbeing.umg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.umanbeing.umg.models.Location;
import com.umanbeing.umg.repos.LocationRepo;
import com.umanbeing.umg.services.LocationService;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {
    @Mock LocationRepo locationRepo;

    @InjectMocks LocationService locationService;

    @Test
    void getRandomLocations_whenNotEnoughLocations_throwsIllegalStateException() {
        when(locationRepo.findAll()).thenReturn(List.of());

        assertThrows(
            IllegalStateException.class, 
            () -> locationService.getRandomLocations(1)
        );
        
        verify(locationRepo).findAll();
    }

    @Test
    void getRandomLocations_whenRequestedAmountIsZero_returnsZeroLocations() {
        when(locationRepo.findAll()).thenReturn(List.of(new Location()));

        List<Location> result = locationService.getRandomLocations(0);
        
        verify(locationRepo).findAll();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getRandomLocations_whenEnoughLocations_returnsDistinctLocations() {
        Location location1 = new Location();
        location1.setLocationId(1L);

        Location location2 = new Location();
        location2.setLocationId(2L);

        when(locationRepo.findAll()).thenReturn(List.of(location1, location2));

        List<Location> result = locationService.getRandomLocations(2);
        
        verify(locationRepo).findAll();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(
            result.size(),
            result.stream().map(Location::getLocationId).distinct().count()
        );
    }

    @Test
    void getRandomLocations_whenMoreThanEnoughLocations_returnsDistinctLocations() {
        Location location1 = new Location();
        location1.setLocationId(1L);

        Location location2 = new Location();
        location2.setLocationId(2L);

        Location location3 = new Location();
        location3.setLocationId(3L);

        when(locationRepo.findAll()).thenReturn(List.of(location1, location2, location3));

        List<Location> result = locationService.getRandomLocations(2);
        
        verify(locationRepo).findAll();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(
            result.size(),
            result.stream().map(Location::getLocationId).distinct().count()
        );
    }
}
