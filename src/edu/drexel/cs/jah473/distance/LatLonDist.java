package edu.drexel.cs.jah473.distance;

import java.io.Serializable;

/**
 * Latitude/longitude utilities.
 * 
 * @author Justin Horvitz
 *
 */
public abstract class LatLonDist implements DistanceFunction, Serializable {

    /**
     * The Earth's radius in kilometers.
     */
    public static final int EARTH_RADIUS_KM = 6_371;
    /**
     * The Earth's radius in miles.
     */
    public static final int EARTH_RADIUS_MI = 3_959;
    protected static final int LAT_INDEX = 0;
    protected static final int LON_INDEX = 1;
    private static final long serialVersionUID = 1799921046678618801L;

    protected static double getLat(KDPoint point) {
        return point.getCoord(LAT_INDEX);
    }

    protected static double getLatRadians(KDPoint point) {
        return Math.toRadians(getLat(point));
    }

    protected static double getLon(KDPoint point) {
        return point.getCoord(LON_INDEX);
    }

    protected static double getLonRadians(KDPoint point) {
        return Math.toRadians(getLon(point));
    }

    @Override
    public abstract double distanceBetween(KDPoint point1, KDPoint point2);

    /**
     * Calculates the distance, in kilometers, between two latitude/longitude
     * points.
     * 
     * @param point1
     *            the first point
     * @param point2
     *            the second point
     * @return the distance between the two points, in kilometers
     */
    public double distanceBetweenKM(KDPoint point1, KDPoint point2) {
        return distanceBetween(point1, point2) * EARTH_RADIUS_KM;
    }

    /**
     * Calculates the distance, in miles, between two latitude/longitude points.
     * 
     * @param point1
     *            the first point
     * @param point2
     *            the second point
     * @return the distance between the two points, in miles
     */
    public double distanceBetweenMI(KDPoint point1, KDPoint point2) {
        return distanceBetween(point1, point2) * EARTH_RADIUS_MI;
    }
}
