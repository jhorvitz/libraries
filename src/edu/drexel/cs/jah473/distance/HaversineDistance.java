package edu.drexel.cs.jah473.distance;

import java.io.Serializable;

/**
 * Haversine distance for latitude/longitude coordinates.
 * 
 * @author Justin Horvitz
 *
 */
public final class HaversineDistance extends LatLonDist implements DistanceFunction, Serializable {

    private static final long serialVersionUID = -8816579594679316314L;

    /**
     * Calculates the distance between two latitude/longitude points using the
     * Haversine distance formula.
     * 
     * @param point1
     *            the first point
     * @param point2
     *            the second point
     * @return the Haversine distance between point1 and point2
     */
    @Override
    public double distanceBetween(KDPoint point1, KDPoint point2) {
        double lat1 = LatLonDist.getLatRadians(point1);
        double lat2 = LatLonDist.getLatRadians(point2);
        double dlat = lat2 - lat1;
        double dlon = Math.toRadians(LatLonDist.getLon(point2) - LatLonDist.getLon(point1));
        double sinlat = Math.sin(dlat / 2);
        double sinlon = Math.sin(dlon / 2);
        double a = sinlat * sinlat + Math.cos(lat1) * Math.cos(lat2) * sinlon * sinlon;
        return 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

}