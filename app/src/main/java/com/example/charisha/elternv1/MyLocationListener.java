package com.example.charisha.elternv1;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by e-Miracle workers.
 * Class to update the user's current location
 */

public class MyLocationListener implements LocationListener {
private Double lat;
    private Double longi;
    @Override
    public void onLocationChanged(Location loc) {
  //      editLocation.setText("");
  //      pb.setVisibility(View.INVISIBLE);

        lat = loc.getLatitude();
        longi = loc.getLongitude();
    }

    //@Override
    public void getLastKnownLocation(Location loc) {
        //      editLocation.setText("");
        //      pb.setVisibility(View.INVISIBLE);

        lat = loc.getLatitude();
        longi = loc.getLongitude();
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLongi() {
        return longi;
    }

    public void setLongi(Double longi) {
        this.longi = longi;
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
