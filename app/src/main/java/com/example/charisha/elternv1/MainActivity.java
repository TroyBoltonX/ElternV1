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

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by e-Miracle workers.
 * Registration screen code
 * The first screen which the user comes to after opening app.
 * Asks the the user to log in through this screen or to register for an account
 */

public class MainActivity extends Activity {
    public static String userName = "";
    public static String userType = "";
    public static final int SHOW_REGISTER_REQUEST = 1;
    public static final int LOGIN_REQUEST = 2;
    public static final int FORGOT_PASSWORD_REQUEST = 3;
    private EditText userNameET;
    private EditText passwordET;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userNameET = (EditText) findViewById(R.id.userNameET);
        passwordET = (EditText) findViewById(R.id.passwordET);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //   if (id == R.id.action_settings) {
        //        return true;
        //   }
        return super.onOptionsItemSelected(item);
    }

    /*
    Method executed when register button is pressed
    Method to go to register page
     */
    public void openRegister(View v)
    {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);

        startActivityForResult(intent, SHOW_REGISTER_REQUEST);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //make keyboard disappear
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }
    /*
    Method executed when login button is pressed
    Method to login to app
    The method will take user to either elder or carer interface depending
    on what the user type is (which is determinded using their user id)
     */

    public void login(View v)
    {
        //check if network is available
        if (isNetworkConnected()) {

            //obatin usertype from database
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Elderly");
            query.whereEqualTo("userName", userNameET.getText().toString());

            // Encrypt Password
            String password = passwordET.getText().toString();

            Encryption encryption = new Encryption();
            String hashPassword = encryption.hashPassword(password);

            query.whereEqualTo("password", hashPassword);
            System.out.println("Elderly hashPassword: " + hashPassword);

            query.getFirstInBackground(new GetCallback<ParseObject>() {

                public void done(ParseObject object, ParseException e) {
                    if (object == null) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Carer");
                        query.whereEqualTo("userName", userNameET.getText().toString());

                        // Encrypt Password
                        String password = passwordET.getText().toString();

                        Encryption encryption = new Encryption();
                        String hashPassword = encryption.hashPassword(password);

                        query.whereEqualTo("password", hashPassword);
                        System.out.println("Carer hashPassword: " + hashPassword);

                        query.getFirstInBackground(new GetCallback<ParseObject>() {

                            public void done(ParseObject object, ParseException e) {
                                if (object == null) {
                                    //pop up to hint input is incorrect

                                    //pop up to hint name is already registered
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    // Set message and title for dialog (we can chain methods here!)
                                    builder.setTitle("Incorrect inputs!").setMessage("Please check username and password");
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
                                    MainActivity.userName = userNameET.getText().toString();
                                    MainActivity.userType = "carer";

                                    //save username and usertype to system
                                    SaveSharedPreference.setUserName(MainActivity.this, MainActivity.userName);
                                    SaveSharedPreference.setUserType(MainActivity.this, MainActivity.userType);
                                    //if user is carer the following will be run
                                    Intent intent = new Intent(MainActivity.this, MainCarerScreenActivity.class);

                                    startActivityForResult(intent, LOGIN_REQUEST);

                                    finish();
                                }
                            }
                        });
                    } else {
                        MainActivity.userName = userNameET.getText().toString();
                        MainActivity.userType = "elderly";

                        //save username and usertype to system
                        SaveSharedPreference.setUserName(MainActivity.this, MainActivity.userName);
                        SaveSharedPreference.setUserType(MainActivity.this, MainActivity.userType);

                        //if user is elder the following will be run
                        Intent intent = new Intent(MainActivity.this, MainElderScreenActivity.class);

                        startActivityForResult(intent, LOGIN_REQUEST);

                        finish();
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
    Method executed when forgot password button is pressed
    Takes user to a screen where their password question is shown
    in order to answer correctly to re-set password
     */
    public void forgotPassword(View v)
    {
        Intent intent = new Intent(MainActivity.this, ForgetPasswordUserNameActivity.class);

        startActivityForResult(intent, FORGOT_PASSWORD_REQUEST);
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
}
