package edu.drexel.cs.jah473.distance;

import java.io.Serializable;

/**
 * Equirectangular distance for latitude/longitude coordinates.
 * 
 * @author Justin Horvitz
 *
 */
public final class EquirectangularDistance extends SquaredEquirectangularDistance implements DistanceFunction, Serializable {

    private static final long serialVersionUID = 2734650996358374876L;

    /**
     * Estimates the unitless distance between two latitude/longitude points
     * using an equirectangular projection. Does not account for wraparound.
     * 
     * @param point1
     *            the first point
     * @param point2
     *            the second point
     * @return the equirectangular distance between point1 and point2
     */
    @Override
    public double distanceBetween(KDPoint point1, KDPoint point2) {
        return Math.sqrt(super.distanceBetween(point1, point2));
    }
}