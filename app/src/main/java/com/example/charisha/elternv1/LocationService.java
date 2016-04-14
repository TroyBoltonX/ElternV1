package com.example.charisha.elternv1;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.tatarka.support.job.JobParameters;
import me.tatarka.support.job.JobService;
import me.tatarka.support.os.PersistableBundle;

/**
 * Created by e-Miracle workers.
 */
public class LocationService extends JobService {

    private static final long FALL_ASLEEP = 2000;
    private double latitude, longitude;
    private GoogleApiClient mLocationClient;
    private Location mCurrentLocation;
    protected String mLastUpdateTime;
    private String userName;

    private static final String TAG = "Notice: ";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        PersistableBundle persistableBundle = jobParameters.getExtras();
        userName = persistableBundle.getString(MainElderScreenActivity.USER_NAME);

        new UpdateLocationAsyncTask().execute(jobParameters);
        System.out.println("LocationService " + persistableBundle.getString(MainElderScreenActivity.USER_NAME));
        jobFinished(jobParameters, false);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {

        System.out.println("onStopJoonStopJobonStopJobonStopJobb");
        return false;
    }

    private void updateLocation(String userName) {

        mLocationClient = new GoogleApiClient.Builder(LocationService.this)
                .addApi(LocationServices.API)
                .build();

        mLocationClient.connect();
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mLocationClient);

        if (mCurrentLocation == null) {
            Log.i(TAG, "Unable to retrieve location services...");
        } else {
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            latitude = mCurrentLocation.getLatitude();
            longitude = mCurrentLocation.getLongitude();

            ParseObject elderLocation = new ParseObject("ElderLocation");
            elderLocation.put("userName", userName);
            elderLocation.put("trackTime", mLastUpdateTime);
            elderLocation.put("latitude", latitude);
            elderLocation.put("longitude", longitude);
            elderLocation.saveInBackground();
            System.out.println("Start UpdateLocationAsyncTask");
        }
    }

    private class UpdateLocationAsyncTask extends AsyncTask<JobParameters, String, Void> {

        @Override
        protected Void doInBackground(JobParameters... params) {

            mLocationClient = new GoogleApiClient.Builder(LocationService.this)
                    .addApi(LocationServices.API)
                    .build();

            mLocationClient.connect();
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mLocationClient);

            if (mCurrentLocation == null) {
                try {
                    Thread.sleep(FALL_ASLEEP);
                } catch (InterruptedException e) {
                    System.out.println("Thread.sleep 2 seconds");
                }
            } else {
                latitude = mCurrentLocation.getLatitude();
                longitude = mCurrentLocation.getLongitude();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
                mLastUpdateTime = simpleDateFormat.format(new Date());

                ParseObject elderLocation = new ParseObject("ElderLocation");
                elderLocation.put("userName", userName);
                elderLocation.put("trackTime", mLastUpdateTime);
                elderLocation.put("latitude", latitude);
                elderLocation.put("longitude", longitude);
                elderLocation.saveInBackground();
                System.out.println("Start UpdateLocationAsyncTask");
            }
            return null;
        }
    }
}
