package edu.drexel.cs.jah473.us_cities;

import java.util.List;

import static org.junit.Assert.*;
import org.junit.Test;

import static edu.drexel.cs.jah473.us_cities.State.*;

public class CitiesTest {

    @Test
    public void forNameTest() {
        City c = City.forName("Newtown", PA);
        assertEquals("Newtown", c.getName());
        assertEquals(c.getState(), PA);
        assertEquals("Newtown, PA", c.toString());
        assertEquals("18940", c.getZip());
    }

    @Test
    public void forZipTest() {
        City c = City.forZip(18977);
        assertEquals("Washington Crossing", c.getName());
        assertEquals(c.getState(), PA);
        assertEquals("Washington Crossing, PA", c.toString());
        assertEquals("18977", c.getZip());
    }

    @Test
    public void citiesInTest() {
        List<City> inRhodeIsland = USCities.citiesIn(RI);
        assertTrue(inRhodeIsland.size() > 0);
        for (City c : inRhodeIsland) {
            assertEquals(RI, c.getState());
        }
    }

    @Test
    public void getZipTest() {
        String zip = USCities.getZip("Washington Crossing", PA);
        assertEquals("18977", zip);
    }

    @Test
    public void cacheTest() {
        City c1 = City.forName("Princeton", NJ);
        City c2 = City.forZip(c1.getZip());
        assertTrue(c1 == c2);
    }

    @Test
    public void fullStateNameTest() {
        assertEquals("Washington", WA.fullName());
        assertEquals("Michigan", MI.fullName());
    }
}
