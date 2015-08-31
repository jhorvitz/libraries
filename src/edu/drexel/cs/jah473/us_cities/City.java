package edu.drexel.cs.jah473.us_cities;

import java.util.Map;
import java.util.WeakHashMap;

import edu.drexel.cs.jah473.distance.KDPoint;
import edu.drexel.cs.jah473.us_cities.USCities.AllCityRes;

/**
 * Represents a city in the United States including city, state, and zip code.
 * @author Justin Horvitz
 *
 */
public class City extends KDPoint {

    static final Map<String,City> CACHE = new WeakHashMap<>();
    private static final long serialVersionUID = 8484886685791907195L;
    /**
     * Returns a {@code City} instance for the specified city.
     * @param city the name of the city
     * @param state the state where the city is located
     * @return a {@code City} instance representing the specified city, or null if no city matches
     */
    public static City forName(String city, State state){
        return USCities.forName(city, state);
    }
    
    /**
     * Returns a {@code City} instance for the specified zip code.
     * @param zip the zip code
     * @return a {@code City} instance for the specified zip code, or null if the zip code is invalid
     */
    public static City forZip(String zip) {
        return USCities.forZip(zip);
    }
    
    /**
     * Returns a {@code City} instance for the specified zip code.
     * @param zip the zip code
     * @return a {@code City} instance for the specified zip code, or null if the zip code is invalid
     */
    public static City forZip(int zip){
        return City.forZip(String.valueOf(zip));
    }
    
    String name;
    State state;
    String zip;

    /* Package constructor used by USCities */
    City(AllCityRes res){
        super(res.lat,res.lon);
        name=res.name;
        state=State.valueOf(res.state);
        zip=res.zip;
        CACHE.put(zip, this);
    }

    /**
     * Returns a string representation of this city's location in the form (latitude,longitude).
     * @return a string representation of this city's location
     */
    public String getCoordsString(){
        return super.toString();
    }
    
    /**
     * Retrieves this city's latitude.
     * @return this city's latitude
     */
    public double getLat(){
        return coords[0];
    }

    /**
     * Retrieves this city's longitude.
     * @return this city's longitude
     */
    public double getLon(){
        return coords[1];
    }
    
    /**
     * Retrieves this city's name.
     * @return this city's name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns the state where this city is located.
     * @return the state where this city is located
     */
    public State getState() {
        return state;
    }
    
    /**
     * Retrieves this city's zip code.
     * @return this city's zip code
     */
    public String getZip() {
        return zip;
    }
    
    /**
     * Returns a string representation of this city in the form City, State.
     */
    @Override
    public String toString(){
        return name+", "+state;
    }
    
}