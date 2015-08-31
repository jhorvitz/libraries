package edu.drexel.cs.jah473.distance;

import java.io.Serializable;

/**
 * k-dimensional Euclidean distance.
 * 
 * @author Justin Horvitz
 *
 */
public final class EuclideanDistance extends SquaredEuclideanDistance implements DistanceFunction, Serializable {

    private static final long serialVersionUID = -1369218471585380110L;

    /**
     * Constructs a new Euclidean distance function with the given number of
     * dimensions.
     * 
     * @param k
     *            the number of dimensions
     */
    public EuclideanDistance(int k) {
        super(k);
    }

    /**
     * Calculates the distance between two points using the Euclidean distance
     * formula.
     * 
     * @param point1
     *            the first point
     * @param point2
     *            the second point
     * @return the Euclidean distance between point1 and point2
     */
    @Override
    public double distanceBetween(KDPoint point1, KDPoint point2) {
        return Math.sqrt(super.distanceBetween(point1, point2));
    }
}