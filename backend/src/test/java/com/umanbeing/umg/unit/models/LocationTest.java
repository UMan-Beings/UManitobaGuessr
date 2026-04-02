package com.umanbeing.umg.unit.models;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.umanbeing.umg.models.Location;

class LocationTest {

    @Test
    void location_constructor_and_getters_work() {
        BigDecimal x = new BigDecimal("123.4567890123456");
        BigDecimal y = new BigDecimal("65.4321098765432");
        Location loc = new Location("Park", "url", x, y);

        assertEquals("Park", loc.getName());
        assertEquals("url", loc.getImageUrl());
        assertEquals(x, loc.getCorX());
        assertEquals(y, loc.getCorY());
    }

    @Test
    void location_no_arg_constructor_creates_empty_object() {
        Location loc = new Location();
        assertNotNull(loc);
    }

    @Test
    void location_setters_work() {
        Location loc = new Location();
        BigDecimal x = new BigDecimal("50.1234567890123");
        BigDecimal y = new BigDecimal("40.9876543210987");

        loc.setName("Museum");
        loc.setImageUrl("https://example.com/museum.jpg");
        loc.setCorX(x);
        loc.setCorY(y);

        assertEquals("Museum", loc.getName());
        assertEquals("https://example.com/museum.jpg", loc.getImageUrl());
        assertEquals(x, loc.getCorX());
        assertEquals(y, loc.getCorY());
    }

    @Test
    void location_id_can_be_set_and_retrieved() {
        Location loc = new Location("Park", "url", new BigDecimal("1.0"), new BigDecimal("2.0"));
        loc.setLocationId(42L);

        assertEquals(42L, loc.getLocationId());
    }
}
