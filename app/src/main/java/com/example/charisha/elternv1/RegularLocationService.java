package com.example.charisha.elternv1;

import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.tatarka.support.job.JobParameters;
import me.tatarka.support.job.JobService;
import me.tatarka.support.os.PersistableBundle;

/**
 * Created by e-Miracle workers.
 */
public class RegularLocationService extends JobService {

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

        GPSTracker gpsTracker = new GPSTracker(this);

        System.out.println("gpsTracker.canGetLocation()  " + gpsTracker.canGetLocation());

        if (gpsTracker.canGetLocation()) {

            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();

            writeLocationToParse(userName, latitude, longitude);
        }

        System.out.println("LocationService " + persistableBundle.getString(MainElderScreenActivity.USER_NAME));
        jobFinished(jobParameters, false);
        return false;
    }

    private void writeLocationToParse(String userName, double latitude, double longitude) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
        mLastUpdateTime = simpleDateFormat.format(new Date());

        ParseObject elderLocation = new ParseObject("ElderLocation");
        elderLocation.put("userName", userName);
        elderLocation.put("trackTime", mLastUpdateTime);
        elderLocation.put("latitude", latitude);
        elderLocation.put("longitude", longitude);
        elderLocation.saveInBackground();
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {

        System.out.println("onStopJoonStopJobonStopJobonStopJobb");
        return false;
    }
}
