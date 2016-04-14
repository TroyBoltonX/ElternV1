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
import android.view.View;
import android.widget.EditText;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by e-Miracle workers.
 */

public class ForgetPasswordUserNameActivity extends Activity {

    public static String userName2 = "";
    public static final int CHANGE_PASSWORD = 1;
    private EditText userNameET;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_user_name);
        userNameET = (EditText) findViewById(R.id.userNameEditText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_forget_password_user_name, menu);
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

    public void forgetAndChange(View v)
    {
        //check if network is available
        if (isNetworkConnected()) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Elderly");
            query.whereEqualTo("userName", userNameET.getText().toString());
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (object == null) {
                        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Carer");
                        query2.whereEqualTo("userName", userNameET.getText().toString());
                        query2.getFirstInBackground(new GetCallback<ParseObject>() {
                            public void done(ParseObject object, ParseException e) {
                                if (object == null) {
                                    //pop up to hint the username is incorrect.
                                    // Use the AlertDialog Builder utility to create the dialog
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPasswordUserNameActivity.this);
// Set message and title for dialog (we can chain methods here!)
                                    builder.setTitle("Username does not exist.").setMessage("Please check Username");
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

                                    ForgetPasswordUserNameActivity.userName2 = userNameET.getText().toString();
                                    Intent intent = new Intent(ForgetPasswordUserNameActivity.this, ResetPasswordActivity.class);

                                    startActivityForResult(intent, CHANGE_PASSWORD);
                                    finish();
                                }
                            }
                        });
                    } else {
                        ForgetPasswordUserNameActivity.userName2 = userNameET.getText().toString();
                        Intent intent = new Intent(ForgetPasswordUserNameActivity.this, ResetPasswordActivity.class);

                        startActivityForResult(intent, CHANGE_PASSWORD);
                        finish();
                    }
                }
            });
        }
        else{
            // Use the AlertDialog Builder utility to create the dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPasswordUserNameActivity.this);
// Set message and title for dialog (we can chain methods here!)
            builder.setTitle("No network connectivity").setMessage("Please check network connection");
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
}
