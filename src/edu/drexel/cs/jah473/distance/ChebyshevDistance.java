package edu.drexel.cs.jah473.distance;

import java.io.Serializable;
import java.util.stream.IntStream;

/**
 * k-dimensional Chebyshev's distance.
 * 
 * @author Justin Horvitz
 *
 */
public final class ChebyshevDistance implements DistanceFunction, Serializable {

    private static final long serialVersionUID = 1281921458258099049L;
    private int k;

    /**
     * Constructs a new Chebyshev's distance function with the given number of
     * dimensions.
     * 
     * @param k
     *            the number of dimensions
     */
    public ChebyshevDistance(int k) {
        if (k < 1) {
            throw new IllegalArgumentException("k must be greater than or equal to 1");
        }
        this.k = k;
    }

    /**
     * Calculates the distance between two points using Chebyshev's distance
     * formula.
     * 
     * @param point1
     *            the first point
     * @param point2
     *            the second point
     * @return the Chebyshev's distance between point1 and point2
     */
    @Override
    public double distanceBetween(KDPoint point1, KDPoint point2) {
        return IntStream.range(0, k).mapToDouble(i -> Math.abs(point1.getCoord(i) - point2.getCoord(i))).max()
                .orElse(0);
    }

}