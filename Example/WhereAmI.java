
import java.util.List;
import java.util.stream.Collectors;

import static edu.drexel.cs.jah473.args.Type.*;
import edu.drexel.cs.jah473.args.Arg;
import edu.drexel.cs.jah473.args.ArgParseParams;
import edu.drexel.cs.jah473.args.Args;
import edu.drexel.cs.jah473.datastructures.KDTree;
import edu.drexel.cs.jah473.distance.Distances;
import edu.drexel.cs.jah473.distance.KDPoint;
import edu.drexel.cs.jah473.distance.LatLonDist;
import edu.drexel.cs.jah473.us_cities.City;
import edu.drexel.cs.jah473.us_cities.State;
import edu.drexel.cs.jah473.us_cities.USCities;

public class WhereAmI {
    static double lat;
    static double lon;

    static int numCities = 5;
    static float radius = 15F;

    static final ArgParseParams params = new ArgParseParams() {

        @Override
        public Arg[] getRequiredArgs() {
            Arg[] req = new Arg[2];
            req[0] = new Arg("lat", DOUBLE, "your latitude", "lat");
            req[1] = new Arg("lon", DOUBLE, "your longitude", "lon");
            return req;
        }

        @Override
        public Arg[] getOptionalArgs() {
            Arg[] opt = new Arg[2];
            opt[0] = new Arg("numCities", POS_INT, "the number of nearby cities to find (default 5)", "-n", "--number");
            opt[1] = new Arg("radius", POS_FLOAT, "the radius within which to find cities (default 15.0 miles)", "-r",
                    "--radius");
            return opt;
        }

        @Override
        public String getUsageMessage() {
            return "where-am-i lat lon [options]\n"
                    + "Enter your latitude and longitude coordinates and find out where you are!\n"
                    + "Example usage: whereAmI 40.27 -74.9";
        }
    };

    public static void main(String[] args) {
        Args.parse(args, params, WhereAmI.class);
        KDPoint unknownLoc = new KDPoint(lat, lon);
        LatLonDist haversine = Distances.haversine();
        KDTree<City> citiesInUS = new KDTree<>(USCities.allCities(), 2, haversine::distanceBetweenMI);
        List<State> results = citiesInUS.kNNClassify(unknownLoc, numCities, City::getState);
        System.out.println();
        if (results.size() == 1) {
            System.out.println("You are most likely in: " + results.get(0).fullName());
        } else {
            System.out
                    .println("\nYou are probably in one of the following: " + results.stream().map(State::fullName).collect(Collectors.toList()));
        }
        System.out.println("\nThe " + numCities + " closest cities to your location are:");
        int i = 0;
        for (City c : citiesInUS.kNN(unknownLoc, numCities)) {
            System.out.println(++i + ". " + c);
        }
        System.out.println("\nCities within " + radius + " miles of you are: ");
        i = 0;
        for (City c : citiesInUS.radiusSearch(unknownLoc, radius)) {
            System.out.println(++i + ". " + c);
        }
        System.out.println();
    }
}
