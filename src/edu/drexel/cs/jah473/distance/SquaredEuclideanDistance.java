package edu.drexel.cs.jah473.distance;

import java.io.Serializable;

/**
 * k-dimensional squared Euclidean distance.
 * 
 * @author Justin Horvitz
 *
 */
public class SquaredEuclideanDistance implements DistanceFunction, Serializable {

    private static final long serialVersionUID = -3718638548355242836L;
    private int k;

    /**
     * Constructs a new squared Euclidean distance function with the given
     * number of dimensions.
     * 
     * @param k
     *            the number of dimensions
     */
    public SquaredEuclideanDistance(int k) {
        if (k < 1) {
            throw new IllegalArgumentException("k must be greater than or equal to 1");
        }
        ;
        this.k = k;
    }

    /**
     * Calculates the distance between two points using the squared Euclidean
     * distance formula.
     * 
     * @param point1
     *            the first point
     * @param point2
     *            the second point
     * @return the squared Euclidean distance between point1 and point2
     */
    @Override
    public double distanceBetween(KDPoint point1, KDPoint point2) {
        double dist = 0;
        for (int i = 0; i < k; i++) {
            double dK = point1.getCoord(i) - point2.getCoord(i);
            dist += dK * dK;
        }
        return dist;
    }
}
