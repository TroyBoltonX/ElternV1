package com.example.charisha.elternv1;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import com.mapquest.android.maps.AnnotationView;
import com.mapquest.android.maps.DefaultItemizedOverlay;
import com.mapquest.android.maps.GeoPoint;
import com.mapquest.android.maps.ItemizedOverlay;
import com.mapquest.android.maps.MapView;
import com.mapquest.android.maps.OverlayItem;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e-Miracle workers.
 * Class to implement the find hospital function
 */

public class FindHospitalActivity extends Activity implements LocationListener{

    private Double lat = 0.0;
    private Double longi = 0.0;

    private AnnotationView annotation;
    private OverlayItem[] point = new OverlayItem[160];
    private ArrayList<OverlayItem> points;
    private double latitude=0.0;
    private double longitude =0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_hospital);

        /*  LocationManager locationManager = (LocationManager)
         getSystemService(Context.LOCATION_SERVICE);
         LocationListener locationListener = new MyLocationListener();
         locationManager.requestLocationUpdates(
         LocationManager.GPS_PROVIDER, 5000, 10, locationListener);*/

        // this.getLastKnownLocation(Location loc);
        //gps tracker to get the latitude and longitude of the current location
        GPSTracker gps = new GPSTracker(this);
        if(gps.canGetLocation()){

            lat = gps.getLatitude();
            longi = gps.getLongitude();

            // \n is for new line
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + lat + "\nLong: " + longi, Toast.LENGTH_LONG).show();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

        MapView map = (MapView) findViewById(R.id.map);

        //add map event listener, when interacting with map, different situations happen
        map.addMapViewEventListener(new MapView.MapViewEventListener() {
            @Override
            public void moveStart(MapView mapView) {
            }

            @Override
            public void move(MapView mapView) {
            }

            @Override
            public void moveEnd(MapView mapView) {
            }

            //when touch the map, annotation hides.
            @Override
            public void touch(MapView mapView) {
                annotation.hide();
            }

            @Override
            public void longTouch(MapView mapView) {
            }

            @Override
            public void zoomStart(MapView mapView) {
            }

            @Override
            public void zoomEnd(MapView mapView) {
            }

            @Override
            public void mapLoaded(MapView mapView) {
            }
        });

        //set zoom
        map.getController().setZoom(13);
        //set center location of the map
        map.getController().setCenter(new GeoPoint(lat,
                longi));
        // map.getController().setCenter(new GeoPoint(latitude,
        //       longitude));
        map.setBuiltInZoomControls(true);

        // use a custom POI marker by referencing the bitmap file directly, // using the filename as the resource ID
        Drawable icon =
                getResources().getDrawable(R.drawable.mapicon);
        //Set the bounding for the drawable
        icon.setBounds(
                0,0, icon.getIntrinsicWidth(),
                icon.getIntrinsicHeight());
        final DefaultItemizedOverlay poiOverlay = new
                DefaultItemizedOverlay(icon);

        Drawable icon2 =
                getResources().getDrawable(R.drawable.currentlocation);
        //Set the bounding for the drawable
        icon.setBounds(
                0, 0, icon.getIntrinsicWidth(),
                icon.getIntrinsicHeight());
        final DefaultItemizedOverlay poiOverlay2 = new
                DefaultItemizedOverlay(icon2);

        // set GeoPoints and title/snippet to be used in the annotation view

        // retrieve hospital data from database, adding them into poiOverlay
        ParseQuery<ParseObject> query = ParseQuery.getQuery("HospitalVIC2");
        query.setLimit(1000);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> hospitallist, ParseException e) {

                if (hospitallist.size()>0) {
                    OverlayItem poi1 = new OverlayItem(new GeoPoint (lat,
                            longi), "your location","");
                    poiOverlay2.addItem(poi1);

                    for(int i = 0; i < hospitallist.size(); i++){

                        point[i] = new OverlayItem(
                                new GeoPoint (Double.parseDouble(hospitallist.get(i).getString("Latitude")),
                                        Double.parseDouble(hospitallist.get(i).getString("Longitude"))), hospitallist.get(i).getString("Hospname"), hospitallist.get(i).getString("AddressLine1")+" "+hospitallist.get(i).getString("AddressLine2"));
                        poiOverlay.addItem(point[i]);
                    }
                } else {
                    //Log.d("dish", "Error: " + e.getMessage());
                }
            }
        });

        //format annotation buble
        annotation = new AnnotationView(map);
        annotation.getTitle().setTextSize(15);
        annotation.getSnippet().setTextSize(15);
        annotation.getTitle().setWidth(500);
       // annotation.getSnippet().setMaxWidth(40);
        annotation.getTitle().setMaxLines(5);
        annotation.getTitle().setSingleLine(false);
        annotation.getSnippet().setSingleLine(false);
        annotation.getTitle().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        annotation.getSnippet().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
      //  annotation.getTitle().setGravity(Gravity.CENTER);
       // annotation.getSnippet().setGravity(Gravity.CENTER);

        // add a tap listener for the POI overlay
        poiOverlay.setTapListener(new
                                          ItemizedOverlay.OverlayTapListener() {
                                              @Override
                                              public void onTap(GeoPoint pt, MapView mapView) {
                                                  // when tapped, show the annotation for the overlayItem
                                                  int lastTouchedIndex = poiOverlay.getLastFocusedIndex();
                                                  if (lastTouchedIndex > -1) {
                                                      OverlayItem tapped =
                                                              poiOverlay.getItem(lastTouchedIndex);
                                                      annotation.showAnnotationView(tapped);
                                                  }
                                              }
                                          });

        poiOverlay2.setTapListener(new
                                           ItemizedOverlay.OverlayTapListener() {
                                               @Override
                                               public void onTap(GeoPoint pt, MapView mapView) {
                                                   // when tapped, show the annotation for the overlayItem
                                                   int lastTouchedIndex = poiOverlay2.getLastFocusedIndex();
                                                   if (lastTouchedIndex > -1) {
                                                       OverlayItem tapped =
                                                               poiOverlay2.getItem(lastTouchedIndex);
                                                       annotation.showAnnotationView(tapped);
                                                   }
                                               } });



        map.getOverlays().add(poiOverlay);
        map.getOverlays().add(poiOverlay2);
        System.out.println("lat###" +lat+", "+longi);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_find_hospital, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location loc) {
        //      editLocation.setText("");
        //      pb.setVisibility(View.INVISIBLE);
        System.out.println("got here");
        lat = loc.getLatitude();
        longi = loc.getLongitude();
    }

    public void getLastKnownLocation(Location loc) {
        //      editLocation.setText("");
        //      pb.setVisibility(View.INVISIBLE);
        System.out.println("got here");
        lat = loc.getLatitude();
        longi = loc.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    public static double microDegreesToDegrees(Double microDegrees) {
        return microDegrees * 1E6;
    }
}
