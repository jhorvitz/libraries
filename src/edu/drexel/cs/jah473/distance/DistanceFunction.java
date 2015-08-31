package edu.drexel.cs.jah473.distance;

/**
 * Interface to be implemented with a custom distance function.
 * 
 * @author Justin Horvitz
 *
 */
@FunctionalInterface
public interface DistanceFunction {

    /**
     * Calculates the distance between two points using the formula specific to
     * the implementing class.
     * 
     * @param point1
     *            the first point
     * @param point2
     *            the second point
     * @return the distance between point1 and point2
     */
    public double distanceBetween(KDPoint point1, KDPoint point2);
}
