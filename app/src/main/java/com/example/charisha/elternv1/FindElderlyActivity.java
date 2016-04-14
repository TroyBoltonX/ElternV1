package com.example.charisha.elternv1;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e-Miracle workers.
 */
public class FindElderlyActivity extends AppCompatActivity {

    GoogleMap googleMap;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final float CAMERA_ZOOM = 15;
    private ArrayList<Coordinate> coordinateList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        double latitude, longitude;
        String elderlyName, trackTime;

        if (servicesOK()) {
            setContentView(R.layout.activity_find_elderly);
            System.out.println("servicesOK() " + servicesOK());
            if (initMap()) {

                coordinateList = retrieveLatestUpdate();

                System.out.println("coordinateList == null " + coordinateList.isEmpty());
                if (coordinateList.isEmpty()) {
                    Toast.makeText(this, "No coordinate retrieved from cloud...", Toast.LENGTH_LONG).show();

                } else {
                    for (Coordinate coordinate: coordinateList) {

                        elderlyName = coordinate.getElderlyName();
                        trackTime = coordinate.getTrackTime();
                        latitude = coordinate.getLatitude();
                        longitude = coordinate.getLongitude();
                        goToLocation(latitude, longitude, CAMERA_ZOOM);

                        Toast.makeText(this, "Last Updated Location", Toast.LENGTH_SHORT).show();
                        MarkerOptions markerOptions = new MarkerOptions()
                                .title(elderlyName + "'s Last Updated Location: \n")
                                .snippet(trackTime)
                                .position(new LatLng(latitude, longitude));

                        googleMap.addMarker(markerOptions);
                    }
                }
            } else {
                Toast.makeText(this, "Map not connected!", Toast.LENGTH_SHORT).show();
            }

        } else {
            setContentView(R.layout.activity_main_carer_screen);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_find_elderly, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.mapTypeNormal:
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mapTypeSatellite:
                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeTerrain:
                googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.mapTypeHybrid:
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.mapTypeNone:
                googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean servicesOK() {

        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
            Dialog dialog =
                    GooglePlayServicesUtil.getErrorDialog(isAvailable, this, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "Can't connect to mapping service", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private boolean initMap() {
        if (googleMap == null) {
            SupportMapFragment supportMapFragment =
                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            googleMap = supportMapFragment.getMap();
        }
        return (googleMap != null);
    }

    private void goToLocation(double latitude, double longitude, float zoom) {
        LatLng latLng = new LatLng(latitude, longitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        googleMap.moveCamera(cameraUpdate);
    }

//    private void hideSoftKeyboard(View view) {
//        InputMethodManager inputMethodManager =
//                (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
//        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
//    }

    private ArrayList<Coordinate> retrieveLatestUpdate() {

        Coordinate coordinate;
        ArrayList<Coordinate> coordinates = new ArrayList<>();

        ParseQuery<ParseObject> queryElderly = ParseQuery.getQuery("ElderlyCarer");

        System.out.println("SaveSharedPreference.getUserName(this) " + SaveSharedPreference.getUserName(this));
        queryElderly.whereEqualTo("carerID", SaveSharedPreference.getUserName(this));

        try {
            List<ParseObject> elderlyList;
            elderlyList = queryElderly.find();

            if (elderlyList.isEmpty()) {
//                System.out.println();
                System.out.println("isEmptyisEmptyisEmptyisEmptyisEmptyisEmpty");

            } else {
                System.out.println("No isEmptyisEmptyisEmptyisEmptyisEmptyisEmpty");
                String elderlyName, trackTime;
                double latitude, longitude;
                for (ParseObject elder : elderlyList) {

                    elderlyName = elder.getString("elderlyID");
                    System.out.println("elderlyNameelderlyNameelderlyName" + elderlyName);
                    ParseQuery<ParseObject> queryCoordinate = ParseQuery.getQuery("ElderLocation");

                    queryCoordinate.orderByDescending("trackTime");
                    queryCoordinate.whereEqualTo("userName", elderlyName);

                    try {
                        ParseObject lastUpdate = queryCoordinate.getFirst();
                        trackTime = lastUpdate.getString("trackTime");
                        latitude = lastUpdate.getDouble("latitude");
                        longitude = lastUpdate.getDouble("longitude");

                        System.out.println("lastUpdate   " + trackTime + " " +latitude +" " +longitude);

                        coordinate = new Coordinate(elderlyName, trackTime, latitude, longitude);
                        coordinates.add(coordinate);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return coordinates;
    }

    public class Coordinate {

        double latitude;
        double longitude;
        String trackTime;
        String elderlyName;

        public Coordinate(String elderlyName, String trackTime, double latitude, double longitude) {
            this.elderlyName = elderlyName;
            this.trackTime = trackTime;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String getTrackTime() {
            return trackTime;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public String getElderlyName() {
            return elderlyName;
        }
    }
}
