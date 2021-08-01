package com.sp.foodplaces;

public class ModelGeoPlaces {
    String Id;
    double Latitude, Longitude;

    public ModelGeoPlaces(String Id, double latitude, double longitude) {
        this.Id = Id;
        Latitude = latitude;
        Longitude = longitude;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }
}
