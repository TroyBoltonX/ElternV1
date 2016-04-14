package com.example.charisha.elternv1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by e-Miracle workers.
 */

public class ResetPasswordActivity extends Activity {

    private TextView questionText;
    private EditText answerET;
    private EditText newPasswordET;
    private boolean downloadCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        questionText = (TextView)findViewById(R.id.questionText);
        answerET = (EditText)findViewById(R.id.resetAnswerET);
        newPasswordET = (EditText)findViewById(R.id.newPasswordET);

       // questionText.setText(ForgetPasswordUserNameActivity.questionString);

        // if (MainActivity.userType.equals("elderly")) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Elderly");
        query.whereEqualTo("userName", ForgetPasswordUserNameActivity.userName2);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Carer");
                    query2.whereEqualTo("userName", ForgetPasswordUserNameActivity.userName2);
                    query2.getFirstInBackground(new GetCallback<ParseObject>() {
                        public void done(ParseObject object, ParseException e) {
                            if (object == null) {
                                downloadCompleted = true;
                            } else {
                                questionText.setText(object.getString("question"));
                                MainActivity.userType = "carer";
                                downloadCompleted = true;
                            }
                        }
                    });
                } else {
                    questionText.setText(object.getString("question"));
                    MainActivity.userType = "elderly";
                    downloadCompleted = true;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reset_password, menu);
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

    /*
    Method executed when reset password button is pressed
    Method to reset the user password.
    The method checks if the user enterd the correct answer to their secret question
    If the answer is correct the newly entered password replaces old password
    */
    public void resetPassword(View v) {

        //check if network is available
        if (isNetworkConnected()) {

            //code to replace old password and save new password to database
            if (MainActivity.userType.equals("elderly")) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Elderly");
                query.whereEqualTo("userName", ForgetPasswordUserNameActivity.userName2);
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    public void done(ParseObject object, ParseException e) {
                        if (object == null) {
                            Log.d("score", "The getFirst request failed.");

                        } else {

                            //change password
                            String answer1 = object.get("answer").toString();
                            if (answerET.getText().toString().equals(answer1)) {
                                if (newPasswordET.getText().toString().isEmpty() == false &&
                                        newPasswordET.getText().toString().length() >= 6 &&
                                        newPasswordET.getText().toString().length() <= 20) {

                                    // Encrypt Password
                                    String newPassword = newPasswordET.getText().toString();

                                    Encryption encryption = new Encryption();
                                    String hashPassword = encryption.hashPassword(newPassword);

                                    System.out.println("New Elderly hashPassword: " + hashPassword);

                                    object.put("password", hashPassword);

                                    object.saveInBackground();
                                    Toast.makeText(getBaseContext(), "Password changed",
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    //pop up to hint password input is empty.

                                    // Use the AlertDialog Builder utility to create the dialog
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
// Set message and title for dialog (we can chain methods here!)
                                    builder.setTitle("Check input").setMessage("Password should be 6 - 20 characters long");
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
                            } else {
                                //pop up to hint answer is incorrect

                                // Use the AlertDialog Builder utility to create the dialog
                                AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
// Set message and title for dialog (we can chain methods here!)
                                builder.setTitle("Incorrect Answere").setMessage("Please check answere");
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

            //code to replace old password and save new password to database
            if (MainActivity.userType.equals("carer")) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Carer");
                query.whereEqualTo("userName", ForgetPasswordUserNameActivity.userName2);
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    public void done(ParseObject object, ParseException e) {
                        if (object == null) {
                            Log.d("score", "The getFirst request failed.");

                        } else {
                            //change password
                            String answer1 = object.get("answer").toString();
                            if (answerET.getText().toString().equals(answer1)) {
                                if (newPasswordET.getText().toString().isEmpty() == false &&
                                        newPasswordET.getText().toString().length() >= 6 &&
                                        newPasswordET.getText().toString().length() <= 20) {

                                    // Encrypt Password
                                    String newPassword = newPasswordET.getText().toString();

                                    Encryption encryption = new Encryption();
                                    String hashPassword = encryption.hashPassword(newPassword);

                                    System.out.println("New Carer hashPassword: " + hashPassword);

                                    object.put("password", hashPassword);

                                    object.saveInBackground();
                                    Toast.makeText(getBaseContext(), "Password changed",
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    //pop up to hint password input is empty.
                                    // Use the AlertDialog Builder utility to create the dialog
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
// Set message and title for dialog (we can chain methods here!)
                                    builder.setTitle("Check input").setMessage("Password should be 6 - 20 characters long");
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
                            } else {
                                //pop up to hint answer is incorrect

                                // Use the AlertDialog Builder utility to create the dialog
                                AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
// Set message and title for dialog (we can chain methods here!)
                                builder.setTitle("Incorrect Answere").setMessage("Please check answere");
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
