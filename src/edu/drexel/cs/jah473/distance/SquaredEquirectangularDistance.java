package edu.drexel.cs.jah473.distance;

import java.io.Serializable;

/**
 * Squared equirectangular distance for latitude/longitude coordinates.
 * 
 * @author Justin Horvitz
 *
 */
public class SquaredEquirectangularDistance extends LatLonDist implements DistanceFunction, Serializable {

    private static final long serialVersionUID = 1492422017832642515L;

    /**
     * Estimates the squared unitless distance between two latitude/longitude
     * points using an equirectangular projection. Does not account for
     * wraparound.
     * 
     * @param point1
     *            the first point
     * @param point2
     *            the second point
     * @return the squared equirectangular distance between point1 and point2
     */
    @Override
    public double distanceBetween(KDPoint point1, KDPoint point2) {
        double lat1 = LatLonDist.getLat(point1);
        double lat2 = LatLonDist.getLat(point2);
        double dLat = lat1 - lat2;
        double avgLatRad = lat1 + lat2 / 2;
        double dLon = LatLonDist.getLon(point1) - LatLonDist.getLon(point2);
        dLon *= Math.cos(avgLatRad);
        return dLat * dLat + dLon * dLon;
    }

}
