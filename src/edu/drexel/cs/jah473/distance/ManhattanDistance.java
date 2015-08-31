package edu.drexel.cs.jah473.distance;

import java.io.Serializable;

/**
 * k-dimensional Manhattan distance.
 * 
 * @author Justin Horvitz
 *
 */
public final class ManhattanDistance implements DistanceFunction, Serializable {

    private static final long serialVersionUID = -7050762961997869297L;
    private int k;

    /**
     * Constructs a new Manhattan distance function with the given number of
     * dimensions.
     * 
     * @param k
     *            the number of dimensions
     */
    public ManhattanDistance(int k) {
        if (k < 1) {
            throw new IllegalArgumentException("k must be greater than or equal to 1");
        }
        this.k = k;
    }

    /**
     * Calculates the distance between two points using the Manhattan distance
     * formula.
     * 
     * @param point1
     *            the first point
     * @param point2
     *            the second point
     * @return the Manhattan distance between point1 and point2
     */
    @Override
    public double distanceBetween(KDPoint point1, KDPoint point2) {
        double mdist = 0;
        for (int i = 0; i < k; i++) {
            mdist += Math.abs(point1.getCoord(i) - point2.getCoord(i));
        }
        return mdist;
    }

}
