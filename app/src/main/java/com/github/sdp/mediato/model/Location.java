package com.github.sdp.mediato.model;

public class Location {

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
}
