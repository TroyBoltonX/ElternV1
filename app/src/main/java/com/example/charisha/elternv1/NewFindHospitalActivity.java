package com.example.charisha.elternv1;

import android.app.Dialog;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by e-Miracle workers.
 */
public class NewFindHospitalActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleMap googleMap;

    private GoogleApiClient mLocationClient;
    private LocationListener locationListener;
    private Location mCurrentLocation;

    protected LocationRequest locationRequest;
    protected boolean mRequestingLocationUpdates;
    protected String mLastUpdateTime;

    private ArrayList<Hospital> localHospitalList = new ArrayList<>();

    private static final long FALL_ASLEEP = 2000;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final float CAMERA_ZOOM = 15;
    private static final int REQUEST_INTERVAL_ON_LOCATION_CHANGED = 10000; // 2 minute  120000
    private static final int REQUEST_FASTEST_INTERVAL_ON_LOCATION_CHANGED = 2000; // 1 minute  60000

    private static final String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    private static final String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    private static final String LOCATION_KEY = "location-key";
    private static final String TAG = "Notice: ";
    private static final String STATE_HOSPITALS = "state_hospitals";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRequestingLocationUpdates = true;
        mLastUpdateTime = "";

        if (servicesOK()) {

            setContentView(R.layout.activity_new_find_hospital);
            System.out.println("servicesOK() " + servicesOK());
            if (initMap()) {

                buildGoogleApiClient();
                mLocationClient.connect();

//                Toast.makeText(this, "Searching Hospital..." + mLocationClient.isConnected(), Toast.LENGTH_SHORT).show();
//                if (mLocationClient.isConnected()) {
//                    if (showCurrentLocation() != null) {
//                        Toast.makeText(this, "Searching Hospital...", Toast.LENGTH_SHORT).show();
//                    }
//                }
            } else {
                Toast.makeText(this, "Map not connected!", Toast.LENGTH_SHORT).show();
            }
        }

        updateValuesFromBundle(savedInstanceState);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {

        Log.i(TAG, "Updating values from bundle");

        Toast.makeText(this, "Searching Hospital...", Toast.LENGTH_SHORT).show();

        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and
            // make sure that the Start Updates and Stop Updates buttons are
            // correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
            }

            // Update the value of mCurrentLocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocationis not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }

            if (savedInstanceState.keySet().contains(STATE_HOSPITALS)) {

                localHospitalList = savedInstanceState.getParcelableArrayList(STATE_HOSPITALS);

                if (localHospitalList == null) {
                    Log.i(TAG, "No hospital data saved");
                } else {

                    for (int i = 0; i < localHospitalList.size(); i++) {

                        MarkerOptions markerOptions = new MarkerOptions()
                                .title(localHospitalList.get(i).getHospitalName() + "\n")
                                .snippet(localHospitalList.get(i).getAddressLine1() + " " + localHospitalList.get(i).getAddressLine2())
                                .position(new LatLng(localHospitalList.get(i).getHospitalLatitude(),
                                        localHospitalList.get(i).getHospitalLongitude()))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                        googleMap.addMarker(markerOptions);
                    }
                }
            }
        } else {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("HospitalVIC2");
            query.setLimit(1000);

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> hospitalList, ParseException e) {

                    if (e == null) {

                        String hospitalName, hospitalAddress1, hospitalAddress2;
                        double hospitalLatitude, hospitalLongitude;
                        Hospital hospital;

                        for (int i = 0; i < hospitalList.size(); i++) {

                            hospitalName = hospitalList.get(i).getString("Hospname");
                            hospitalAddress1 = hospitalList.get(i).getString("AddressLine1");
                            hospitalAddress2 = hospitalList.get(i).getString("AddressLine2");
                            hospitalLatitude = Double.parseDouble(hospitalList.get(i).getString("Latitude"));
                            hospitalLongitude = Double.parseDouble(hospitalList.get(i).getString("Longitude"));

                            hospital = new Hospital(hospitalName, hospitalAddress1, hospitalAddress2, hospitalLatitude, hospitalLongitude);
                            localHospitalList.add(hospital);

                            MarkerOptions markerOptions = new MarkerOptions()
                                    .title(hospitalName + "\n")
                                    .snippet(hospitalAddress1 + " " + hospitalAddress2)
                                    .position(new LatLng(hospitalLatitude, hospitalLongitude))
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                            googleMap.addMarker(markerOptions);
                        }
                    } else {
                        Log.i(TAG, "Error occurred...");
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_find_hospital, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

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
            case R.id.hospitalCurrentLocation:
                showCurrentLocation();
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
                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.hospitalMap);
            googleMap = supportMapFragment.getMap();
        }
        return (googleMap != null);
    }

    private void goToLocation(double latitude, double longitude, float zoom) {

        LatLng latLng = new LatLng(latitude, longitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        googleMap.moveCamera(cameraUpdate);
    }

    public Location showCurrentLocation() {

        Location currentLocation = LocationServices.FusedLocationApi
                .getLastLocation(mLocationClient);

        if (currentLocation == null) {

        } else {

            LatLng latLng = new LatLng(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude()
            );

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, CAMERA_ZOOM);
            googleMap.animateCamera(cameraUpdate);
        }
        return currentLocation;
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        createLocationRequest();
    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(REQUEST_INTERVAL_ON_LOCATION_CHANGED);
        locationRequest.setFastestInterval(REQUEST_FASTEST_INTERVAL_ON_LOCATION_CHANGED);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mLocationClient, locationRequest, locationListener
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLocationClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLocationClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (mLocationClient.isConnected()) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(
//                    mLocationClient, locationListener);
//        }
    }

    @Override
    protected void onStop() {
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mLocationClient);

        if (mCurrentLocation == null) {
            Toast.makeText(this, "Unable to retrieve Location servcices...", Toast.LENGTH_SHORT).show();
        } else {

            double latitude = mCurrentLocation.getLatitude();
            double longitude = mCurrentLocation.getLongitude();
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

            goToLocation(latitude, longitude, CAMERA_ZOOM);

            MarkerOptions markerOptions = new MarkerOptions()
                    .title(SaveSharedPreference.getUserName(this) + "'s Current Location\n")
                    .snippet(mLastUpdateTime)
                    .position(new LatLng(latitude, longitude))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

            googleMap.addMarker(markerOptions);
        }

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                mCurrentLocation = location;
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

//                Toast.makeText(NewFindHospitalActivity.this,
//                        "Current Location Changed To: \n" + location.getLatitude() + ", " +
//                                location.getLongitude(), Toast.LENGTH_SHORT).show();

//                goToLocation(location.getLatitude(), location.getLongitude(), CAMERA_ZOOM);
            }
        };

        if (mRequestingLocationUpdates && mLocationClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mLocationClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        savedInstanceState.putParcelableArrayList(STATE_HOSPITALS, localHospitalList);
        super.onSaveInstanceState(savedInstanceState);
    }
}
