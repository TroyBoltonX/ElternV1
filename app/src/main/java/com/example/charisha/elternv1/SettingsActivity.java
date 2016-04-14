package com.example.charisha.elternv1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by e-Miracle workers.
 * Settings activity screen is used by the user to input setting
 * user can add a parent and can chose which parent to care for
 */

public class SettingsActivity extends Activity {

    private EditText userNameET;
    private EditText passwordET;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        userNameET = (EditText) findViewById(R.id.userNameEditText);
        passwordET = (EditText) findViewById(R.id.passwordEditText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        // if (id == R.id.action_settings) {
        //     return true;
        // }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //make keyboard disappear
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    public void createRelationship(View v)
    {
        //check if network is available
        if (isNetworkConnected()) {

            // Encrypt Password
            String password = passwordET.getText().toString();

            Encryption encryption = new Encryption();
            String hashPassword = encryption.hashPassword(password);

            //obatin usertype from database
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Elderly");
            query.whereEqualTo("userName", userNameET.getText().toString());
            query.whereEqualTo("password", hashPassword);

            query.getFirstInBackground(new GetCallback<ParseObject>() {

                public void done(ParseObject object, ParseException e) {
                    if (object == null) {
                        //pop up to hint input is incorrect
                        // Use the AlertDialog Builder utility to create the dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
// Set message and title for dialog (we can chain methods here!)
                        builder.setTitle("Inncorrect inputs").setMessage("Please check Elder's username and password");
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

                    } else {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("ElderlyCarer");
                        query.whereEqualTo("elderlyID", userNameET.getText().toString());
                        query.whereEqualTo("carerID", MainActivity.userName);
                        query.getFirstInBackground(new GetCallback<ParseObject>() {
                            public void done(ParseObject object, ParseException e) {

                                if (object == null) {
                                    ParseObject elderlyCarer = new ParseObject("ElderlyCarer");
                                    elderlyCarer.put("elderlyID", userNameET.getText().toString());
                                    elderlyCarer.put("carerID", MainActivity.userName);

                                    elderlyCarer.saveInBackground();
                                    finish();
                                    Toast.makeText(SettingsActivity.this, "Elder added", Toast.LENGTH_SHORT).show();

                                } else {
                                    //pop up to hint this relationship already exists
                                    // Use the AlertDialog Builder utility to create the dialog
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
// Set message and title for dialog (we can chain methods here!)
                                    builder.setTitle("Elder already added!").setMessage("This elder has already been added");
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
                        });
                    }
                }
            });
        }
        else{
            // Use the AlertDialog Builder utility to create the dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
// Set message and title for dialog (we can chain methods here!)
            builder.setTitle("No connection!").setMessage("Check network connection");
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

    /*
   Check network connection
    */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
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
    Method executed when setUp emergency button is pressed
     */

    public void setupEmergency(View v) {
        Intent intent = new Intent(SettingsActivity.this, SetupEmergencyCallCarerActivity.class);

        startActivity(intent);
    }
}
