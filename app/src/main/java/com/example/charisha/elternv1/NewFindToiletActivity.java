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
public class
        NewFindToiletActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleMap googleMap;

    private GoogleApiClient mLocationClient;
    private LocationListener locationListener;
    private Location mCurrentLocation;

    protected LocationRequest locationRequest;
    protected boolean mRequestingLocationUpdates;
    protected String mLastUpdateTime;

    private ArrayList<Toilet> localToiletList = new ArrayList<>();

    private static final long FALL_ASLEEP = 2000;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final float CAMERA_ZOOM = 15;
    private static final int REQUEST_INTERVAL_ON_LOCATION_CHANGED = 10000; // 2 minute  120000
    private static final int REQUEST_FASTEST_INTERVAL_ON_LOCATION_CHANGED = 2000; // 1 minute  60000

    private static final String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    private static final String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    private static final String LOCATION_KEY = "location-key";
    private static final String TAG = "Notice: ";
    private static final String STATE_TOILETS = "state_toilets";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRequestingLocationUpdates = true;
        mLastUpdateTime = "";

        if (servicesOK()) {

            setContentView(R.layout.activity_new_find_toilet);
            System.out.println("servicesOK() " + servicesOK());
            if (initMap()) {

                buildGoogleApiClient();
                mLocationClient.connect();

            } else {
                Toast.makeText(this, "Map not connected!", Toast.LENGTH_SHORT).show();
            }
        }

        updateValuesFromBundle(savedInstanceState);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {

        Log.i(TAG, "Updating values from bundle" + (savedInstanceState != null));

        Toast.makeText(this, "Searching Toilet...", Toast.LENGTH_SHORT).show();

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

            if (savedInstanceState.keySet().contains(STATE_TOILETS)) {

                localToiletList = savedInstanceState.getParcelableArrayList(STATE_TOILETS);

                if (localToiletList == null) {
                    Log.i(TAG, "No toilet data saved");
                } else {
                    for (int i = 0; i < localToiletList.size(); i++) {

//                        MarkerOptions markerOptions = new MarkerOptions()
//                                .title(localToiletList.get(i).getToiletName() + "\n")
//                                .snippet(localToiletList.get(i).getToiletAddress() + " " + localToiletList.get(i).getToiletTown())
//                                .position(new LatLng(localToiletList.get(i).getToiletLatitude(),
//                                        localToiletList.get(i).getToiletLongitude()))
//                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//
//                        googleMap.addMarker(markerOptions);

                        MarkerOptions markerOptions = new MarkerOptions()
                                .title(localToiletList.get(i).getToiletAddress() + " " + localToiletList.get(i).getToiletTown() + "\n")
                                .position(new LatLng(localToiletList.get(i).getToiletLatitude(), localToiletList.get(i).getToiletLongitude()));

                        if(localToiletList.get(i).getAccessibleMale().equals("TRUE")&&
                                localToiletList.get(i).getAccessibleFemale().equals("TRUE")) {

                            markerOptions
                                    .snippet("Disable Male: Yes || " + "\n" + "Disable Female: Yes")
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            googleMap.addMarker(markerOptions);

                        } else {

                            if (localToiletList.get(i).getAccessibleMale().equals("TRUE")&&
                                    localToiletList.get(i).getAccessibleFemale().equals("FALSE")) {

                                markerOptions
                                        .snippet("Disable Male: Yes || " + "\n" + "Disable Female: No")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                                googleMap.addMarker(markerOptions);

                            } else if (localToiletList.get(i).getAccessibleMale().equals("FALSE")&&
                                    localToiletList.get(i).getAccessibleFemale().equals("TRUE")) {

                                markerOptions
                                        .snippet("Disable Male: No || " + "\n" + "Disable Female: Yes")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                googleMap.addMarker(markerOptions);
                            } else {

                                markerOptions
                                        .snippet("Disable Male: No || " + "\n" + "Disable Female: No")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                googleMap.addMarker(markerOptions);
                            }
                        }
                    }
                }
            }
        } else {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("VicToilet3");
            query.setLimit(4000);

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> toiletList, ParseException e) {

                    if (e == null) {
                        String toiletName, toiletAddress, toiletTown, accessibleMale, accessibleFemale;
                        double toiletLatitude, toiletLongitude;
                        Toilet toilet;

                        for (int i = 0; i < toiletList.size(); i++) {

                            toiletName = toiletList.get(i).getString("Name");
                            toiletAddress = toiletList.get(i).getString("Address1");
                            toiletTown = toiletList.get(i).getString("Town");
                            accessibleMale = toiletList.get(i).getString("AccessableMale");
                            accessibleFemale = toiletList.get(i).getString("AccessableFemale");
                            toiletLatitude = Double.parseDouble(toiletList.get(i).getString("Latitude"));
                            toiletLongitude = Double.parseDouble(toiletList.get(i).getString("Longitude"));

                            toilet = new Toilet(toiletName, toiletAddress, toiletTown, accessibleMale, accessibleFemale, toiletLatitude, toiletLongitude);
                            localToiletList.add(toilet);

                            MarkerOptions markerOptions = new MarkerOptions()
                                    .title(toiletAddress + " " + toiletTown + "\n")
                                    .position(new LatLng(toiletLatitude, toiletLongitude));

                            if(toiletList.get(i).getString("AccessableMale").equals("TRUE")&&
                                    toiletList.get(i).getString("AccessableFemale").equals("TRUE")) {

                                markerOptions
                                        .snippet("Disable Male: Yes || " + "\n" + "Disable Female: Yes")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                googleMap.addMarker(markerOptions);

                            } else {

                                if (toiletList.get(i).getString("AccessableMale").equals("TRUE")&&
                                        toiletList.get(i).getString("AccessableFemale").equals("FALSE")) {

                                    markerOptions
                                            .snippet("Disable Male: Yes || " + "\n" + "Disable Female: No")
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                                    googleMap.addMarker(markerOptions);

                                } else if (toiletList.get(i).getString("AccessableMale").equals("FALSE")&&
                                        toiletList.get(i).getString("AccessableFemale").equals("TRUE")) {

                                    markerOptions
                                            .snippet("Disable Male: No || " + "\n" + "Disable Female: Yes")
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                    googleMap.addMarker(markerOptions);
                                } else {

                                    markerOptions
                                            .snippet("Disable Male: No || " + "\n" + "Disable Female: No")
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                    googleMap.addMarker(markerOptions);
                                }
                            }
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
        getMenuInflater().inflate(R.menu.menu_new_find_toilet, menu);
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
            case R.id.toiletCurrentLocation:
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
                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.toiletMap);
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
        if (mLocationClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mLocationClient, locationListener);
        }
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

//                Toast.makeText(NewFindToiletActivity.this,
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
        savedInstanceState.putParcelableArrayList(STATE_TOILETS, localToiletList);
        super.onSaveInstanceState(savedInstanceState);
    }
}
