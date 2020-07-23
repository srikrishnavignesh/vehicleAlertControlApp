package com.ve.vehiclealertcontrolapp.location_updates;

import androidx.annotation.NonNull;

public class Loc {
    double latitude;
    double longitude;
    Loc()
    {

    }
    Loc(double latitude,double longitude)
    {
        this.latitude=latitude;
        this.longitude=longitude;
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

    @NonNull
    @Override
    public String toString() {
        return latitude+" "+longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
