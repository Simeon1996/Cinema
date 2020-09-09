package system.cinema.unit;

import org.junit.jupiter.api.Test;
import system.cinema.model.Cinema;
import system.cinema.model.City;
import system.cinema.model.Country;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NameNormalizationTest {
    @Test
    void testCinemaRepresentationNormalization()
    {
        assertEquals("Ring Mall", Cinema.Unit.RING_MALL.toString());
        assertEquals("Mall Of Sofia", Cinema.Unit.MALL_OF_SOFIA.toString());
        assertEquals("Bulgaria Mall", Cinema.Unit.BULGARIA_MALL.toString());
    }

    @Test
    void testCountryRepresentationNormalization()
    {
        assertEquals("Bulgaria", Country.Unit.BULGARIA.toString());
    }

    @Test
    void testCityRepresentationNormalization()
    {
        assertEquals("Sofia", City.Unit.SOFIA.toString());
        assertEquals("Stara Zagora", City.Unit.STARA_ZAGORA.toString());
        assertEquals("Smolyan", City.Unit.SMOLYAN.toString());
    }
}
