package com.github.sdp.mediato.model;

public class Location {

    private double RADIUS = 100;
    private static final double EARTH_RADIUS = 6371; // Earth's radius in kilometers

    double latitude;
    double longitude;

    boolean valid;

    public Location(){valid = false;}

    public Location(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
        this.valid = true;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isInRadius(Location centerLocation) {
        return calculateDistance(centerLocation) <= RADIUS;
    }

    public double calculateDistance(Location that) {
        //Haversine formula for distance calculation
        double dLat = Math.toRadians(this.getLatitude() - that.getLatitude());
        double dLon = Math.toRadians(this.getLongitude() - that.getLongitude());

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(that.getLatitude())) * Math.cos(Math.toRadians(this.getLatitude())) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c;
        System.out.println("Estimated distance is " + distance);
        return distance;
    }

}
