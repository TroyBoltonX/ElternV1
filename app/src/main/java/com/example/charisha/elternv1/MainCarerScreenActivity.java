package com.example.charisha.elternv1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by e-Miracle workers.
 * The main screen for carer users
 */

public class MainCarerScreenActivity extends Activity {

    public static final int REMINDERS_REQUEST = 4;
    public static final int FIND_HOSPITAL = 5;
    public static final int FIND_ELDERLY = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_carer_screen);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_carer_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
      //  if (id == R.id.action_settings) {
     //       return true;
      //  }

        switch(item.getItemId())
        {
            //got to settings screen
            case R.id.addElder:
                Intent intent = new Intent(MainCarerScreenActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;

            //logout user
            case R.id.logout:
                SaveSharedPreference.setUserName(MainCarerScreenActivity.this, "");
                Intent intent2 = new Intent(MainCarerScreenActivity.this, MainActivity.class);
                startActivity(intent2);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    Takes user to the reminders screen to view their reminders
     */
    public void remindersCarer(View v)
    {
        Intent intent = new Intent(MainCarerScreenActivity.this, RemindersCarerActivity.class);

        startActivityForResult(intent, REMINDERS_REQUEST);
    }

    public void findHospital(View v)
    {
        Intent intent = new Intent(MainCarerScreenActivity.this, NewFindHospitalActivity.class);

        startActivityForResult(intent, FIND_HOSPITAL);
    }

    public void findElder(View view) {

        Intent intent = new Intent(MainCarerScreenActivity.this, FindElderlyActivity.class);
        startActivityForResult(intent, FIND_ELDERLY);
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
}
