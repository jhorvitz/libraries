package edu.drexel.cs.jah473.us_cities;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.drexel.cs.jah473.sqlite.QueryManager;
import edu.drexel.cs.jah473.sqlite.QueryResults;

/**
 * Contains methods for working with US cities. This class utilizes <a
 * href="cs.drexel.edu/~jah473/resources/uscities.sqlite3">this US city
 * database</a>.
 * 
 * @author Justin Horvitz
 *
 */
public final class USCities {
    private static WeakReference<List<City>> allCities = new WeakReference<>(null);
    private static final Map<State, String> ABBREVIATIONS = new EnumMap<>(State.class);
    private static final Object ALL_CITIES_QUERY = new Object();
    private static final Object BY_STATE_QUERY = new Object();
    private static final Object BY_ZIP_QUERY = new Object();
    private static final Object GET_ZIP_QUERY = new Object();
    private static final Object GET_ZIPS_QUERY = new Object();
    private static final Object STATE_NAME_QUERY = new Object();
    private static final Object FOR_NAME_QUERY = new Object();

    static class AllCityRes extends QueryResults {
        String name;
        String state;
        String zip;
        double lat;
        double lon;
    };

    static class ZipRes extends QueryResults {
        String zip;
    }

    static class StateNameRes extends QueryResults {
        String stateName;
    }

    private static QueryManager qm;
    static {
        initQM();
    }

    private static void initQM() {
        String cl = "USCities.class";
        String path = USCities.class.getResource(cl).toString().substring(5);
        path = path.substring(0, path.length() - cl.length());
        String dbName = "uscities.sqlite3";
        File f = new File(path + dbName);
        if (!f.exists()) {
            File backUp = new File(path + "uscities_backup.sqlite3");
            try {
                Files.copy(backUp.toPath(), f.toPath());
            } catch (IOException e) {
                System.err.println("Failure to initialize connection to US cities database");
                return;
            }
        }
        try {
            qm = new QueryManager(path + dbName);
        } catch (SQLException e) {
            System.err.println("Failure to initialize connection to US cities database");
            return;
        }
        qm.addQuery(ALL_CITIES_QUERY, "select name, state, zip, latitude, longitude from cities", AllCityRes.class);
        qm.addQuery(BY_STATE_QUERY, "select name, state, zip, latitude, longitude from cities where state=?",
                AllCityRes.class);
        qm.addQuery(BY_ZIP_QUERY, "select name, state, zip, latitude, longitude from unfiltered where zip=? limit 1",
                AllCityRes.class);
        qm.addQuery(GET_ZIP_QUERY, "select zip from cities where name=? and state=? limit 1", ZipRes.class);
        qm.addQuery(GET_ZIPS_QUERY, "select zip from unfiltered where name=? and state=?", ZipRes.class);
        qm.addQuery(STATE_NAME_QUERY, "select state from abbrevs where abbreviation=? limit 1", StateNameRes.class);
        qm.addQuery(FOR_NAME_QUERY,
                "select name, state, zip, latitude, longitude from cities where name=? and state=? limit 1",
                AllCityRes.class);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                qm.close();
            }
        });
    }

    /**
     * Returns a list of all US cities.
     * 
     * @return a list of all US cities
     */
    public static List<City> allCities() {
        List<City> cities = allCities.get();
        if (cities == null) {
            AllCityRes res = qm.executeQuery(ALL_CITIES_QUERY);
            cities = new ArrayList<>();
            while (res.nextRow()) {
                cities.add(new City(res));
            }
        }
        allCities = new WeakReference<>(cities);
        return new ArrayList<>(cities);
    }

    /**
     * Returns a list of cities in the specified state(s)
     * 
     * @param states
     *            the state(s)
     * @return a list of cities in the specified state(s)
     */
    public static List<City> citiesIn(State... states) {
        if (states.length == 0) {
            return new ArrayList<>();
        }
        List<City> all = allCities.get();
        if (all != null) {
            List<State> statesList = Arrays.asList(states);
            return all.stream().filter(c -> statesList.contains(c.state)).collect(Collectors.toList());
        }
        List<City> cities = new ArrayList<>();
        for (State s : states) {
            AllCityRes res = qm.executeQuery(BY_STATE_QUERY, s);
            while (res.nextRow()) {
                cities.add(new City(res));
            }
        }
        return cities;
    }

    static City forZip(String zip) {
        City ret = City.CACHE.get(zip);
        if (ret != null) {
            return ret;
        }
        AllCityRes res = qm.executeQuery(BY_ZIP_QUERY, zip);
        if (!res.nextRow()) {
            return null;
        }
        return new City(res);
    }

    /**
     * Returns the zip code for the specified city. If there are multiple zip
     * codes associated with the city, one will be arbitrarily returned. To
     * retrieve all zip codes, use {@link #getZips(String, State)}.
     * 
     * @param city
     *            the city name
     * @param state
     *            the state
     * @return the zip code for the specified city, or {@code null} if the given
     *         city/state is invalid
     */
    public static String getZip(String city, State state) {
        ZipRes res = qm.executeQuery(GET_ZIP_QUERY, city, state.toString());
        if (!res.nextRow()) {
            return null;
        }
        return res.zip;
    }

    /**
     * Returns all zip codes associated with the specified city.
     * 
     * @param city
     *            the city name
     * @param state
     *            the state
     * @return a list of all zip codes associated with the specified city, or
     *         {@code null} if the given city is invalid
     */
    public static List<String> getZips(String city, State state) {
        ZipRes res = qm.executeQuery(GET_ZIPS_QUERY, city, state.toString());
        List<String> zips = new ArrayList<>();
        while (res.nextRow()) {
            zips.add(res.zip);
        }
        if (zips.isEmpty()) {
            return null;
        }
        return zips;
    }

    static String fullStateName(State state) {
        String stateName = ABBREVIATIONS.get(state);
        if (stateName == null) {
            StateNameRes res = qm.executeQuery(STATE_NAME_QUERY, state.toString());
            if (!res.nextRow()) {
                return null;
            }
            stateName = res.stateName;
            ABBREVIATIONS.put(state, stateName);
        }
        return stateName;
    }

    static City forName(String city, State state) {
        AllCityRes res = qm.executeQuery(FOR_NAME_QUERY, city, state.toString());
        if (!res.nextRow()) {
            return null;
        }
        return new City(res);
    }

    /* Private constructor to prevent instantiation */
    private USCities() {
    }
}
