package com.example.charisha.elternv1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import me.tatarka.support.job.JobInfo;
import me.tatarka.support.job.JobScheduler;
import me.tatarka.support.os.PersistableBundle;

/**
 * Created by e-Miracle workers.
 * The main screen for elderly users
 */

public class MainElderScreenActivity extends Activity {

    public static final int REMINDERS_REQUEST = 4;
    public static final int FIND_TOILET = 5;
    public static final int INTERVAL_PERIODIC_LOCATION_UPDATE = 10000; // 15 minutes: 1000*60*15 = 9000000
    public static final int INTERVAL_PERIODIC_REMINDER_ALERT = 60000; // 15 minutes: 1000*60*15 = 9000000
    public static final String USER_NAME = "user_name";
    String emergencyNum;

    private static final int JOB_ID_LOCATION_UPDATE = 100;
    private static final int JOB_ID_REMINDER_ALERT = 200;
    // Scheduler for main thread
    private JobScheduler jobScheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_elder_screen_activity);

        // Main thread to update coordinates of the elderly
        jobScheduler = JobScheduler.getInstance(this);
        constructJob();
    }

    // Set periodic schedule job
    public void constructJob() {

        System.out.println("SaveSharedPreference.getUserName(this) " + SaveSharedPreference.getUserName(this));

        String userName = SaveSharedPreference.getUserName(this);
        PersistableBundle persistableBundle = new PersistableBundle();
        persistableBundle.putString(USER_NAME, userName);

        JobInfo.Builder locationBuilder = new JobInfo.Builder(JOB_ID_LOCATION_UPDATE, new ComponentName(this, RegularLocationService.class))
                .setPeriodic(INTERVAL_PERIODIC_LOCATION_UPDATE)
                .setExtras(persistableBundle)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true);

        JobInfo.Builder reminderBuilder = new JobInfo.Builder(JOB_ID_REMINDER_ALERT, new ComponentName(this, ReminderAlertService.class))
                .setPeriodic(INTERVAL_PERIODIC_REMINDER_ALERT)
                .setExtras(persistableBundle)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true);

        JobInfo jobInfoLocation = locationBuilder.build();
        JobInfo jobInfoReminder = reminderBuilder.build();
        jobScheduler.schedule(jobInfoLocation);
        jobScheduler.schedule(jobInfoReminder);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_elder_screen, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId())
        {
            //logout user
            case R.id.logout:
                SaveSharedPreference.setUserName(MainElderScreenActivity.this, "");
                jobScheduler.cancel(JOB_ID_LOCATION_UPDATE);
                jobScheduler.cancel(JOB_ID_REMINDER_ALERT);
                System.out.println("jobScheduler.cancel(JOB_ID);");
                Intent intent2 = new Intent(MainElderScreenActivity.this, MainActivity.class);
                startActivity(intent2);
                finish();
                break;
        }
        return true;
    }

    /*
    Mathod executed when reminders button is pressed
    Takes user to the reminders screen to view their reminders
     */
    public void remindersElder(View v)
    {
        Intent intent = new Intent(MainElderScreenActivity.this, RemindersElderlyActivity.class);

        startActivityForResult(intent, REMINDERS_REQUEST);
    }

    public void underConstruction(View v)
    {
        // Use the AlertDialog Builder utility to create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
// Set message and title for dialog (we can chain methods here!)
        builder.setTitle("Under Construction!").setMessage("This feature is under construction");
// Add a button to the dialog with an event handler for clicks (presses)
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
// Set the dialog to not be distrubed by the application
        builder.setCancelable(false);
// Create dialog and display it to the user
        builder.create().show();
    }

    /*
    Method called when emergency call button is pressed
     */
    public void emergencyCall(View v)
    {
        AlertDialog.Builder builder2 = new AlertDialog.Builder(MainElderScreenActivity.this);
        builder2.setTitle("Emergency?");
        builder2.setMessage("Do you wish to call you emergency contact?");
        builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //obatin usertype from database
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Elderly");
                query.whereEqualTo("userName", MainActivity.userName);

                query.getFirstInBackground(new GetCallback<ParseObject>() {

                    public void done(ParseObject object, ParseException e) {
                        if (object == null) {

                        } else {
                            //check if emergency number has been setuo
                            if (object.has("emergency")) {
                                emergencyNum = object.getString("emergency");
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:"+emergencyNum));
                                startActivity(callIntent);
                            }
                            //if no emergency number has been setup
                            else {
                                // Use the AlertDialog Builder utility to create the dialog
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainElderScreenActivity.this);
                                // Set message and title for dialog (we can chain methods here!)
                                builder.setTitle("Emergency number not setup").setMessage("Please tell carer to setup your emergency number");
                                 // Add a button to the dialog with an event handler for clicks (presses)
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                // Set the dialog to not be distrubed by the application
                                builder.setCancelable(false);
                                // Create dialog and display it to the user
                                builder.create().show();
                            }
                        }
                    }
                });
            }
        });
        //if changes have been made to appointment and uses choses not to save appointment
        builder2.setNegativeButton("No", new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder2.create().show();
    }

    public void findToilet(View v)
    {
        Intent intent = new Intent(MainElderScreenActivity.this, NewFindToiletActivity.class);

        startActivityForResult(intent, FIND_TOILET);
    }
}
