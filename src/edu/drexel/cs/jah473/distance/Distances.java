package edu.drexel.cs.jah473.distance;

/**
 * Class containing factory methods to instantiate various common distance
 * functions that implement the {@link DistanceFunction} functional interface.
 * These distance functions are compatible with the
 * {@link edu.drexel.cs.jah473.datastructures.KDTree KDTree} class. The
 * Distances class itself cannot be instantiated.
 * 
 * @author Justin Horvitz
 *
 */
public class Distances {

    /**
     * Returns a new Chebyshev's distance function with the given number of
     * dimensions
     * 
     * @param k
     *            the number of dimensions
     * @return a new Chebyshev's distance object
     */
    public static DistanceFunction chebyshev(int k) {
        return new ChebyshevDistance(k);
    }

    /**
     * Returns a new Equirectangular distance function.
     * 
     * @return a new Equirectangular distance object
     */
    public static LatLonDist equirectangular() {
        return new EquirectangularDistance();
    }

    /**
     * Returns a new Euclidean distance function with the given number of
     * dimensions.
     * 
     * @param k
     *            the number of dimensions
     * @return a new Euclidean distance object
     */
    public static DistanceFunction euclidean(int k) {
        return new EuclideanDistance(k);
    }

    /**
     * Returns a new Haversine distance function.
     * 
     * @return a new Haversine distance object
     */
    public static LatLonDist haversine() {
        return new HaversineDistance();
    }

    /**
     * Returns a new Manhattan distance function with the given number of
     * dimensions.
     * 
     * @param k
     *            the number of dimensions
     * @return a new Manhattan distance object
     */
    public static DistanceFunction manhattan(int k) {
        return new ManhattanDistance(k);
    }

    /**
     * Returns a new squared Equirectangular distance function.
     * 
     * @return a new squared Equirectangular distance object
     */
    public static LatLonDist sqEquirectangular() {
        return new SquaredEquirectangularDistance();
    }

    /**
     * Returns a new squared Euclidean distance function with the given number
     * of dimensions.
     * 
     * @param k
     *            the number of dimensions
     * @return a new squared Euclidean distance object
     */
    public static DistanceFunction sqEuclidean(int k) {
        return new SquaredEuclideanDistance(k);
    }

    /* Private constructor to prevent instantiation */
    private Distances() {
    }
}
